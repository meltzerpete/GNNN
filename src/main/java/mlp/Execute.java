package mlp;

import networkComponents.UnitLabels;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import static networkComponents.Constants.DATA;

public class Execute {

    @Context
    public GraphDatabaseService db;

    @Procedure(value = "mlp.connect", mode = Mode.SCHEMA)
    public void connect() {

        MLP mlp = new MLP(5, 3, 5, db);
        mlp.initialize();

        Node t = db.createNode(UnitLabels.TARGET);
        Node[] d = new Node[5];

        for (int i = 0; i < d.length; i++) {
            d[i] = db.createNode(mnist.Execute.Labels.DATA);
            d[i].setProperty(DATA, Math.random());
        }

        mlp.connect(t, d);

        double[] targets = {1.0, 1.0, 1.0, 0.0, 0.0};
        long nPatterns = 10000;
        long nOutputs = 5;

        double error;
        for (int i = 0; i < nPatterns; i++) {
            mlp.forwardPass();
            mlp.backwardPass(targets);
            error = mlp.getMeanSquaredError(nPatterns, nOutputs, targets);
            System.out.printf("Root mean squared error: %f\n", Math.sqrt(error));
        }
    }

    @Procedure(value = "mlp.testMNIST", mode = Mode.SCHEMA)
    public void testMNIST(@Name("nTrain") long nTrain, @Name("nTest") long nTest) {

        Thread worker = new Thread(new TestTask(nTrain, nTest, db));
        worker.setDaemon(true);
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}