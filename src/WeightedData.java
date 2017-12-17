/**
 * Created by haoxiong on 12/17/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class WeightedData {
    public int[] vector;
    public double weight;
    public boolean pass;
    public WeightedData(int[] vector, double weight, boolean pass)
    {
        this.vector = vector;
        this.weight = weight;
        this.pass = pass;
    }
    public int getLabel()
    {
        return  this.vector[vector.length-1];
    }
}
