package example;

import mlp.Classifier;
import mlp.Execute;
import mlp.GraphGenerator;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Pete Meltzer on 19/10/17.
 */
public class Random {

    // This rule starts a Neo4j instance
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()

            // This is the function we want to test
            .withProcedure( GraphGenerator.class )
            .withProcedure( Classifier.class );

    @Test
    public void testingXOR() throws Throwable {

        SplittableRandom random = new SplittableRandom();

        for (int i = 0; i < 10; i++) {

            int a = random.nextInt(2);
            int b = random.nextInt(2);
            int c = a ^ b;

            System.out.println(String.format("a: %d, b:%d, c:%d", a, b, c));
        }
    }

    @Test
    public void streamTest() throws Throwable {

        Stream<String> stream = Stream.of("red", "blue", "white");
        Stream<String> upper = stream
                .map(String::toUpperCase)
                .peek(System.out::println);
        upper.collect(Collectors.toList());
    }

    @Test
    public void completeTest() throws Throwable {
        // This is in a try-block, to make sure we close the driver after the test
        // In a try-block, to make sure we close the driver and session after the test
        try(Driver driver = GraphDatabase.driver( neo4j.boltURI() , Config.build()
                .withEncryptionLevel( Config.EncryptionLevel.NONE ).toConfig() );
            Session session = driver.session() )
        {

            session.run("match (n) detach delete n;");
            session.run("call mlp.XOR(1000);");
            session.run("call mlp.createMLP();");
            session.run("call mlp.attach();");
            session.run("call mlp.train(100000);");
            System.out.println("done");

        }
    }

}
