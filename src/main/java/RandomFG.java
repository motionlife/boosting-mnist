import mvn.MultivariateNormal;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by haoxiong on 1/9/2018.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class RandomFG {

    private int[][] factors;
    private double[] labelMargin;
    private LabelLeafMargin[] labelLeafMargins;
    double error;
    double alpha;

    public RandomFG(WeightedData[] data, int degree, int leaf) {
        System.out.println("Calculating label margin...");
        this.labelMargin = getLabelMargin(data);
        this.factors = getFactors(data[0].vector.length, degree, leaf);
        System.out.println("Calculating leaf margins...");
        this.labelLeafMargins = getLabelLeafMargin(data);
        System.out.println("Calculating error rate (parallel)...");
        this.error = errorRate(data);
        this.alpha = Math.log((1 / error - 1) * (WeightedData.K - 1));
        System.out.println("Updating weights...");
        for (WeightedData wd : data) {
            wd.weight = wd.missed ? (wd.weight * (WeightedData.K - 1) / (WeightedData.K * error)) : (wd.weight / (WeightedData.K * (1 - error)));
            wd.missed = false;
        }
    }

    private LabelLeafMargin[] getLabelLeafMargin(WeightedData[] data) {
        LabelLeafMargin[] result = new LabelLeafMargin[factors.length];
        for (int i = 0; i < factors.length; i++) {
            result[i] = LabelLeafMargin.getFromData(data, factors[i]);
        }
        return result;
    }

    private double[] getLabelMargin(WeightedData[] data) {
        double result[] = new double[WeightedData.K];
        for (WeightedData wd : data) {
            result[wd.vector[WeightedData.label]] += wd.weight;
        }
        return result;
    }

    private double errorRate(WeightedData[] data) {
        Arrays.stream(data).parallel().forEach(wd -> wd.missed = wd.vector[WeightedData.label] != predict(wd.vector));
        return Arrays.stream(data).mapToDouble(WeightedData::contributeErrorRate).sum();
    }

    int predict(int[] x) {
        double[] score = new double[labelMargin.length];
        for (int i = 0; i < score.length; i++) {
            double pb = (1 - factors.length) * Math.log(labelMargin[i]);
            for (int j = 0; j < factors.length; j++) {
                double[] lower = new double[factors[j].length + 1];
                double[] upper = new double[factors[j].length + 1];
                for (int k = 0; k < factors[j].length; k++) {
                    lower[k] = x[factors[j][k]] - 1;
                    upper[k] = x[factors[j][k]] + 1;
                }
                lower[lower.length - 1] = i - 1;
                upper[upper.length - 1] = i + 1;
                pb += Math.log(MultivariateNormal.DEFAULT_INSTANCE.cdf(labelLeafMargins[j].mu, labelLeafMargins[j].sigma, lower, upper).cdf);
                //todo: non-thread safe
            }
            System.out.println("p(y=" + i + ")=" + pb);
            score[i] = pb;
        }

        int result = 0;
        for (int i = 0; i < score.length; i++) {
            result = (score[i] > score[result] ? i : result);
        }
        return result;
    }

    private int[][] getFactors(int range, int degree, int leaf) {
        int[][] fac = new int[degree][leaf];
        Random rand = new Random();
        List<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < range; i++) {
            if (i != WeightedData.label) nodes.add(i);
        }
        for (int i = 0; i < degree; i++)
            for (int j = 0; j < leaf; j++) {
                int picked = rand.nextInt(nodes.size());
                fac[i][j] = nodes.get(picked);
                nodes.remove(picked);
            }
        return fac;
    }
}

class LabelLeafMargin {
    double[] mu;
    RealMatrix sigma;

    private LabelLeafMargin(double[] mean, RealMatrix covariance) {
        this.mu = mean;
        this.sigma = covariance;
    }


    static LabelLeafMargin getFromData(WeightedData[] data, int[] cols) {
        int dim = cols.length;
        double[] mean = new double[dim + 1];
        int[][] subset = new int[dim + 1][data.length];
        for (int i = 0; i < data.length; i++) {
            double w = data[i].weight;
            for (int j = 0; j < dim; j++) {
                subset[j][i] = data[i].vector[cols[j]];
                mean[j] += subset[j][i] * w;
            }
            subset[dim][i] = data[i].vector[WeightedData.label];
            mean[dim] += subset[dim][i] * w;
        }

        double[][] cov = new double[dim + 1][dim + 1];
        for (int i = 0; i < subset.length; i++) {
            for (int j = i; j < subset.length; j++) {
                int[] x = subset[i];
                int[] y = subset[j];
                int k, n = x.length;
                double d1, d2, avg1 = 0, avg2 = 0;
                double w;
                for (k = 0; k < n; k++) {
                    d1 = x[k];
                    d2 = y[k];
                    w = data[k].weight;
                    avg1 += d1 * w;
                    avg2 += d2 * w;
                }
                double covar = 0.;
                for (k = 0; k < n; k++) {
                    d1 = x[k];
                    d2 = y[k];
                    w = data[k].weight;
                    d1 -= avg1;
                    d2 -= avg2;
                    covar += w * d1 * d2;
                }
                cov[i][j] = covar;
                cov[j][i] = covar;
            }
        }
        return new LabelLeafMargin(mean, MatrixUtils.createRealMatrix(cov));
    }
}
