package mlp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import java.util.ArrayList;
import java.util.LinkedList;

import static mlp.Constants.dataFields.*;
import static mlp.Constants.eta_0;
import static mlp.Constants.labels.*;
import static mlp.Constants.lambda;
import static mlp.Constants.relationshipTypes.*;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;
import static org.neo4j.procedure.Mode.SCHEMA;
import static org.neo4j.procedure.Mode.WRITE;

/**
 * Created by Pete Meltzer on 30/10/17.
 */
public class Classifier {

    private static double eta = eta_0;

    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    @Procedure(value = "mlp.createMLP", mode = SCHEMA)
    @Description("Creates a 2-2-1 MLP")
    public void createMLP() {

        Node finalNode = db.createNode(FINAL, NEURON);
        initializeNode(finalNode);

        Node[] hiddenNodes = {db.createNode(HIDDEN, NEURON), db.createNode(HIDDEN, NEURON), db.createNode(BIAS, NEURON)};
        Node[] inputNodes = {db.createNode(INPUT, NEURON), db.createNode(INPUT, NEURON), db.createNode(BIAS, NEURON)};

        Relationship relationship;

        for (Node hiddenNode : hiddenNodes) {
            initializeNode(hiddenNode);
            relationship = hiddenNode.createRelationshipTo(finalNode, LINK);
            initializeLink(relationship);

            for (Node inputNode : inputNodes) {
                if (!hiddenNode.hasLabel(BIAS)) {
                    relationship = inputNode.createRelationshipTo(hiddenNode, LINK);
                    initializeLink(relationship);
                } else {
                    initializeNode(inputNode);
                }
            }
        }
    }

    private void initializeLink(Relationship relationship) {
        relationship.setProperty(WEIGHT.toString(),
                (Math.random() - 0.5) / 100);
    }

    private void initializeNode(Node node) {

        if (node.hasLabel(BIAS)) {
            node.setProperty(FA.toString(), 1.0);
            node.setProperty(Y.toString(), 1.0);
        } else {

            node.setProperty(A.toString(), 0.0);
            node.setProperty(FA.toString(), 0.0);
            node.setProperty(Y.toString(), 0.0);
//            node.setProperty(FEEDBACK.toString(), 0.5);
        }

    }

    private void setInput(Node node) {
        int degree = node.getSingleRelationship(IN, OUTGOING)
                            .getEndNode().getDegree();

        node.setProperty(Y.toString(), (double) degree - 2);
        node.setProperty(FA.toString(), (double) degree - 2);
    }

    @Procedure(value = "mlp.attach", mode = SCHEMA)
    @Description("Attaches MLP to random set")
    public void attach() {

        Node set = db.findNodes(SET).stream().findAny().get();
        set.addLabel(CURRENTSET);

        ArrayList<Node> dataNodes = new ArrayList<>();
        ArrayList<Node> inputNodes = new ArrayList<>();

        ((ResourceIterator<Relationship>)
                set.getRelationships(CONTAINS, OUTGOING)
                    .iterator()).stream()
                        .map(Relationship::getEndNode)
                        .forEach(dataNodes::add);

        db.findNodes(INPUT).stream().forEach(inputNodes::add);

        for (int i = 0; i < inputNodes.size(); i++) {
            inputNodes.get(i).createRelationshipTo(dataNodes.get(i), IN);
            setInput(inputNodes.get(i));
        }
    }

    @Procedure(value = "mlp.moveNext", mode = SCHEMA)
    @Description("Moves MLP to the next set")
    public void moveNext() {

        Node oldSet = db.findNodes(CURRENTSET).next();
        oldSet.removeLabel(CURRENTSET);

        Node nextSet = oldSet.getRelationships(OUTGOING, NEXT).iterator().next()
                .getEndNode();

        nextSet.addLabel(CURRENTSET);

        db.findNodes(INPUT).stream()
                .forEach(node -> {

                    Node target = ((ResourceIterator<Relationship>)
                            nextSet.getRelationships(OUTGOING, CONTAINS).iterator()).stream()
                            .map(Relationship::getEndNode)
                            .filter(node1 -> !node1.hasRelationship(INCOMING, IN))
                            .findFirst()
                            .get();

                    node.getSingleRelationship(IN, OUTGOING).delete();
                    node.createRelationshipTo(target, IN);
                    setInput(node);
                });
    }

    @Procedure(value = "mlp.forwardPass", mode = WRITE)
    @Description("Forward pass through NN")
    public void forwardPass() {

        db.findNodes(HIDDEN).stream()
                .filter(node -> !node.hasLabel(BIAS))
                .forEach(this::forwardCompute);

        db.findNodes(FINAL).stream().forEach(this::forwardCompute);
    }

    private void forwardCompute(Node node) {

        Double activation = ((ResourceIterator<Relationship>)
                node.getRelationships(INCOMING, LINK).iterator()).stream()
                .map(relationship -> {
                    Node prev = relationship.getStartNode();
                    double prevY = (double) prev.getProperty(Y.toString());
                    double weight = (double) relationship.getProperty(WEIGHT.toString());
                    return prevY * weight;
                })
                .reduce(Double::sum)
                .get();

        node.setProperty(A.toString(), activation);
        node.setProperty(FA.toString(), sigmoid(activation));
        node.setProperty(Y.toString(), sigmoid(activation) > 0.5 ? 1.0 : 0.0);
    }

    public void backwardPass(double reward) {

        Node outputNode = db.findNodes(FINAL).next();

        updateWeights(outputNode, reward);

        ((ResourceIterator<Relationship>) outputNode.getRelationships(INCOMING, LINK).iterator())
                .stream()
                .map(Relationship::getStartNode)
                .forEach(node -> updateWeights(node, reward));

    }

    private void updateWeights(Node node, double reward) {

        // calculate delta
        double y = (double) node.getProperty(Y.toString());
        double fa = (double) node.getProperty(FA.toString());
        double delta;

        if (reward == 1) {
            // increase probability of same action
            delta = y == 0 ? (- fa) : (1 - fa);

        } else {
            // otherwise, increase probability of opposite action
            delta = y == 0 ? (lambda * (1 - fa)) : ((- lambda) * fa);
        }

        // update weight for each link into this node
        node.getRelationships(INCOMING, LINK).forEach(relationship -> {

//            System.out.println(String.format(
//                    "updating node: %d, rel: %d",
//                    node.getId(), relationship.getId()));

            // change in weight = eta * delta * x (output of previous layer node)
            double x = (double) relationship.getStartNode().getProperty(Y.toString());
            double deltaW = eta * delta * x;
            double oldW = (double) relationship.getProperty(WEIGHT.toString());

            relationship.setProperty(WEIGHT.toString(), oldW + deltaW);

        });

    }

    private void updateTrainRate(double t) {
        eta = eta_0 / (Math.pow(t, 0.55));
    }

public double sigmoid(double x) {
    return 1 / (1 + Math.exp(-x));
}

    @Procedure(value = "mlp.train", mode = WRITE)
    @Description("Backward pass through NN")
    public void train(@Name(value = "no. of passes") long nPasses) {

//        LinkedList<Double> errors = new LinkedList<>();

        DataLogger logger = new DataLogger("proto1", "error");

        for (int i = 0; i < nPasses; i++) {
            updateTrainRate(i + 1);
            forwardPass();

            double error = calculateError();

            double reward = 1.0 - error;

//            if (Math.random() > 0.9) reward = error;

            backwardPass(reward);
            moveNext();


//            System.out.println(String.format("Error: %f", error));
//            if (i % 100 == 0 && i > 0) {
//                double meanError = errors.stream().reduce(0.0, Double::sum) / 100;
//                System.out.println(String.format(
//                        "Mean error: %f, eta: %f", meanError, eta));
//                errors.clear();
//            }
//            errors.add(error);
            logger.append(error);


        }

        forwardPass();
        logger.close();
    }

    private double calculateError() {
        double error =
                (double) db.findNodes(CURRENTSET).next().getProperty(TARGET.toString()) -
                (double) db.findNodes(FINAL).next().getProperty(Y.toString());

        return Math.abs(error);
    }
}
