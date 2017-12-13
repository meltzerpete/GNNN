package mnist;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteToCSV {

    public static void go() {

        File output = new File("/home/pete/MNISTLoader/train_output.csv");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(output));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("FAIL");
        }
        CSVWriter csvWriter = new CSVWriter(writer);

        MNISTLoader loader = new MNISTLoader();

        while (loader.hasNext()) {

            MNISTLoader.Example example = loader.nextExample();

            csvWriter.writeNext(new String[]{});
        }
    }
}
