package seTryout;

import org.neo4j.graphdb.Node;

public class InputUnit extends Unit {

    public InputUnit(ComputeBehaviour computeBehaviour) {
        super(computeBehaviour, computeBehaviour.node);
    }
}
