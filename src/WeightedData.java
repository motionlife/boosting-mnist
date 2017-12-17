/**
 * Created by haoxiong on 12/17/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class WeightedData {
    public int[] vector;
    public double weight;
    public boolean missed;

    public WeightedData(int[] vector, double weight) {
        this.vector = vector;
        this.weight = weight;
    }

    public int getLabel() {
        return this.vector[vector.length - 1];
    }
}
