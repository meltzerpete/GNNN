package networkComponents;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;

import java.util.concurrent.ThreadLocalRandom;

import static networkComponents.Constants.*;
import static networkComponents.RelationshipTypes.WEIGHT;
import static org.neo4j.graphdb.Direction.INCOMING;

public abstract class TrainableUnit extends Unit {

    TrainBehaviour trainBehaviour;

    TrainableUnit(ComputeBehaviour computeBehaviour, TrainBehaviour trainBehaviour, Node node) {
        super(computeBehaviour, node);

        this.trainBehaviour = trainBehaviour;
        trainBehaviour.setNode(node);
    }

        public void updateWeights(double target) {
        trainBehaviour.updateWeights(target);
//        System.out.println(getWeightsAsString());
    }

    @Override
    public void initialize() {
        setProperty(ACTIVATION, 0.0);
        setProperty(SQAUSHED, 0.0);
        setProperty(OUTPUT, 0.0);
        setProperty(DELTA, 0.0);
        initializeWeights();
    }

    private void initializeWeights() {
        getRelationships(INCOMING, WEIGHT).forEach(
                relationship -> relationship.setProperty(VALUE, randomWeight()));
    }

    private double randomWeight() {
        return ThreadLocalRandom.current().nextDouble(0, 0.2) - 0.1;
    }

    @Override
    public String toString() {

        return super.toString() + "\n" + getWeightsAsString();
    }

    public String getWeightsAsString() {
        String template = "[(%d) %f]";

        return "Weights: " +
                ((ResourceIterator<Relationship>)
                        getRelationships(INCOMING, WEIGHT).iterator()).stream()
                        .map(relationship ->
                                String.format(template,
                                        relationship.getStartNode().getId(),
                                        relationship.getProperty(VALUE)))
                        .reduce((acc, next) -> String.join(", ", acc, next)).orElse("");
    }
}
