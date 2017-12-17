import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by haoxiong on 12/14/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class ChowLiu {
    public int[][] data;
    public int[] domain;
    public int label;
    public int degree;
    public double[] weight;
    public double[] labelMargin;
    public HashMap<Integer, double[][]> labelPairMargin;
    public byte[] cache;
    public double error;
    public double alpha;

    public ChowLiu(int[][] data, int[] domain, int label, double[] weight) {
        this.data = data;
        this.domain = domain;
        this.label = label;
        this.weight = weight;
        this.labelMargin = this.getMargin(label);
        this.labelPairMargin = new HashMap<>();
        cache = new byte[data.length];
        this.buildChowLiuTree(label + 1);
        this.error = this.errorRate();
    }

    private double[] getMargin(int u) {
        double result[] = new double[this.domain[u]];
        for (int i = 0; i < this.data.length; i++) {
            result[this.data[i][u]] += this.weight[i];
        }
        return result;
    }

    private double[][] getPairMargin(int u, int v) {
        if (u > v) {
            int k = u;
            u = v;
            v = k;
        }
        double result[][] = new double[this.domain[u]][this.domain[v]];
        for (int i = 0; i < this.data.length; i++) {
            int[] x = this.data[i];
            result[x[u]][x[v]] += this.weight[i];
        }
        return result;
    }

    private double mutualInfo(int u, int v) {
        if (u > v) {
            int k = u;
            u = v;
            v = k;
        }
        double info = 0;
        double[] mu = this.getMargin(u);
        double[] mv = this.getMargin(v);
        double[][] pmuv = this.getPairMargin(u, v);
        for (int i = 0; i < mu.length; i++) {
            for (int j = 0; j < mv.length; j++) {
                double puv = pmuv[i][j];
                info += puv * (Math.log(puv) - Math.log(mu[i]) - Math.log(mv[j]));
            }
        }
        return info;
    }

    private void buildChowLiuTree(int V) {
        System.out.println("Calculate mutual info begins...");
        Graph G = new Graph(V);
        //the slowest process is here, try to optimize java stream parallelism
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                //G.setWeight(i, j, -mutualInfo(i, j));
                G.setWeight(i,j, -Math.random());
            }
        }
        System.out.println("Mutual info calculation finished");
        G.prim();
        System.out.println("Building MST finished");
        //extract neighbour nodes of label node
        ArrayList<Node> neibs = new ArrayList<>();
        //add parent
        Node parent = G.nodes[label].parent;
        if (parent != null) {
            neibs.add(parent);
        }
        //add all children
        for (Node node : G.nodes) {
            Node p = node.parent;
            if (p != null && p.key == this.label) {
                neibs.add(p);
            }
        }
        //store pair margin
        this.degree = neibs.size();
        System.out.println("Tree degree: "+this.degree);
        for (Node n : neibs) {
            labelPairMargin.put(n.id, this.getPairMargin(n.id, label));
            System.out.print(n.id+ ",");
        }
        System.out.println();
    }

    private double errorRate() {
        double err = 0;
        for (int i = 0; i < this.data.length; i++) {
            int[] x = this.data[i];
            if (x[this.label] != ChowLiu.predict(x, this)) {
                err += this.weight[i];
                this.cache[i] = 1;
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
                likelihood += p == 0 ? Math.log(values.length / (model.data.length + values.length)) : Math.log(p);
            }
            score[i] = likelihood;
        }

        int result = 0;
        for (int i = 0; i < score.length; i++) {
            result = score[i] > score[result] ? i : result;
        }
        return result;
    }
}
