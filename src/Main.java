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

        int[] train_labels = MnistReader.getLabels("C:/Users/HaoXiong/IdeaProjects/ChowLiuBoosting/data/MNIST/train-labels-idx1-ubyte");
        List<int[][]> train_imgs = MnistReader.getImages("C:/Users/HaoXiong/IdeaProjects/ChowLiuBoosting/data/MNIST/train-images-idx3-ubyte");
        int[] test_labels = MnistReader.getLabels("C:/Users/HaoXiong/IdeaProjects/ChowLiuBoosting/data/MNIST/t10k-labels-idx1-ubyte");
        List<int[][]> test_imgs = MnistReader.getImages("C:/Users/HaoXiong/IdeaProjects/ChowLiuBoosting/data/MNIST/t10k-images-idx3-ubyte");
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
        WeightedData[] trainset = new WeightedData[train.length];
        for (int i = 0; i < train.length; i++) {
            double w = 1.0 / train.length;
            trainset[i] = new WeightedData(train[i], w, true);
        }

        //boosting-SAMME
        int M = 500;
        ArrayList<ChowLiu> models = new ArrayList<>(M);
        for (int i = 0; i < M; i++) {
            ChowLiu m = new ChowLiu(trainset, domain, label);
            double e = m.error;
            m.alpha = Math.log((1 / e - 1) * (K - 1));
            System.out.println("error=" + e + " alpha=" + m.alpha);
            models.add(m);
            for (WeightedData wd : trainset) {
                wd.weight = wd.pass ? wd.weight / (K * (1 - e)) : wd.weight * (K - 1) / (K * e);
            }
            benchmark(test, models);
        }


    }

    private static void benchmark(int[][] test, ArrayList<ChowLiu> models) {
        double correct = 0;
        for (int[] d : test) {
            double[] votes = new double[K];
            for (ChowLiu cl : models) {
                votes[ChowLiu.predict(d, cl)] += cl.alpha;
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
