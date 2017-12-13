package mnist;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import static networkComponents.Constants.DATA;
import static networkComponents.RelationshipTypes.NEXT;

public class LoadMNISTTask implements Runnable {

    GraphDatabaseService db;

    private long limit;

    private long[] idsToConnectTo;
    private long targetIDtoConnectTo;

    public LoadMNISTTask(long limit, GraphDatabaseService db) {
        this.limit = limit;
        this.db = db;
    }

    @Override
    public void run() {

        MNISTLoader mnistLoader = new MNISTLoader();

        while (limit > 0) {

            System.out.println(String.format("%d remaining.", limit));

            // check amount remaining
            long batchLimit = limit > 100 ? 100 : limit;
            limit -= 100;

            // start transaction for the batch
            try (Transaction tx = db.beginTx()) {

                MNISTLoader.Example example = mnistLoader.nextExample();

                Node[] imageNodes = new Node[Execute.IMAGE_SIZE];

                Node targetNode;

                // check if starting from scratch or connecting to existing chain
                if (idsToConnectTo == null) {

                    // create start nodes for image
                    for (int i = 0; i < Execute.IMAGE_SIZE; i++) {
                        // create pixel node
                        imageNodes[i] = db.createNode(Execute.Labels.START, Execute.Labels.DATA);
                        imageNodes[i].setProperty(Execute.PIXEL, i);
                        imageNodes[i].setProperty(DATA, example.getPixel(i));
                    }

                    // create target/label node
                    targetNode = db.createNode(Execute.Labels.START, Execute.Labels.TARGET);
                    targetNode.setProperty(DATA, example.getTarget());
                } else {

                    for (int i = 0; i < Execute.IMAGE_SIZE; i++) {
                        imageNodes[i] = db.getNodeById(idsToConnectTo[i]);
                    }
                    targetNode = db.getNodeById(targetIDtoConnectTo);
                }


                int count = 0;
                Node nextNodes[] = new Node[Execute.IMAGE_SIZE];
                Node nextTargetNode = null;
                while (mnistLoader.hasNext() && count++ < batchLimit) {

                    example = mnistLoader.nextExample();

                    for (int i = 0; i < Execute.IMAGE_SIZE; i++) {
                        // create pixel node and link to chain
                        nextNodes[i] = db.createNode(Execute.Labels.DATA);
                        nextNodes[i].setProperty(Execute.PIXEL, i);
                        nextNodes[i].setProperty(DATA, example.getPixel(i));

                        imageNodes[i].createRelationshipTo(nextNodes[i], NEXT);
                        imageNodes[i] = nextNodes[i];
                    }

                    // create target/label node and link to chain
                    nextTargetNode = db.createNode(Execute.Labels.TARGET);
                    nextTargetNode.setProperty(DATA, example.getTarget());

                    targetNode.createRelationshipTo(nextTargetNode, NEXT);
                    targetNode = nextTargetNode;
                }

                // save ids for next batch to connect to
                idsToConnectTo = new long[imageNodes.length];

                for (int i = 0; i < imageNodes.length; i++) {
                    idsToConnectTo[i] = imageNodes[i].getId();
                }

                targetIDtoConnectTo = nextTargetNode.getId();

                tx.success();
            }


        }
    }
}
