import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by haoxiong on 1/3/2018.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class CIFAR {

    public static void main(String args[]) throws IOException {
        int channel = 1;
        WeightedData[] dataset = new WeightedData[50000];
        byte[][] test = new byte[10000][1024 * channel + 1];

        for (int i = 0; i < 5; i++) {
            FileInputStream inputStream = new FileInputStream("./data/cifar-10-bin/data_batch_" + (i + 1) + ".bin");
            for (int j = 0; j < 10000; j++) {
                byte[] b = new byte[1024 * channel + 1];
                inputStream.read(b, 1024 * channel, 1);
                int[] buffer = new int[1024 * channel];
                for (int itr = 0; itr < buffer.length; itr++) buffer[itr] = inputStream.read();
                //inputStream.read(b, 0, 1024 * channel);
                inputStream.skip(1024 * (3 - channel));
                int[] dithered = twoToOne(dither(oneToTwo(buffer)));
                for (int k = 0; k < b.length - 1; k++) {
                    b[k] = (byte) (dithered[k] < 128 ? 0 : 1);
                }
                dataset[i * 10000 + j] = new WeightedData(b, 1.0 / dataset.length);
            }
        }

        FileInputStream inputStream = new FileInputStream("./data/cifar-10-bin/test_batch.bin");
        for (int i = 0; i < 10000; i++) {
            byte[] b = new byte[1024 * channel + 1];
            inputStream.read(b, 1024 * channel, 1);
            int[] buffer = new int[1024 * channel];
            for (int itr = 0; itr < buffer.length; itr++) buffer[itr] = inputStream.read();
            //inputStream.read(b, 0, 1024 * channel);
            inputStream.skip(1024 * (3 - channel));
            int[] dithered = twoToOne(dither(oneToTwo(buffer)));
            for (int k = 0; k < b.length - 1; k++) {
                b[k] = (byte) (dithered[k] < 128 ? 0 : 1);
            }
            test[i] = b;
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
            FactorGraph model = new FactorGraph(dataset, domain, label, 20, 7, true);
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

    static int[][] dither(int[][] pixel) {
        int height = 32;
        int width = 32;
        int oldpixel, newpixel, error;
        boolean nbottom, nleft, nright;
        for (int y = 0; y < height; y++) {
            nbottom = y < height - 1;
            for (int x = 0; x < width; x++) {
                nleft = x > 0;
                nright = x < width - 1;
                oldpixel = pixel[x][y];
                newpixel = oldpixel < 128 ? 0 : 255;
                pixel[x][y] = newpixel;
                error = oldpixel - newpixel;
                if (nright) pixel[x + 1][y] += 7 * error / 16;
                if (nleft & nbottom) pixel[x - 1][y + 1] += 3 * error / 16;
                if (nbottom) pixel[x][y + 1] += 5 * error / 16;
                if (nright && nbottom) pixel[x + 1][y + 1] += error / 16;
            }
        }
        return pixel;
    }

    static int[][] oneToTwo(int[] input) {
        int output[][] = new int[32][32];
        for (int i = 0; i < 32; i++)
            System.arraycopy(input, i * 32, output[i], 0, 32);
        return output;
    }

    static int[] twoToOne(int[][] input) {
        int[] output = new int[1024];
        for (int i = 0; i < 32; i++)
            System.arraycopy(input[i], 0, output, i * 32, 32);
        return output;
    }
}
