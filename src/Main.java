import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static int K;
    private static int label;

    public static void main(String[] args) {

        int[] train_labels = MnistReader.getLabels("../data/MNIST/train-labels-idx1-ubyte");
        List<int[][]> train_imgs = MnistReader.getImages("../data/MNIST/train-images-idx3-ubyte");
        int[] test_labels = MnistReader.getLabels("../data/MNIST/t10k-labels-idx1-ubyte");
        List<int[][]> test_imgs = MnistReader.getImages("../data/MNIST/t10k-images-idx3-ubyte");
        int[][] train = MnistReader.toVectors(train_imgs, train_labels);
        int[][] test = MnistReader.toVectors(test_imgs, test_labels);

        label = 784;
        K = 10;
        //define domain of all variables;
        int[] domain = new int[label + 1];
        for (int i = 0; i < label; i++) {
            domain[i] = 7;// originally is 256, down scaled to 7
        }
        domain[label] = K;

        //initialize dataset
        WeightedData[] training = new WeightedData[train.length];
        for (int i = 0; i < training.length; i++) {
            training[i] = new WeightedData(train[i], 1.0 / training.length);
        }

        //boosting-SAMME
        int M = 700;
        ArrayList<ChowLiu> models = new ArrayList<>(M);
        for (int i = 0; i < M; i++) {
            //System.out.println("Sum(weight)="+Arrays.stream(training).mapToDouble(d -> d.weight).sum());
            ChowLiu model = new ChowLiu(training, domain, label);
            double e = model.error;
            model.alpha = Math.log((1 / e - 1) * (K - 1));
            System.out.println("error=" + e + " alpha=" + model.alpha);
            models.add(model);
            for (WeightedData wd : training) {
                wd.weight = wd.missed ? (wd.weight * (K - 1) / (K * e)) : (wd.weight / (K * (1 - e)));
                //IMPORTANT!!! MUST RESET MARKERS
                wd.missed = false;
            }
            benchmark(test, models);
        }

    }

    private static void benchmark(int[][] test, ArrayList<ChowLiu> models) {
        double correct = 0;
        for (int[] d : test) {
            double[] votes = new double[K];
            for (ChowLiu cl : models) {
                votes[cl.predict(d)] += cl.alpha;
            }
            int winner = 0;
            for (int i = 0; i < votes.length; i++) {
                winner = (votes[i] > votes[winner] ? i : winner);
            }
            if (d[label] == winner) correct++;
        }
        String log = "Chow-liu boosting round: " + models.size() + " Accuracy: " + correct / test.length + "\n";
        saveResult(log, "result.txt");
        System.out.println(log);
    }

    /**
     * Save the running result to file
     */
    private static boolean saveResult(String content, String filename) {
        boolean success = false;
        File file = new File(filename);
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
