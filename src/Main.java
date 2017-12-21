import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    private static int K;
    private static int label;
    private double correct;

    public static void main(String[] args) {

        int[] train_labels = MnistReader.getLabels("data/MNIST/train-labels-idx1-ubyte");
        List<int[][]> train_imgs = MnistReader.getImages("data/MNIST/train-images-idx3-ubyte");
        int[] test_labels = MnistReader.getLabels("data/MNIST/t10k-labels-idx1-ubyte");
        List<int[][]> test_imgs = MnistReader.getImages("data/MNIST/t10k-images-idx3-ubyte");
        int[][] train = MnistReader.toVectors(train_imgs, train_labels);
        int[][] test = MnistReader.toVectors(test_imgs, test_labels);

        label = 784;
        K = 10;
        //define domain of all variables;
        int[] domain = new int[label + 1];
        for (int i = 0; i < label; i++) {
            domain[i] = 2;
        }
        domain[label] = K;

        //initialize dataset
        WeightedData[] dataset = new WeightedData[train.length];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = new WeightedData(train[i], 1.0 / dataset.length);
        }

        //boosting-SAMME
        Main profile = new Main();
        int M = 1000;
        ArrayList<FactorGraph> models = new ArrayList<>(M);
        for (int i = 0; i < M; i++) {
            //System.out.println("Sum(weight)="+Arrays.stream(training).mapToDouble(d -> d.weight).sum());
            //ChowLiu model = new ChowLiu(training, domain, label);
            FactorGraph model = new FactorGraph(dataset, domain, label, 11, 11);
            double e = model.error;
            model.alpha = Math.log((1 / e - 1) * (K - 1));
            saveResult("error=" + e + ", alpha=" + model.alpha);
            models.add(model);
            for (WeightedData wd : dataset) {
                wd.weight = wd.missed ? (wd.weight * (K - 1) / (K * e)) : (wd.weight / (K * (1 - e)));
                //IMPORTANT!!! MUST RESET MARKERS
                wd.missed = false;
            }
            profile.benchmark(test, models);
        }

    }

    private void benchmark(int[][] test, ArrayList<FactorGraph> models) {
        correct = 0;
        Arrays.stream(test).parallel().forEach(d->{
            double[] votes = new double[K];
            for (FactorGraph cl : models) {
                votes[cl.predict(d)] += cl.alpha;
            }
            int winner = 0;
            for (int i = 0; i < votes.length; i++) {
                winner = (votes[i] > votes[winner] ? i : winner);
            }
            synchronized (this){
                if (d[label] == winner) correct++;
            }
        });
        String log = "Boosting round: " + models.size() + ", Accuracy: " + correct / test.length + "\n";
        saveResult(log);
        correct = 0;
    }

    /**
     * Save the running result to file
     */
    private static boolean saveResult(String content) {
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
