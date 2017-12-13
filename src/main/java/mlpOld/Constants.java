package mlpOld;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

/**
 * Created by Pete Meltzer on 30/10/17.
 */
@Deprecated
public class Constants {

    public final static double eta_0 = 0.1;
    public final static double lambda = 1;

    public enum labels implements Label {
        SET, NEURON, INPUT, HIDDEN, FINAL, BIAS, CURRENTSET
    }

    public enum relationshipTypes implements RelationshipType {
        REL, LINK, CONTAINS, NEXT, IN
    }

    public enum dataFields {
        A, FA, Y, WEIGHT, TARGET
    }

}
