package networkComponents;

import org.neo4j.graphdb.Relationship;

import static networkComponents.Constants.DATA;
import static networkComponents.Constants.OUTPUT;
import static networkComponents.RelationshipTypes.IN;
import static org.neo4j.graphdb.Direction.OUTGOING;

public class Scale extends ComputeBehaviour {

    private double scaleFactor;

    public Scale(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public double compute() {
//        System.out.print("computing direct copy:");

        double output;

        Relationship rel = node.getSingleRelationship(IN, OUTGOING);

        if (rel == null) throw new RuntimeException("Scale: No input node.");

        output = (double) (int) node.getSingleRelationship(IN, OUTGOING)
                .getEndNode().getProperty(DATA) / scaleFactor;

        node.setProperty(OUTPUT, output);

//        System.out.println(" " + output);

        return output;
    }
}
