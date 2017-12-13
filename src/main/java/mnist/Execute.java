package mnist;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import static org.neo4j.procedure.Mode.SCHEMA;

public class Execute {

    static final int IMAGE_SIZE = 28 * 28;
    static final String PIXEL = "pixel";
    public enum Labels implements Label { START, DATA, TARGET }

    @Context
    public GraphDatabaseService db;

    @Procedure(value = "mnist.load", mode = SCHEMA)
    public void load(@Name("Limit") long limit) {


//        Runnable r = new LoadMNISTTask(limit);
//        r.run();
        Thread worker = new Thread(new LoadMNISTTask(limit, db));
        worker.setName("MNIST_Loader");
        worker.start();

        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
