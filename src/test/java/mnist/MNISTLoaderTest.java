package mnist;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.harness.junit.Neo4jRule;

public class MNISTLoaderTest {

    // This rule starts a Neo4j instance
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withProcedure(Execute.class);

    @Test
    public void testNextImage() throws Exception {

        MNISTLoader mnistLoader = new MNISTLoader();

        int count = 0;

        while (mnistLoader.hasNext() && count++ < 10) {

            MNISTLoader.Example example = mnistLoader.nextExample();
            int[] image = example.getImage();
            int target = example.getTarget();
            System.out.print("Target: " + target);

            for (int i = 0; i < 28 * 28; i++){
                if (i % 28 == 0) System.out.println();
                System.out.print(image[i] > 128 ? "@ " : "- ");
            }
            System.out.println("\n");
        }

    }

    @Test
    public void testLoad() throws Throwable {
        // This is in a try-block, to make sure we close the driver after the test
        // In a try-block, to make sure we close the driver and session after the test
        try(Driver driver = GraphDatabase.driver( neo4j.boltURI() , Config.build()
                .withEncryptionLevel( Config.EncryptionLevel.NONE )
                .toConfig() );
            Session session = driver.session() )
        {
            GraphDatabaseService db = neo4j.getGraphDatabaseService();

            db.execute("call mnist.load(1000);");
            String result = db.execute("match (n) return n;").resultAsString();
            System.out.println(result);
        }
    }
}