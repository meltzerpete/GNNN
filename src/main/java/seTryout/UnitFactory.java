package seTryout;

import org.neo4j.graphdb.Node;

public abstract class UnitFactory {

    public abstract Unit getUnit(Node node);
}
