import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by haoxiong on 1/3/2018.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class CIFAR {

    private double correct;

    public static void main(String args[]) throws IOException {
        //int channel = 1;
        WeightedData[] dataset = new WeightedData[50000];
        int[][] test = new int[10000][1025];

        for (int i = 0; i < 5; i++) {
            final FileChannel channel = new FileInputStream("./data/cifar-10-bin/data_batch_" + (i + 1) + ".bin").getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            for (int j = 0; j < 10000; j++) {
                int[] img = new int[1025];
                img[1024] = buffer.get();
                for (int ptr = 0; ptr < 3072; ptr++) img[ptr % 1024] += buffer.get() & 0xff;
                for (int k = 0; k < 1024; k++) img[k] /= 3;
                dataset[i * 10000 + j] = new WeightedData(img, 1.0 / dataset.length);
            }
            channel.close();
        }

        final FileChannel channel = new FileInputStream("./data/cifar-10-bin/test_batch.bin").getChannel();
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        for (int i = 0; i < 10000; i++) {
            test[i][1024] = buffer.get();
            for (int ptr = 0; ptr < 3072; ptr++) test[i][ptr % 1024] += buffer.get() & 0xff;
            for (int k = 0; k < 1024; k++) test[i][k] /= 3;
        }

        WeightedData.label = 1024;
        WeightedData.K = 10;

        CIFAR cifar = new CIFAR();

        int M = 700;
        ArrayList<RandomFG> models = new ArrayList<>(M);
        for (int i = 0; i < M; i++) {
            //System.out.println("Sum(weight)="+Arrays.stream(dataset).mapToDouble(d -> d.weight).sum());
            RandomFG model = new RandomFG(dataset, 3, 7);
            models.add(model);
            Samme.saveResult("error=" + model.error + ", alpha=" + model.alpha);

            cifar.benchmark(test, models, WeightedData.K, WeightedData.label);
        }
    }

    private void benchmark(int[][] test, ArrayList<RandomFG> models, int k, int l) {
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
