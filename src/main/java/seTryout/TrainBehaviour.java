package seTryout;

import org.neo4j.graphdb.Node;

public abstract class TrainBehaviour {

    protected Node node;

    public void setNode(Node node) {
        this.node = node;
    }

    abstract void updateWeights();
}
