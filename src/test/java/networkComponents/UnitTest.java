package networkComponents;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;
import mlp.MLP;

import static networkComponents.Constants.DATA;
import static networkComponents.Constants.VALUE;
import static networkComponents.UnitLabels.TARGET;

public class UnitTest {

    // This rule starts a Neo4j instance
    @Rule
    public Neo4jRule neo4j = new Neo4jRule();

    @Test
    public void test() throws Throwable {
        // This is in a try-block, to make sure we close the driver after the test
        // In a try-block, to make sure we close the driver and session after the test
        try(Driver driver = GraphDatabase.driver( neo4j.boltURI() , Config.build()
                .withEncryptionLevel( Config.EncryptionLevel.NONE )
                .toConfig() );
            Session session = driver.session() )
        {

            GraphDatabaseService db = neo4j.getGraphDatabaseService();
            UnitFactory unitFactory = MLPUnitFactory.get();

            try (Transaction tx = db.beginTx()){

                Node[] nodes = new Node[2];
                for (int i = 0; i < 2; i++) {
                    nodes[i] = db.createNode();
                    nodes[i].setProperty(DATA, 1.0);
                }

                Node targetNode = db.createNode(TARGET);
                targetNode.setProperty(VALUE, 1.0);

                // create network
                MLP MLP = new MLP(2, 2, 1, db);
                MLP.connect(targetNode, nodes);

                db.getAllNodes().stream()
                        .filter(unitFactory::isUnit)
                        .map(unitFactory::getUnit)
                        .forEach(Unit::initialize);

                db.getAllNodes().stream()
                        .filter(unitFactory::isUnit)
                        .map(unitFactory::getUnit)
                        .peek(System.out::println)
                        .forEach(Unit::compute);

                System.out.println("\n--- TRAINING ---");

                db.getAllNodes().stream()
                        .filter(unitFactory::isTrainable)
                        .map(unitFactory::getTrainableUnit)
                        .peek(System.out::println)
                        .forEach(trainableUnit -> trainableUnit.updateWeights((double) targetNode.getProperty(VALUE)));

            }

        }
    }

}