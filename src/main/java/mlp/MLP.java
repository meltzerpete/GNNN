package mlp;

import networkComponents.*;
import org.apache.commons.lang.ArrayUtils;
import org.neo4j.graphdb.*;
import scala.Array$;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static networkComponents.RelationshipTypes.IN;
import static networkComponents.RelationshipTypes.NEXT;
import static networkComponents.RelationshipTypes.WEIGHT;
import static networkComponents.UnitLabels.*;

public class MLP {

    public static final String ID = "unitID";

    private GraphDatabaseService db;

    public Node getCurrentTarget() {
        return currentTarget;
    }

    private Node currentTarget;

    public MLP(long nInput, long nHidden, long nOutput, GraphDatabaseService db) {
        this.db = db;

        Node[] biasNodes = new Node[2];
        for (int i = 0; i < 2; i++) {
            biasNodes[i] = db.createNode(UNIT, BIAS);
            biasNodes[i].setProperty(ID, i);
        }

        List<Node> outputNodes = new LinkedList<>();
        for (long i = 0; i < nOutput; i++) {
            Node node = db.createNode(UNIT, OUTPUT);
            node.setProperty(ID, i);
            outputNodes.add(node);
        }

        List<Node> hiddenNodes = new LinkedList<>();
        for (long i = 0; i < nHidden; i++) {
            Node node = db.createNode(UNIT, HIDDEN);
            node.setProperty(ID, i);
            hiddenNodes.add(node);
        }

        List<Node> inputNodes = new LinkedList<>();
        for (long i = 0; i < nInput; i++) {
            Node node = db.createNode(UNIT, INPUT);
            node.setProperty(ID, i);
            inputNodes.add(node);
        }

        // connect input units to hidden layer
        hiddenNodes.forEach(hidden -> {
            biasNodes[0].createRelationshipTo(hidden, WEIGHT);
            inputNodes.forEach(input ->
                input.createRelationshipTo(hidden, WEIGHT));
        });

        // connect hidden units to output layer
        outputNodes.forEach(output -> {
            biasNodes[1].createRelationshipTo(output, WEIGHT);
            hiddenNodes.forEach(hidden ->
                hidden.createRelationshipTo(output, WEIGHT));
        });
    }

    public void initialize() {
        db.findNodes(UNIT).stream()
                .map(MLPUnitFactory.get()::getUnit)
                .forEach(Unit::initialize);
    }

    public void connect(Node targetNode, Node... inputNodes) {
        if (inputNodes.length != db.findNodes(INPUT).stream().count())
            throw new RuntimeException("Wrong number of inputs!");

        Iterator<Node> inputLayerNodes = db.findNodes(INPUT).stream().sorted(byUnitID).iterator();

        Node current;
        for (int i = 0; i < inputNodes.length; i++) {
            current = inputLayerNodes.next();

            Relationship rel = current.getSingleRelationship(IN, Direction.OUTGOING);
            if (rel != null) rel.delete();

            current.createRelationshipTo(inputNodes[i], IN);
        }

        currentTarget = targetNode;
    }

    public void nextInput() {

        db.findNodes(INPUT).stream()
                .map(MLPUnitFactory.get()::getUnit)
                .map(InputUnit.class::cast)
                .forEach(InputUnit::next);

        currentTarget = currentTarget.getSingleRelationship(NEXT, Direction.OUTGOING).getEndNode();
    }

    Comparator<Node> byUnitID = Comparator.comparingLong(a -> (long) a.getProperty(ID));

    public double getMeanSquaredError(long nPatterns, long nOutputs, double... targets) {

        DoubleStream targetStream = Arrays.stream(targets);
        PrimitiveIterator.OfDouble iterator = targetStream.iterator();

        double totalError = db.findNodes(OUTPUT).stream()
                .sorted(byUnitID)
                .map(MLPUnitFactory.get()::getUnit)
                .map(OutputUnit.class::cast)
                .map(outputUnit -> outputUnit.getSquaredError(iterator.next()))
                .reduce(Double::sum)
                .orElseThrow(() -> new RuntimeException("getMeanSquaredError() failled - probably couldn't find OUTPUT nodes"));

        return totalError / (nPatterns * nOutputs);
    }

    public void forwardPass() {

        Stream.of(INPUT, HIDDEN, OUTPUT)
                .map(unitLabels -> db.findNodes(unitLabels).stream())
                .flatMap(Function.identity())
                .map(MLPUnitFactory.get()::getUnit)
                .forEach(Unit::compute);
    }

    public void backwardPass(double... targets) {

        DoubleStream targetStream = Arrays.stream(targets);
        PrimitiveIterator.OfDouble iterator = targetStream.iterator();

        db.findNodes(OUTPUT).stream()
                .sorted(byUnitID)
                .map(MLPUnitFactory.get()::getTrainableUnit)
                .forEach(trainableUnit -> trainableUnit.updateWeights(iterator.next()));

        db.findNodes(HIDDEN).stream()
                .sorted(byUnitID)
                .map(MLPUnitFactory.get()::getTrainableUnit)
                .forEach(trainableUnit -> trainableUnit.updateWeights(-1)); // target is not used for hidden units
    }

    public int getGuess() {

        AtomicInteger value = new AtomicInteger(-1);
        AtomicInteger index = new AtomicInteger(0);
        db.findNodes(OUTPUT).stream()
                .sorted(byUnitID)
                .map(MLPUnitFactory.get()::getUnit)
                .map(Unit::compute)
                .peek(System.out::println)
                // not sure why i've used a reduce() for this?? - this could probably look much simpler
                .reduce(0.0, (acc, next) -> {
                    int current = index.getAndIncrement();
                    if (next > acc) {
                        value.set(current);
                        return next;
                    } else return acc;
                });

        if (value.get() == -1) throw new RuntimeException("getGuess() failed - this should never happen");

        return value.get();
    }
}
