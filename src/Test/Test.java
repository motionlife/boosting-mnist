package Test;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

public class Test {
    public static void main(String args[]) {
        double[] means = new double[]{13.34, 125.3, 78.12};
        double[][] covariance = new double[][]{{20.0, -0.86, -0.15}, {-0.86, 30.4, 0.48}, {-0.15, 0.48, 20.82}};
        MultivariateNormalDistribution mv = new MultivariateNormalDistribution(means, covariance);
        System.out.println(mv.density(new double[]{13,125,89}));
    }
}