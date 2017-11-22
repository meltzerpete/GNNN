package seTryout;

import static org.neo4j.graphdb.Direction.INCOMING;
import static seTryout.Constants.DATA;
import static seTryout.RelationshipTypes.IN;

public class DirectCopy extends ComputeBehaviour {

    @Override
    public double compute() {
        System.out.println("computing direct copy");
        return 0;
//        return (double) node.getSingleRelationship(IN, INCOMING).getEndNode().getProperty(DATA);
    }
}
