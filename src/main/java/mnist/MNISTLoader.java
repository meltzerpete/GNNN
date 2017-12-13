package mnist;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MNISTLoader {

    private DataInputStream imageReader;
    private DataInputStream labelReader;
    private int nRows;
    private int nCols;
    private int numImages;
    private int currentImage = 0;

    public MNISTLoader() {

        try {
            imageReader = new DataInputStream(new FileInputStream(new File("/home/pete/MNIST/train-images-idx3-ubyte")));
            labelReader = new DataInputStream(new FileInputStream(new File("/home/pete/MNIST/train-labels-idx1-ubyte")));

            int magicNumber = imageReader.readInt();
            numImages = imageReader.readInt();

            nRows = imageReader.readInt();
            nCols = imageReader.readInt();

            String imageTemplate = "IMAGES - Magic number: %d, Number of images: %d\n" +
                    "Each images is %d x %d (rows x cols).";
            System.out.println(String.format(imageTemplate, magicNumber, numImages, nRows, nCols));

            int labelsMagicNumber = labelReader.readInt();
            int numItems = labelReader.readInt();

            String labelTemplate = "LABELS - Magic number: %d, Number of labels: %d";
            System.out.println(String.format(labelTemplate, labelsMagicNumber, numItems));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Example {

        private int target;
        private int[] image;

        public Example(int target, int[] image) {
            this.target = target;
            this.image = image;
        }

        public int getTarget() {
            return target;
        }

        public int[] getImage() {
            return image;
        }

        public int getPixel(int i) {
            return image[i];
        }
    }

    boolean hasNext() {
        return currentImage < numImages;
    }

    Example nextExample() {

        int[] image = new int[nRows * nCols];
        int target = 0;

        try {
            for (int i = 0; i < image.length; i++) {
                image[i] = imageReader.readUnsignedByte();
            }
            target = labelReader.readUnsignedByte();
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentImage++;
        return new Example(target, image);
    }
}
