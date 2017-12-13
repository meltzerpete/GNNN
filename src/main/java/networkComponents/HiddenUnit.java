package networkComponents;

import org.neo4j.graphdb.Node;

public class HiddenUnit extends TrainableUnit {

    HiddenUnit(ComputeBehaviour computeBehaviour, TrainBehaviour trainBehaviour, Node node) {
        super(computeBehaviour, trainBehaviour, node);
    }
}
