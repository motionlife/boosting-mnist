import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haoxiong on 12/18/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class FactorGraph {
    private WeightedData[] Data;
    private int[] domain;
    private int label;
    private int degree;
    private int f_size;
    private double[] labelMargin;
    private HashMap<Integer, double[][]> labelPairMargin;
    public double error;
    public double alpha;

    FactorGraph(WeightedData[] Data, int[] domain, int label, int degree, int f) {
        this.Data = Data;
        this.domain = domain;
        this.label = label;
        this.degree = degree;
        f_size = f;
        labelMargin = getMargin(label);


        error = errorRate();
    }

    private double[] getMargin(int u) {
        double result[] = new double[domain[u]];
        for (WeightedData wd : Data) {
            result[wd.vector[u]] += wd.weight;
        }
        return result;
    }

    private double errorRate() {
        double err = 0.0;
        for (WeightedData wd : Data) {
            if (wd.getLabel() != predict(wd.vector)) {
                err += wd.weight;
                wd.missed = true;
            }
        }
        return err;
    }

    public int predict(int[] x) {
        double[] score = new double[labelMargin.length];
        for (int i = 0; i < score.length; i++) {
            double likelihood = (1 - degree) * Math.log(labelMargin[i]);
            for (Map.Entry<Integer, double[][]> entry : labelPairMargin.entrySet()) {
                int id = entry.getKey();
                double[][] values = entry.getValue();
                double p = values[x[id]][i];
                likelihood += (p == 0 ? Math.log(values.length / (Data.length + values.length)) : Math.log(p));
            }
            score[i] = likelihood;
        }

        int result = 0;
        for (int i = 0; i < score.length; i++) {
            result = (score[i] > score[result] ? i : result);
        }
        return result;
    }
}

class Factor {
    int[] nodes;

    Factor(int size)
    {
        nodes = new int[size];
    }
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Factor && obj.toString().equals(toString());
    }

    @Override
    public String toString() {
        return String.join(",", (CharSequence) Arrays.asList(nodes));
    }
}
