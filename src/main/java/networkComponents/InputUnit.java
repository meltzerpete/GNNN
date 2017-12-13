package networkComponents;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import static networkComponents.Constants.OUTPUT;
import static networkComponents.RelationshipTypes.IN;
import static networkComponents.RelationshipTypes.NEXT;

public class InputUnit extends Unit {

    InputUnit(ComputeBehaviour computeBehaviour, Node node) {
        super(computeBehaviour, node);
    }

    @Override
    public void initialize() {
        setProperty(OUTPUT, 0.0);
    }

    public void next() {
        Relationship rel = node.getSingleRelationship(IN, Direction.OUTGOING);
        Node oldNode = rel.getEndNode();
        rel.delete();
        Node newNode = oldNode.getSingleRelationship(NEXT, Direction.OUTGOING).getEndNode();
        node.createRelationshipTo(newNode, IN);
    }
}
