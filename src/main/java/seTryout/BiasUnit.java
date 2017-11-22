package seTryout;

import org.neo4j.graphdb.Node;

public class BiasUnit extends Unit {

public BiasUnit(ComputeBehaviour computeBehaviour) {
        super(computeBehaviour, computeBehaviour.node);
    }
}
