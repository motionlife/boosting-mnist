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
            domain[i] = 7;
        }
        domain[label] = K;

        //initialize weights
        double[] W = new double[train.length];
        for (int i = 0; i < W.length; i++) {
            W[i] = 1.0 / train.length;
        }

        //boosting-SAMME
        int M = 500;
        ArrayList<ChowLiu> models = new ArrayList<>(M);
        for (int i = 0; i < M; i++) {
            ChowLiu m = new ChowLiu(train, domain, label, W);
            double e = m.error;
            m.alpha = Math.log((1 / e - 1) * (K - 1));
            System.out.println("error=" + e + " alpha=" + m.alpha);
            models.add(m);
            for (int j = 0; j < train.length; j++) {
                W[j] = m.cache[j] == 0 ? W[j] / (K * (1 - e)) : W[j] * (K - 1) / (K * e);
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
        System.out.println("Chow-liu boosting round: " + models.size() + " Accuracy: " + correct / test.length + "\n");
    }
}
