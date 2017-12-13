package mlp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.List;

import static mnist.Execute.Labels.START;
import static mnist.Execute.Labels.TARGET;
import static networkComponents.Constants.DATA;

public class TestTask implements Runnable {

    private GraphDatabaseService db;
    long nTrain;

    public TestTask(long nTrain, long nTest, GraphDatabaseService db) {
        this.db = db;
        this.nTrain = nTrain;
        this.nTest = nTest;
    }

    long nTest;

    @Override
    public void run() {

        /* create and initialize MLP */

        MLP mlp;

        try (Transaction tx = db.beginTx()) {

            System.out.println("TestTask.run() - starting");
            int nInputs = 28 * 28;

            mlp = new MLP(nInputs, 30, 10, db);
            mlp.initialize();

            tx.success();
        }

        /* connect MLP to pixel nodes and target label nodes */

        Node target;
        Node originalTarget;
        Node[] nodes;

        try (Transaction tx = db.beginTx()) {

            nodes = db.findNodes(START).stream()
                    .filter(node -> !node.hasLabel(TARGET))
                    .filter(node -> node.getId() < 1000)    // hack to select the first pixel node in each chain
                    .toArray(Node[]::new);

            target = db.findNodes(START).stream()
                    .filter(node -> node.hasLabel(TARGET))
                    .filter(node -> node.getDegree() == 1)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("TestTask.run() - Node not found"));

            originalTarget = target;

            System.out.printf("array: %d, 28*28: %d\n", nodes.length, 28 * 28);
            mlp.connect(target, nodes);

            mlp.connect(originalTarget, nodes);

            tx.success();
        }

        /* update weights once for each example */

        double error;
        for (int i = 0; i < nTrain; i++) {

            try (Transaction tx = db.beginTx()) {
                target = mlp.getCurrentTarget();
                mlp.forwardPass();
                mlp.backwardPass(getTargets(target));
                error = mlp.getMeanSquaredError(1, 10, getTargets(target));
                if (i % 100 == 0) {
                    System.out.printf("Training %d of %d.\n", i, nTrain);
                    System.out.printf("Root mean squared error: %f\n", Math.sqrt(error));
                }

                mlp.nextInput();
            }
        }

        /* make predictions for test data */

        try (Transaction tx = db.beginTx()) {

            mlp.connect(originalTarget, nodes);

            for (int i = 0; i < nTest; i++) {
                target = mlp.getCurrentTarget();
                mlp.forwardPass();
                System.out.printf("Actual target: %d, Guess: %s\n", (int) target.getProperty(DATA), mlp.getGuess());

                mlp.nextInput();
            }

            tx.success();
        }
    }

    private double[] getTargets(Node target) {
        int val = (int) target.getProperty(DATA);
        double[] targets = new double[10];

        for (int i = 0; i < 10; i++) {
            targets[i] = 0.0;
        }
        targets[val] = 1.0;

        return targets;
    }
}
