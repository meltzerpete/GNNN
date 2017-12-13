package mlpOld;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import static org.neo4j.procedure.Mode.SCHEMA;

/**
 * Created by Pete Meltzer on 30/10/17.
 */
@Deprecated
public class Execute {

    @Context
    public GraphDatabaseService db;

    @Procedure(value = "mlp.proto1test1", mode = SCHEMA)
    public void proto1test1(@Name(value="no. of trials") long nTrials) {

        for (int i = 0; i < nTrials; i ++) {
            System.out.printf("\rTrial: %d", i + 1);
            db.execute("match (n) detach delete n;");
            db.execute("call mlp.XOR(1000);");
            db.execute("call mlp.createMLP();");
            db.execute("call mlp.attach();");
            db.execute("call mlp.train(100000);");
        }
        System.out.println("\nTest Complete.");
    }
}
