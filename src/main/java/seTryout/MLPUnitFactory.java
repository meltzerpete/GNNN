package seTryout;

import org.neo4j.graphdb.Node;

import static seTryout.UnitLabels.*;

public class MLPUnitFactory extends UnitFactory {

    private static UnitFactory unitFactory;

    public static UnitFactory get() {

        if (unitFactory == null) unitFactory = new MLPUnitFactory();

        return unitFactory;
    }

    @Override
    public Unit getUnit(Node node) {

        if (node.hasLabel(BIAS)) return new BiasUnit(new Constant());
        if (node.hasLabel(INPUT)) return new InputUnit(new DirectCopy());
        if (node.hasLabel(OUTPUT)) return new OutputUnit(new Sigmoid(), new Backpropogation());
        if (node.hasLabel(HIDDEN)) return new HiddenUnit(new Sigmoid(), new Backpropogation());

        return null;
    }
}
