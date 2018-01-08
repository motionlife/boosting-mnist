import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by haoxiong on 1/3/2018.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class CIFAR {

    public static void main(String args[]) throws IOException {
        int channel = 3;
        WeightedData[] dataset = new WeightedData[50000];
        byte[][] test = new byte[10000][1024 * channel + 1];

        for (int i = 0; i < 5; i++) {
            FileInputStream inputStream = new FileInputStream("./data/cifar-10-bin/data_batch_" + (i + 1) + ".bin");
            for (int j = 0; j < 10000; j++) {
                byte[] b = new byte[1024 * channel + 1];
                inputStream.read(b, 1024 * channel, 1);
                inputStream.read(b, 0, 1024 * channel);
                //inputStream.skip(1024 * (3 - channel));

                for (int k = 0; k < b.length - 1; k++) {
                    b[k] = (byte) (b[k] < 0 ? 1 : 0);
                }

                dataset[i * 10000 + j] = new WeightedData(b, 1.0 / dataset.length);
            }

        }

        FileInputStream inputStream = new FileInputStream("./data/cifar-10-bin/test_batch.bin");
        for (int j = 0; j < 10000; j++) {
            byte[] b = new byte[1024 * channel + 1];
            inputStream.read(b, 1024 * channel, 1);
            inputStream.read(b, 0, 1024 * channel);
            //inputStream.skip(1024 * (3 - channel));
            for (int k = 0; k < b.length - 1; k++) {
                b[k] = (byte) (b[k] < 0 ? 1 : 0);
            }
            test[j] = b;
        }

        int label = 1024 * channel;
        int K = 10;
        //define domain of all variables;
        byte[] domain = new byte[label + 1];
        for (int i = 0; i < label; i++) {
            domain[i] = 2;
        }
        domain[label] = (byte) K;
        Mnist parallel = new Mnist();
        int M = 1000;
        ArrayList<FactorGraph> models = new ArrayList<>(M);
        for (int i = 0; i < M; i++) {
            //System.out.println("Sum(weight)="+Arrays.stream(dataset).mapToDouble(d -> d.weight).sum());
            FactorGraph model = new FactorGraph(dataset, domain, label, 17, 11, true);
            double e = model.error;
            model.alpha = Math.log((1 / e - 1) * (K - 1));
            Mnist.saveResult("error=" + e + ", alpha=" + model.alpha);
            models.add(model);
            for (WeightedData wd : dataset) {
                wd.weight = wd.missed ? (wd.weight * (K - 1) / (K * e)) : (wd.weight / (K * (1 - e)));
                wd.missed = false;
            }
            parallel.benchmark(test, models, K, label);
        }
    }
}
