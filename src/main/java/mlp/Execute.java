package mlp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Procedure;

import static org.neo4j.procedure.Mode.SCHEMA;

/**
 * Created by Pete Meltzer on 30/10/17.
 */
public class Execute {

    @Context
    public GraphDatabaseService db;

    @Procedure(value = "mlp.completeTest", mode = SCHEMA)
    public void completeTest() {

        db.execute("match (n) detach delete n;");
        db.execute("call mlp.XOR(1000);");
        db.execute("call mlp.createMLP();");
        db.execute("call mlp.attach();");
        db.execute("call mlp.train(100000);");
    }
}
