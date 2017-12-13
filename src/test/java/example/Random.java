package example;

import mlpOld.DataLogger;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Pete Meltzer on 19/10/17.
 */
public class Random {

    @Test
    public void testingXOR() throws Throwable {

        SplittableRandom random = new SplittableRandom();

        for (int i = 0; i < 10; i++) {

            int a = random.nextInt(2);
            int b = random.nextInt(2);
            int c = a ^ b;

            System.out.println(String.format("a: %d, b:%d, c:%d", a, b, c));
        }
    }

    @Test
    public void streamTest() throws Throwable {

        Stream<String> stream = Stream.of("red", "blue", "white");
        Stream<String> upper = stream
                .map(String::toUpperCase)
                .peek(System.out::println);
        upper.collect(Collectors.toList());
    }

    @Test
    public void dateTest() {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd:kk-mm-ss").format(new Date()));

    }

    @Test
    public void dataLoggerTest() {

        DataLogger logger = new DataLogger("test", "a", "b", "c");

    }

    @Test
    public void stringPrinting() {
        int nTrials = 100;

        for (int i = 0; i < nTrials; i++) {
            String bar = String.join("", Collections.nCopies(i, "\u2588"));
            System.out.print(String.format("\r|%99s|", bar));
        }
    }

}
