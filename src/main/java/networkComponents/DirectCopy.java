package networkComponents;

import org.neo4j.graphdb.Relationship;

import static networkComponents.Constants.DATA;
import static networkComponents.Constants.OUTPUT;
import static networkComponents.RelationshipTypes.IN;
import static org.neo4j.graphdb.Direction.OUTGOING;

public class DirectCopy extends ComputeBehaviour {

    @Override
    public double compute() {
//        System.out.print("computing direct copy:");

        double output;

        Relationship rel = node.getSingleRelationship(IN, OUTGOING);

        if (rel == null) throw new RuntimeException("DirectCopy: No input node.");

        output = (double) (int) node.getSingleRelationship(IN, OUTGOING)
                                            .getEndNode().getProperty(DATA);

        node.setProperty(OUTPUT, output);

//        System.out.println(" " + output);

        return output;
    }
}
