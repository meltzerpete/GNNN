package networkComponents;

import org.neo4j.graphdb.Node;

import static networkComponents.Constants.OUTPUT;

public class OutputUnit extends TrainableUnit {

    OutputUnit(ComputeBehaviour computeBehaviour, TrainBehaviour trainBehaviour, Node node) {
        super(computeBehaviour, trainBehaviour, node);
    }

    public double getSquaredError(double target) {
        double error = (target - (double) node.getProperty(OUTPUT));
        return Math.pow(error, 2);
    }
}
