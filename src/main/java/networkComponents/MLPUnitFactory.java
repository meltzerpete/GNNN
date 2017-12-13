package networkComponents;

import org.neo4j.graphdb.Node;

import static networkComponents.UnitLabels.*;

public class MLPUnitFactory extends UnitFactory {

    private static UnitFactory unitFactory;

    public static UnitFactory get() {

        if (unitFactory == null) unitFactory = new MLPUnitFactory();

        return unitFactory;
    }

    @Override
    public Unit getUnit(Node node) {

        if (node.hasLabel(BIAS)) return new BiasUnit(new Constant(), node);
        if (node.hasLabel(INPUT)) return new InputUnit(new Scale(255), node);
        if (node.hasLabel(OUTPUT)) return new OutputUnit(new Sigmoid(), new Backpropagation(), node);
        if (node.hasLabel(HIDDEN)) return new HiddenUnit(new Sigmoid(), new Backpropagation(), node);

        throw new RuntimeException("Not a Unit");
    }

    @Override
    public TrainableUnit getTrainableUnit(Node node) {

        if (node.hasLabel(OUTPUT) | node.hasLabel(HIDDEN))
            return (TrainableUnit) getUnit(node);

        throw new RuntimeException("Not a TrainableUnit");
    }

    public boolean isUnit(Node node) {
        return node.hasLabel(BIAS) | node.hasLabel(INPUT) | node.hasLabel(OUTPUT) | node.hasLabel(HIDDEN);
    }

    public boolean isTrainable(Node node) {
        return node.hasLabel(OUTPUT) | node.hasLabel(HIDDEN);
    }
}
