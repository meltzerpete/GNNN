package networkComponents;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;

import static networkComponents.Constants.*;
import static networkComponents.RelationshipTypes.WEIGHT;
import static org.neo4j.graphdb.Direction.INCOMING;

public class Sigmoid extends ComputeBehaviour {


    @Override
    public double compute() {
//        System.out.print("computing sigmoid: ");

        Double activation = ((ResourceIterator<Relationship>)
                node.getRelationships(INCOMING, WEIGHT).iterator()).stream()
                .map(relationship -> {
                    Node prev = relationship.getStartNode();
                    double prevY = (double) prev.getProperty(OUTPUT);
                    double weight = (double) relationship.getProperty(VALUE);
                    return prevY * weight;
                })
                .reduce(Double::sum)
                .orElseThrow(() -> new RuntimeException("Error in Sigmoid.compute() - reduce()"));

        double squashed = sigmoid(activation);

        node.setProperty(ACTIVATION, activation);
        node.setProperty(SQAUSHED, squashed);
        node.setProperty(OUTPUT, squashed);

//        System.out.println(activation + " -> " + squashed);

        return squashed;
    }

    public double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
