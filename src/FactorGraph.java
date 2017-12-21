import java.util.*;

/**
 * Created by haoxiong on 12/18/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class FactorGraph {
    private WeightedData[] Data;
    private int[] domain;
    private int[][] neighbours;
    private int label;
    private int degree;
    private int f_size;
    private double[] labelMargin;
    private HashMap<List<Integer>, Double>[] labelPairMargin;
    public double error;
    public double alpha;

    FactorGraph(WeightedData[] Data, int[] domain, int label, int degree, int f) {
        this.Data = Data;
        this.domain = domain;
        this.label = label;
        this.degree = degree;
        this.f_size = f;
        this.labelMargin = getMargin(label);
        this.neighbours = getNeighbours();
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

    private HashMap<List<Integer>, Double>[] getLabelPairMargin() {
        HashMap<List<Integer>, Double>[] dist = new HashMap[degree];
        for (int i = 0; i < degree; i++) {
            dist[i] = new HashMap<>();
        }
        Arrays.stream(Data).forEach(wd -> {
            for (int i = 0; i < neighbours.length; i++) {
                List<Integer> dom = new ArrayList<>();
                for (int n : neighbours[i]) {
                    dom.add(wd.vector[n]);
                }
                dom.add(wd.vector[label]);
                Double v = dist[i].get(dom);
                dist[i].put(dom, v == null ? wd.weight : v + wd.weight);
            }
        });
        return dist;
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
            for (int j = 0; j < neighbours.length; j++) {
                HashMap<List<Integer>, Double> dist = labelPairMargin[j];
                List<Integer> dom = new ArrayList<>();
                for (int n : neighbours[j]) {
                    dom.add(x[n]);
                }
                dom.add(i);
                Double v = dist.get(dom);
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


    private int[][] getNeighbours() {
        int[][] nb = new int[degree][f_size];
        Random rand = new Random();
        List<Integer> nodes = new ArrayList<>(label);
        for (int i = 0; i < label; i++) {
            nodes.add(i);
        }
        for (int i = 0; i < degree; i++) {
            for (int j = 0; j < f_size; j++) {
                int randomIndex = rand.nextInt(nodes.size());//out-of-bounds
                nb[i][j] = nodes.get(randomIndex);
                nodes.remove(randomIndex);
            }
        }

        return nb;
    }
}
