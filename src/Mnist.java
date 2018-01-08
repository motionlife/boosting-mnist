import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mnist {

    private double correct;

    public static void main(String[] args) {

        int[] train_labels = MnistReader.getLabels("data/MNIST/train-labels-idx1-ubyte");
        List<int[][]> train_imgs = MnistReader.getImages("data/MNIST/train-images-idx3-ubyte");
        int[] test_labels = MnistReader.getLabels("data/MNIST/t10k-labels-idx1-ubyte");
        List<int[][]> test_imgs = MnistReader.getImages("data/MNIST/t10k-images-idx3-ubyte");
        byte[][] train = MnistReader.toVectors(train_imgs, train_labels);
        byte[][] test = MnistReader.toVectors(test_imgs, test_labels);

        int label = 784;
        int k = 10;
        //define domain of all variables;
        byte[] domain = new byte[label + 1];
        for (int i = 0; i < label; i++) {
            domain[i] = 2;
        }
        domain[label] = (byte) k;

        //initialize dataset
        WeightedData[] dataset = new WeightedData[train.length];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = new WeightedData(train[i], 1.0 / dataset.length);
        }

        //boosting-SAMME
        Mnist profile = new Mnist();
        int M = 1000;
        ArrayList<FactorGraph> models = new ArrayList<>(M);
        for (int i = 0; i < M; i++) {
            //System.out.println("Sum(weight)="+Arrays.stream(training).mapToDouble(d -> d.weight).sum());
            FactorGraph model = new FactorGraph(dataset, domain, label, 11, 11, true);
            double e = model.error;
            model.alpha = Math.log((1 / e - 1) * (k - 1));
            saveResult("error=" + e + ", alpha=" + model.alpha);
            models.add(model);
            for (WeightedData wd : dataset) {
                wd.weight = wd.missed ? (wd.weight * (k - 1) / (k * e)) : (wd.weight / (k * (1 - e)));
                //IMPORTANT!!! MUST RESET MARKERS
                wd.missed = false;
            }
            profile.benchmark(test, models, k, label);
        }

    }

    public void benchmark(byte[][] test, ArrayList<FactorGraph> models, int k, int l) {
        correct = 0;
        Arrays.stream(test).parallel().forEach(d -> {
            double[] votes = new double[k];
            for (FactorGraph cl : models) {
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
        saveResult(log);
        correct = 0;
    }

    /**
     * Save the running result to file
     */
    public static boolean saveResult(String content) {
        System.out.println(content);
        boolean success = false;
        File file = new File("result.txt");
        try {
            if (!file.exists()) success = file.createNewFile();
            //Here true is to append the content to file
            FileWriter fw = new FileWriter(file, true);
            //BufferedWriter writer give better performance
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            //Closing BufferedWriter Stream
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
}
