package seTryout;

import org.neo4j.graphdb.Node;

public abstract class TrainableUnit extends Unit {

    TrainBehaviour trainBehaviour;

    public TrainableUnit(ComputeBehaviour computeBehaviour, TrainBehaviour trainBehaviour) {
        super(computeBehaviour, computeBehaviour.node);

        this.trainBehaviour = trainBehaviour;
        trainBehaviour.setNode(computeBehaviour.node);
    }

    public void updateWeights() {
        trainBehaviour.updateWeights();
    }

}
