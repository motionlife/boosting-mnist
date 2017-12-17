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
    public byte[] cache;
    public double error;
    public double alpha;

    ChowLiu(WeightedData[] Data, int[] domain, int label) {
        this.Data = Data;
        this.domain = domain;
        this.label = label;
        this.labelMargin = this.getMargin(label);
        this.labelPairMargin = new HashMap<>();
        cache = new byte[Data.length];
        this.buildChowLiuTree(label + 1);
        this.error = this.errorRate();
    }

    private double[] getMargin(int u) {
        double result[] = new double[this.domain[u]];
        for (WeightedData wd : this.Data) {
            result[wd.vector[u]] += wd.weight;
        }
        return result;
    }

    private double[][] getPairMargin(int u, int v) {
        double result[][] = new double[this.domain[u]][this.domain[v]];
        for (WeightedData wd : this.Data) {
            result[wd.vector[u]][wd.vector[v]] += wd.weight;
        }
        return result;
    }

    private double mutualInfo(int u, int v) {
        double info = 0;
        double[] mu = new double[this.domain[u]];
        double[] mv = new double[this.domain[v]];
        double[][] muv = new double[this.domain[u]][this.domain[v]];

        Arrays.stream(this.Data).parallel().forEach(wd -> {
            mu[wd.vector[u]] += wd.weight;
            mv[wd.vector[v]] += wd.weight;
            muv[wd.vector[u]][wd.vector[v]] += wd.weight;
        });

        for (int i = 0; i < mu.length; i++) {
            for (int j = 0; j < mv.length; j++) {
                double puv = muv[i][j];
                info += puv * (Math.log(puv) - Math.log(mu[i]) - Math.log(mv[j]));
            }
        }
        return info;
    }

    private void buildChowLiuTree(int V) {
        System.out.println("Calculate mutual info begins...");
        Graph G = new Graph(V);
        //todo: the slowest process is here, try to optimize java stream parallelism
        Arrays.stream(G.nodes).parallel()
                .forEach(u -> Arrays.stream(G.nodes)
                        .filter(v -> u.id < v.id)
                        .forEach(v -> G.setWeight(u.id, v.id, -mutualInfo(u.id, v.id))));
        System.out.println("Mutual info calculation finished");
        G.prim();

        //extract neighbour nodes of label node
        ArrayList<Node> neibs = G.getNeighbors(label);
        //store pair margin
        this.degree = neibs.size();
        for (Node n : neibs) {
            labelPairMargin.put(n.id, this.getPairMargin(n.id, label));
        }
    }

    private double errorRate() {
        double err = 0;
        for (WeightedData wd : this.Data) {
            if (wd.getLabel() != ChowLiu.predict(wd.vector, this)) {
                err += wd.weight;
                wd.pass = false;
            }
        }
        return err;
    }

    public static int predict(int[] x, ChowLiu model) {
        double[] score = new double[model.labelMargin.length];
        for (int i = 0; i < score.length; i++) {
            double likelihood = (1 - model.degree) * Math.log(model.labelMargin[i]);
            for (Map.Entry<Integer, double[][]> entry : model.labelPairMargin.entrySet()) {
                int id = entry.getKey();
                double[][] values = entry.getValue();
                double p = values[x[id]][i];
                likelihood += (p == 0 ? Math.log(values.length / (model.Data.length + values.length)) : Math.log(p));
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
