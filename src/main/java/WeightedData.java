/**
 * Created by haoxiong on 12/17/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class WeightedData {
    static int label;
    static int K;
    public int[] vector;
    public double weight;
    public boolean missed;

    WeightedData(int[] vector, double weight) {
        this.vector = vector;
        this.weight = weight;
    }

    double contributeErrorRate() {
        return missed ? weight : 0;
    }
}
