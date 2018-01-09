/**
 * Created by haoxiong on 12/17/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class WeightedData {
    public byte[] vector;
    public double weight;
    public boolean missed;

    public WeightedData(byte[] vector, double weight) {
        this.vector = vector;
        this.weight = weight;
    }

    double getError() {
        return missed ? weight : 0;
    }
}
