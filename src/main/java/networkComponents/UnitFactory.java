package networkComponents;

import org.neo4j.graphdb.Node;

public abstract class UnitFactory {

    public abstract boolean isUnit(Node node);

    public abstract boolean isTrainable(Node node);

    public abstract Unit getUnit(Node node);

    public abstract TrainableUnit getTrainableUnit(Node node);
}
