package mlp;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.harness.junit.Neo4jRule;

public class MLPTest {
    // This rule starts a Neo4j instance
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withProcedure(mlp.Execute.class)
            .withProcedure(TestTask.class)
            .withProcedure(mnist.Execute.class);

    @Test
    public void completeTest() throws Throwable {
        // This is in a try-block, to make sure we close the driver after the test
        // In a try-block, to make sure we close the driver and session after the test
        try(Driver driver = GraphDatabase.driver( neo4j.boltURI() , Config.build()
                .withEncryptionLevel( Config.EncryptionLevel.NONE ).toConfig() );
            Session session = driver.session() )
        {

            session.run("call mlp.proto1test1(1);");

        }
    }

    @Test
    public void forwardPass() throws Exception {

        try (Driver driver = GraphDatabase.driver( neo4j.boltURI() , Config.build()
                .withEncryptionLevel( Config.EncryptionLevel.NONE ).toConfig() );
            Session session = driver.session() ) {

            session.run("call mlp.connect");
        }

    }

    @Test
    public void MINSTtest() throws Exception {

        try (Driver driver = GraphDatabase.driver( neo4j.boltURI() , Config.build()
                .withEncryptionLevel( Config.EncryptionLevel.NONE ).toConfig() );
             Session session = driver.session() ) {

            session.run("call mnist.load(1000)");
            session.run("call mlp.testMNIST(990,10)");
        }

    }

}