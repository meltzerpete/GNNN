package mlp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class DataLogger {

    private static final int defaultBufferSize = 1000;

    private File file;
    private int bufferSize;
    private int currentCount;
    private String[] cols;
    private BufferedWriter writer;

    public DataLogger(String name, int bufferSize, String... cols) {

        this.bufferSize = bufferSize;
        this.cols = cols;
        this.currentCount = 0;

        // flush buffer upon close
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        String timestamp = new SimpleDateFormat("yyyy-MM-dd::kk:mm:ss:SSS")
                                                            .format(new Date());

        try {
            File dir = new File("data");
            dir.mkdir();

            File innerDir = new File (name);
            innerDir.mkdir();

            file = new File(dir + "//"
                                + innerDir + "//"
                                + name + "_"
                                + timestamp + ".csv");
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Arrays.stream(cols)
                .reduce((acc, next) -> acc + "," + next)
                .ifPresent(headings -> {
                    try {
                        writer.append(headings);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


    }

    /**
     * Create DataLogger with default buffer size ({@value defaultBufferSize} lines)
     * @param name
     * @param cols
     */
    public DataLogger(String name, String... cols) {
        this(name, defaultBufferSize, cols);
    }

    public void append(Object... cols) {

        Arrays.stream(cols)
                .reduce((acc, next) -> acc + "," + next)
                .ifPresent(data -> {
                    try {
                        writer.append(data.toString());
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        if (++currentCount % bufferSize == 0) {
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
