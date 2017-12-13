package networkComponents;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;

import static mlp.MLP.ID;
import static networkComponents.Constants.*;
import static networkComponents.RelationshipTypes.WEIGHT;
import static networkComponents.UnitLabels.HIDDEN;
import static networkComponents.UnitLabels.TARGET;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;

public class Backpropagation extends TrainBehaviour {

    double eta = 0.1;

    @Override
    void updateWeights(double target) {
        double output = (double) node.getProperty(OUTPUT);

//        String template = "Training with BP - UnitID: %d, Output: %f, Target: %f";
//        System.out.println(String.format(template, node.getProperty(ID), output, target));

        // calculate delta
        double delta = calculateDelta(output, target);
        node.setProperty(DELTA, delta);

        // update weights
        node.getRelationships(INCOMING, WEIGHT).forEach(relationship -> {

            double change = eta * delta * (double)
                    relationship.getStartNode().getProperty(OUTPUT);

            double oldValue = (double) relationship.getProperty(VALUE);
            double newValue = oldValue + change;

            relationship.setProperty(VALUE, newValue);
        });

    }

    private double calculateDelta(double output, double target) {

        double delta;

        if (node.hasLabel(UnitLabels.OUTPUT)) {

            delta = (1 - output) * output * (target - output);

        } else if (node.hasLabel(HIDDEN)) {

            double weightedSum =
                    ((ResourceIterator<Relationship>)
                            node.getRelationships(OUTGOING, WEIGHT).iterator()).stream()
                    .map(relationship -> {
                        double deltaAbove = (double) relationship.getEndNode().getProperty(DELTA);
                        double weight = (double) relationship.getProperty(VALUE);
                        return deltaAbove * weight;
                    })
                    .reduce(Double::sum)
                    .orElseThrow(() -> new RuntimeException("Error in Backpropagation.calculateDelta() - reduce()"));

            delta = (1 - output) * output * weightedSum;

        } else {
            throw new RuntimeException("Not a TrainableUnit - cannot apply Backpropagation.calculateDelta()");
        }

        node.setProperty(DELTA, delta);

        return delta;
    }
}
