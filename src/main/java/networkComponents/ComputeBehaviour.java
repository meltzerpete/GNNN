package networkComponents;

import org.neo4j.graphdb.Node;

public abstract class ComputeBehaviour {

    protected Node node;

    public void setNode(Node node){
        this.node = node;
    }

    abstract double compute();
}
