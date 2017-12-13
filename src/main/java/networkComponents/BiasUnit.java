package networkComponents;

import org.neo4j.graphdb.Node;

import static networkComponents.Constants.OUTPUT;

public class BiasUnit extends Unit {

    BiasUnit(ComputeBehaviour computeBehaviour, Node node) {
        super(computeBehaviour, node);
    }

    @Override
    public void initialize() {
        setProperty(OUTPUT, 1.0);
    }
}
