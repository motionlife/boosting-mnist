/**
 * Created by haoxiong on 12/14/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class ChowLiu {
    public int[][] data;
    public int label;
    public double[] weight;
    public double[] labelMargin;
    public double[][] pairMargin;
    private int fr;
    private int lr;

    public ChowLiu(int[][] data, int label, int fr, int lr, double[] weight) {
        this.data = data;
        this.weight = weight;
        this.label = label;
        this.fr = fr;
        this.lr = lr;
        this.labelMargin = this.getMargin(label, this.lr);
    }

    private double[] getMargin(int u, int range) {
        double result[] = new double[range];
        for (int i = 0; i < this.data.length; i++) {
            result[this.data[i][u]] += this.weight[i];
        }
        return result;
    }

    private double[][] getPairMargin(int u, int v, int ur, int vr) {
        if (u > v) {
            int k = u;
            u = v;
            v = k;
        }
        double result[][] = new double[ur][vr];
        for (int i = 0; i < this.data.length; i++) {
            int[] x = this.data[i];
            result[x[u]][x[v]] += this.weight[i];
        }
        return result;
    }

    private double mutualInfo(int u, int v, int ur, int vr) {
        if (u > v) {
            int k = u;
            u = v;
            v = k;
        }
        double info = 0;
        double[] mu = this.getMargin(u, ur);
        double[] mv = this.getMargin(v, vr);
        double[][] pmuv = this.getPairMargin(u, v, ur, vr);
        for (int i = 0; i < mu.length; i++) {
            for (int j = 0; j < mv.length; j++) {
                double puv = pmuv[i][j];
                info += puv * (Math.log(puv) - Math.log(mu[i]) - Math.log(mv[j]));
            }
        }
        return info;
    }

    private void buildChowLiuTree(int n){


    }

}
