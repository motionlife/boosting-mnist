
import java.util.*;

/**
 * Created by haoxiong on 12/18/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class FactorGraph {
    private WeightedData[] Data;
    private int[] domain;
    private int[][] factors;
    private int label;
    private int degree;
    private int leaf;
    private double[] labelMargin;
    private HashMap[] labelPairMargin;
    public double error;
    public double alpha;

    FactorGraph(WeightedData[] Data, int[] domain, int label, int degree, int leaf) {
            this.Data = Data;
            this.domain = domain;
            this.label = label;
            this.degree = degree;
            this.leaf = leaf;
            this.factors = getFactors();
            this.labelMargin = getMargin(label);
            this.labelPairMargin = getLabelPairMargin();
            this.error = errorRate();
    }

    private double[] getMargin(int u) {
        double result[] = new double[domain[u]];
        for (WeightedData wd : Data) {
            result[wd.vector[u]] += wd.weight;
        }
        return result;
    }

    private HashMap[] getLabelPairMargin() {
        HashMap[] dist = new HashMap[degree];
        for (int i = 0; i < degree; i++) {
            dist[i] = new HashMap<List<Integer>, Double>();
        }
        Arrays.stream(Data).forEach(wd -> {
            for (int i = 0; i < factors.length; i++) {
                List<Integer> dom = new ArrayList<>();
                for (int n : factors[i]) {
                    dom.add(wd.vector[n]);
                }
                dom.add(wd.vector[label]);
                Double v = (Double) dist[i].get(dom);
                dist[i].put(dom, v == null ? wd.weight : v + wd.weight);
            }
        });
        return dist;
    }

    private double errorRate() {
        Arrays.stream(Data).parallel().forEach(wd -> wd.missed = wd.vector[label] != predict(wd.vector));
        return Arrays.stream(Data).mapToDouble(WeightedData::contributeErrorRate).sum();
    }

    public int predict(int[] x) {
        double[] score = new double[labelMargin.length];
        for (int i = 0; i < score.length; i++) {
            double likelihood = (1 - degree) * Math.log(labelMargin[i]);
            for (int j = 0; j < factors.length; j++) {
                HashMap dist = labelPairMargin[j];
                List<Integer> dom = new ArrayList<>();
                for (int n : factors[j]) {
                    dom.add(x[n]);
                }
                dom.add(i);
                Double v = (Double) dist.get(dom);
                //laplace smoothing
                likelihood += v == null ? Math.log(dist.size() / (dist.size() + Data.length)) : Math.log(v);
            }
            score[i] = likelihood;
        }

        int result = 0;
        for (int i = 0; i < score.length; i++) {
            result = (score[i] > score[result] ? i : result);
        }
        return result;
    }

    private int[][] getFactors() {
        int[][] nb = new int[degree][leaf];
        Random rand = new Random();
        List<Integer> nodes = new ArrayList<>(domain.length - 1);
        for (int i = 0; i < domain.length - 1; i++) {
            nodes.add(i);
        }
        for (int i = 0; i < degree; i++) {
            for (int j = 0; j < leaf; j++) {
                int randomIndex = rand.nextInt(nodes.size());//out-of-bounds
                nb[i][j] = nodes.get(randomIndex);
                nodes.remove(randomIndex);
            }
        }
        return nb;
    }
}
