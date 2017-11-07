package mlp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.procedure.*;

import static mlp.Constants.dataFields.TARGET;
import static mlp.Constants.labels.SET;
import static mlp.Constants.relationshipTypes.*;
import static org.neo4j.procedure.Mode.SCHEMA;

/**
 * Created by Pete Meltzer on 30/10/17.
 */
public class GraphGenerator {

    @Context
    public GraphDatabaseService db;

    @Procedure(value = "mlp.XOR", mode = SCHEMA)
    @Description("Creates an XOR graph")
    public void xor( @Name("Number of sets") long nSets) {

        if (nSets == 0) return;

        Node current = createXORSet();
        Node first = current;

        for (int i = 1; i < nSets; i++) {

            Node next = createXORSet();
            current.createRelationshipTo(next, NEXT);
            current = next;
        }

        current.createRelationshipTo(first, NEXT);

    }

    private Node createXORSet() {
        Node set = db.createNode(SET);
        Node nodeA = db.createNode();
        Node nodeB = db.createNode();

        set.createRelationshipTo(nodeA, CONTAINS);
        set.createRelationshipTo(nodeB, CONTAINS);

        if (Math.random() > 0.5) nodeA.createRelationshipTo(nodeB, REL);
        if (Math.random() > 0.5) nodeB.createRelationshipTo(nodeA, REL);

        if (nodeA.getDegree() % 2 == 0) set.setProperty(TARGET.toString(), 0.0);
        else set.setProperty(TARGET.toString(), 1.0);

        return set;
    }
}
