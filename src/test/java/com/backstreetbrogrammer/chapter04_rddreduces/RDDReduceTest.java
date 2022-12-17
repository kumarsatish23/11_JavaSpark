package com.backstreetbrogrammer.chapter04_rddreduces;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RDDReduceTest {

    private static final List<Double> data = new ArrayList<>();
    private final int noOfIterations = 10;

    @BeforeAll
    static void beforeAll() {
        final var dataSize = 1_000_000;
        for (int i = 0; i < dataSize; i++) {
            data.add(100 * ThreadLocalRandom.current().nextDouble() + 47);
        }
        assertEquals(dataSize, data.size());
    }

    @Test
    @DisplayName("Test reduce operation using Spark RDD")
    void testReduceOperationUsingSparkRDD() {
        final var conf = new SparkConf().setAppName("RDDReduceTest").setMaster("local[*]");
        try (final var sc = new JavaSparkContext(conf)) {
            final var myRdd = sc.parallelize(data, 14);

            final Instant start = Instant.now();
            for (int i = 0; i < noOfIterations; i++) {
                final var sum = myRdd.reduce(Double::sum);
                System.out.println("[Spark RDD] SUM:" + sum);
            }
            final long timeElapsed = (Duration.between(start, Instant.now()).toMillis()) / noOfIterations;
            System.out.printf("[Spark RDD] time taken: %d ms%n%n", timeElapsed);
        }
    }

    @Test
    @DisplayName("Test reduce operation using Java Streams")
    void testReduceOperationUsingJavaStreams() {
        final Instant start = Instant.now();
        for (int i = 0; i < noOfIterations; i++) {
            final var sum = data.stream().reduce(Double::sum);
            System.out.println("[Java Streams] SUM:" + sum);
        }
        final long timeElapsed = (Duration.between(start, Instant.now()).toMillis()) / noOfIterations;
        System.out.printf("[Java Streams] time taken: %d ms%n%n", timeElapsed);
    }

    @Test
    @DisplayName("Test reduce operation using Java Parallel Streams")
    void testReduceOperationUsingJavaParallelStreams() {
        final Instant start = Instant.now();
        for (int i = 0; i < noOfIterations; i++) {
            final var sum = data.parallelStream().reduce(Double::sum);
            System.out.println("[Java Parallel Streams] SUM:" + sum);
        }
        final long timeElapsed = (Duration.between(start, Instant.now()).toMillis()) / noOfIterations;
        System.out.printf("[Java Parallel Streams] time taken: %d ms%n%n", timeElapsed);
    }
}
