package seTryout;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.kernel.impl.logging.LogService;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.logging.Log;
import org.neo4j.ogm.model.Node;
import org.neo4j.procedure.Context;

import java.util.stream.Stream;

import static seTryout.UnitLabels.*;

public class UnitTest {

    // This rule starts a Neo4j instance
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withFunction( UnitFactory.class );

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

            try (Transaction tx = db.beginTx()){

                Stream.of(BIAS, HIDDEN, OUTPUT, INPUT)
                        .peek(System.out::println)
                        .map(db::createNode)
                        .map(MLPUnitFactory.get()::getUnit)
                        .peek(Unit::compute)
                        .filter(TrainableUnit.class::isInstance)
                        .map(TrainableUnit.class::cast)
                        .forEach(TrainableUnit::updateWeights);

            }

        }
    }

}