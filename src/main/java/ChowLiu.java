import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haoxiong on 12/14/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class ChowLiu {
    private WeightedData[] Data;
    private int[] domain;
    private int label;
    private int degree;
    private double[] labelMargin;
    private HashMap<Integer, double[][]> labelPairMargin;
    public double error;
    public double alpha;

    ChowLiu(WeightedData[] Data, int[] domain, int label) {
        this.Data = Data;
        this.domain = domain;
        this.label = label;
        labelMargin = getMargin(label);
        labelPairMargin = new HashMap<>();
        buildChowLiuTree(label + 1);
        error = errorRate();
    }

    private double[] getMargin(int u) {
        double result[] = new double[domain[u]];
        for (WeightedData wd : Data) {
            result[wd.vector[u]] += wd.weight;
        }
        return result;
    }

    private double[][] getPairMargin(int u, int v) {
        double result[][] = new double[domain[u]][domain[v]];
        for (WeightedData wd : Data) {
            result[wd.vector[u]][wd.vector[v]] += wd.weight;
        }
        return result;
    }

    private double mutualInfo(int u, int v) {
        double info = 0;
        double[] pu = new double[domain[u]];
        double[] pv = new double[domain[v]];
        double[][] puv = new double[domain[u]][domain[v]];

        for (WeightedData wd : Data) {
            pu[wd.vector[u]] += wd.weight;
            pv[wd.vector[v]] += wd.weight;
            puv[wd.vector[u]][wd.vector[v]] += wd.weight;
        }

        for (int i = 0; i < pu.length; i++) {
            for (int j = 0; j < pv.length; j++) {
                double p = puv[i][j];
                if (p != 0) info += p * Math.log(p / (pu[i] * pv[j]));
            }
        }
        return info;
    }

    private void buildChowLiuTree(int V) {
        System.out.println("Calculate mutual info begins...");
        Graph G = new Graph(V);
        //todo: the slowest process is here, try to optimize java stream parallelism
        Arrays.stream(G.nodes).parallel()
                .forEach(u -> Arrays.stream(G.nodes).parallel()
                        .filter(v -> u.id < v.id)
                        .forEach(v -> G.setWeight(u.id, v.id, -mutualInfo(u.id, v.id))));
        System.out.println("Mutual info calculation finished");
        G.prim();
        //extract neighbour nodes of label node
        ArrayList<Node> neighbours = G.getNeighbors(label);
        //store pair margin
        degree = neighbours.size();
        System.out.println("Tree degree=" + degree);
        for (Node n : neighbours) {
            labelPairMargin.put(n.id, getPairMargin(n.id, label));
        }
    }

    private double errorRate() {
        double err = 0.0;
        for (WeightedData wd : Data) {
            if (wd.vector[label] != predict(wd.vector)) {
                err += wd.weight;
                wd.missed = true;
            }
        }
        return err;
    }

    public int predict(byte[] x) {
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