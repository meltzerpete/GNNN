package networkComponents;

import static networkComponents.Constants.OUTPUT;

public class Constant extends ComputeBehaviour {

    @Override
    public double compute() {
        System.out.println("computing constant");
        return (double) node.getProperty(OUTPUT);
    }
}
