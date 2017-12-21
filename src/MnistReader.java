import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class MnistReader {
    public static final int LABEL_FILE_MAGIC_NUMBER = 2049;
    public static final int IMAGE_FILE_MAGIC_NUMBER = 2051;

    public static int[] getLabels(String infile) {

        ByteBuffer bb = loadFileToByteBuffer(infile);

        assertMagicNumber(LABEL_FILE_MAGIC_NUMBER, bb.getInt());

        int numLabels = bb.getInt();
        int[] labels = new int[numLabels];

        for (int i = 0; i < numLabels; ++i)
            labels[i] = bb.get() & 0xFF; // To unsigned

        return labels;
    }

    public static List<int[][]> getImages(String infile) {
        ByteBuffer bb = loadFileToByteBuffer(infile);

        assertMagicNumber(IMAGE_FILE_MAGIC_NUMBER, bb.getInt());

        int numImages = bb.getInt();
        int numRows = bb.getInt();
        int numColumns = bb.getInt();
        List<int[][]> images = new ArrayList<>();

        for (int i = 0; i < numImages; i++)
            images.add(readImage(numRows, numColumns, bb));

        return images;
    }

    private static int[][] readImage(int numRows, int numCols, ByteBuffer bb) {
        int[][] image = new int[numRows][];
        for (int row = 0; row < numRows; row++)
            image[row] = readRow(numCols, bb);
        return image;
    }

    private static int[] readRow(int numCols, ByteBuffer bb) {
        int[] row = new int[numCols];
        for (int col = 0; col < numCols; ++col)
            row[col] = bb.get() & 0xFF; // To unsigned
        return row;
    }

    private static void assertMagicNumber(int expectedMagicNumber, int magicNumber) {
        if (expectedMagicNumber != magicNumber) {
            switch (expectedMagicNumber) {
                case LABEL_FILE_MAGIC_NUMBER:
                    throw new RuntimeException("This is not a label file.");
                case IMAGE_FILE_MAGIC_NUMBER:
                    throw new RuntimeException("This is not an image file.");
                default:
                    throw new RuntimeException(
                            format("Expected magic number %d, found %d", expectedMagicNumber, magicNumber));
            }
        }
    }

    /*******
     * Just very ugly utilities below here. Best not to subject yourself to
     * them. ;-)
     ******/

    private static ByteBuffer loadFileToByteBuffer(String infile) {
        return ByteBuffer.wrap(loadFile(infile));
    }

    private static byte[] loadFile(String infile) {
        try {
            RandomAccessFile f = new RandomAccessFile(infile, "r");
            FileChannel chan = f.getChannel();
            long fileSize = chan.size();
            ByteBuffer bb = ByteBuffer.allocate((int) fileSize);
            chan.read(bb);
            bb.flip();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int i = 0; i < fileSize; i++)
                baos.write(bb.get());
            chan.close();
            f.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String renderImage(int[][] image) {
        StringBuilder sb = new StringBuilder();

        for (int[] row : image) {
            sb.append("|");
            for (int pixelVal : row) {
                if (pixelVal == 0)
                    sb.append(" ");
                else if (pixelVal < 256 / 3)
                    sb.append(".");
                else if (pixelVal < 2 * (256 / 3))
                    sb.append("x");
                else
                    sb.append("X");
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    public static int[][] toVectors(List<int[][]> images, int[] lables) {
        int dim = 28;
        int vlen = dim * dim + 1;
        int[][] result = new int[lables.length][vlen];
        for (int i = 0; i < lables.length; i++) {
            int[][] img = images.get(i);
            for (int j = 0; j < dim; j++) {
                for (int k = 0; k < dim; k++) {
                    int scale = img[j][k];
                    //result[i][dim * j + k] = scale == 0 ? 0 : (scale < 7 ? 1 : (scale < 17 ? 2 : (scale < 47 ? 3 : (scale < 107 ? 4 : (scale < 197 ? 5 : 6)))));
                    result[i][dim * j + k] = scale < 7 ? 0 :1;
                }
            }
            result[i][vlen - 1] = lables[i];
        }
        return result;
    }
}