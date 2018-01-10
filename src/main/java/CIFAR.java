import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by haoxiong on 1/3/2018.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class CIFAR {

    private double correct;

    public static void main(String args[]) throws IOException {
        int channel = 1;
        WeightedData[] dataset = new WeightedData[50000];
        int[][] test = new int[10000][1024 * channel + 1];

        for (int i = 0; i < 5; i++) {
            FileInputStream inputStream = new FileInputStream("./data/cifar-10-bin/data_batch_" + (i + 1) + ".bin");
            for (int j = 0; j < 10000; j++) {
                int[] buffer = new int[1024 * channel + 1];
                buffer[1024 * channel] = inputStream.read();
                for (int ptr = 0; ptr < 1024 * channel; ptr++) buffer[ptr] = inputStream.read();
                inputStream.skip(1024 * (3 - channel));
                dataset[i * 10000 + j] = new WeightedData(buffer, 1.0 / dataset.length);
            }
        }

        FileInputStream inputStream = new FileInputStream("./data/cifar-10-bin/test_batch.bin");
        for (int i = 0; i < 10000; i++) {
            test[i][1024 * channel] = inputStream.read();
            for (int ptr = 0; ptr < 1024 * channel; ptr++) test[i][ptr] = inputStream.read();
            inputStream.skip(1024 * (3 - channel));
        }

        WeightedData.label = 1024 * channel;
        WeightedData.K = 10;

        CIFAR cifar = new CIFAR();

        int M = 1000;
        ArrayList<RandomFG> models = new ArrayList<>(M);
        for (int i = 0; i < M; i++) {
            //System.out.println("Sum(weight)="+Arrays.stream(dataset).mapToDouble(d -> d.weight).sum());
            RandomFG model = new RandomFG(dataset, 7, 7);
            models.add(model);
            Samme.saveResult("error=" + model.error + ", alpha=" + model.alpha);

            cifar.benchmark(test, models, WeightedData.K, WeightedData.label);
        }
    }

    void benchmark(int[][] test, ArrayList<RandomFG> models, int k, int l) {
        correct = 0;
        Arrays.stream(test).parallel().forEach(d -> {
            double[] votes = new double[k];
            for (RandomFG cl : models) {
                votes[cl.predict(d)] += cl.alpha;
            }
            int winner = 0;
            for (int i = 0; i < votes.length; i++) {
                winner = votes[i] > votes[winner] ? i : winner;
            }
            if (d[l] == winner) {
                synchronized (this) {
                    correct++;
                }
            }
        });
        String log = "Boosting round: " + models.size() + ", Accuracy: " + correct / test.length + "\n";
        Samme.saveResult(log);
        correct = 0;
    }

}
