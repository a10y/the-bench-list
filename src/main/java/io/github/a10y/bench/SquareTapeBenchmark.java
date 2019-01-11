package io.github.a10y.bench;

import com.squareup.tape2.QueueFile;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Measure the throughput we can achieve on a single one of these, mapped to a file.
 */
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@Fork(1)
@Measurement(iterations = 3, time = 10)
@Warmup(iterations = 3, time = 4)
public class SquareTapeBenchmark {
    private static final int RANDOM_BYTES = 512;
    private static final byte[] randomData = new byte[RANDOM_BYTES];

    static {
        // Initialize the static data.
        ThreadLocalRandom.current().nextBytes(randomData);
    }

    private File theFile;
    private QueueFile queueFile;

    @Setup
    public void setup() throws Exception {
        // Delete the file, and let Square create it.
        theFile = Files.createTempFile("SquareTapeBenchmark", ".tape").toFile();
        theFile.delete(); // QueueFile builder creates the file.
        queueFile = new QueueFile.Builder(theFile).build();
    }

    @TearDown
    public void tearDown() {
        theFile.delete();
        theFile = null;
        queueFile = null;
    }

    // TODO(aduffy): make sure the add isn't optimized away?
    @Benchmark
    @Threads(8)
    public synchronized void bench_append() throws Exception {
        queueFile.add(randomData);
    }
}
