package seTryout;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;

import static org.neo4j.graphdb.Direction.INCOMING;
import static seTryout.Constants.*;
import static seTryout.RelationshipTypes.WEIGHT;

public class Sigmoid extends ComputeBehaviour {


    @Override
    public double compute() {


        System.out.println("computing sigmoid");
        return 0.0;
//        Double activation = ((ResourceIterator<Relationship>)
//                node.getRelationships(INCOMING, WEIGHT).iterator()).stream()
//                .map(relationship -> {
//                    Node prev = relationship.getStartNode();
//                    double prevY = (double) prev.getProperty(OUTPUT);
//                    double weight = (double) relationship.getProperty(VALUE);
//                    return prevY * weight;
//                })
//                .reduce(Double::sum)
//                .get();
//
//        double squashed = sigmoid(activation);
//
//        node.setProperty(ACTIVATION, activation);
//        node.setProperty(SQAUSHED, squashed);
//        node.setProperty(OUTPUT, squashed);
//
//        return squashed;
    }

    public double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
