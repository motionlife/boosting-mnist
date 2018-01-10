import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Test {
    public static void main(String args[]) {
//        double[] means = new double[]{6300, 101.0, 363000, 3500, 3300, 116000, 1950};
//        double[] longleyData = new double[]{
//                60323, 83.0, 234289, 2356, 1590, 107608, 1947,
//                61122, 88.5, 259426, 2325, 1456, 108632, 1948,
//                60171, 88.2, 258054, 3682, 1616, 109773, 1949,
//                61187, 89.5, 284599, 3351, 1650, 110929, 1950,
//                63221, 96.2, 328975, 2099, 3099, 112075, 1951,
//                63639, 98.1, 346999, 1932, 3594, 113270, 1952,
//                64989, 99.0, 365385, 1870, 3547, 115094, 1953,
//                63761, 100.0, 363112, 3578, 3350, 116219, 1954,
//                66019, 101.2, 397469, 2904, 3048, 117388, 1955,
//                67857, 104.6, 419180, 2822, 2857, 118734, 1956,
//                68169, 108.4, 442769, 2936, 2798, 120445, 1957,
//                66513, 110.8, 444546, 4681, 2637, 121950, 1958,
//                68655, 112.6, 482704, 3813, 2552, 123366, 1959,
//                69564, 114.2, 502601, 3931, 2514, 125368, 1960,
//                69331, 115.7, 518173, 4806, 2572, 127852, 1961,
//                70551, 116.9, 554894, 4007, 2827, 130081, 1962
//        };
//        RealMatrix matrix = createRealMatrix(longleyData, 16, 7);
//        RealMatrix cov = new Covariance(matrix).getCovarianceMatrix();
//        //long start = System.nanoTime();
//        double[] lower = new double[]{6328*0.95, 100.0*0.95, 363000*0.95, 3500*0.95, 3300*0.95, 116000*0.95, 1950*0.95};
//        double[] upper = new double[]{6300*1.05, 101.0*1.05, 363000*1.05, 3500*1.05, 3300*1.05, 116000*1.05, 1950*1.05};
//        MultivariateNormal mvn = new MultivariateNormal();
//        MultivariateNormal.CDFResult result = mvn.cdf(means, cov, lower, upper);
//        System.out.println("cdf = " + result.cdf + "\nerror=" + result.cdfError + "\nConverged:" + result.converged);
////      System.out.println("Time="+(System.nanoTime() - start)/1000000);

//        WeightedData d0 = new WeightedData(new byte[]{10,31,24,7,50,20,9,12,1},0.02);
//        WeightedData d1 = new WeightedData(new byte[]{9,32,24,7,50,20,9,12,2},0.18);
//        WeightedData d2 = new WeightedData(new byte[]{70,33,24,7,50,20,9,12,3},0.25);
//        WeightedData d3 = new WeightedData(new byte[]{125,33,24,7,50,20,9,12,4},0.005);
//        WeightedData d4 = new WeightedData(new byte[]{23,30,24,7,50,20,9,12,5},0.1);
//        WeightedData d5 = new WeightedData(new byte[]{102,34,24,7,50,20,9,12,3},0.005);
//        WeightedData d6 = new WeightedData(new byte[]{35,32,24,7,50,20,9,12,4},0.15);
//        WeightedData d7 = new WeightedData(new byte[]{26,27,24,7,50,20,9,12,5},0.1);
//        WeightedData d8 = new WeightedData(new byte[]{85,28,24,7,50,20,9,12,6},0.05);
//        WeightedData d9 = new WeightedData(new byte[]{124,29,24,7,50,20,9,12,7},0.005);
//        WeightedData.label = 8;
//        LabelLeafMargin lb = LabelLeafMargin.getFromData(new WeightedData[]{d0,d1,d2,d3,d4,d5,d6,d7,d8,d9},new int[]{0,1,7});


    }

    static RealMatrix createRealMatrix(double[] data, int nRows, int nCols) {
        double[][] matrixData = new double[nRows][nCols];
        int ptr = 0;
        for (int i = 0; i < nRows; i++) {
            System.arraycopy(data, ptr, matrixData[i], 0, nCols);
            ptr += nCols;
        }
        return new Array2DRowRealMatrix(matrixData);
    }
}