package io.github.a10y.bench;

import com.sun.nio.file.ExtendedOpenOption;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Timing information for reading/writing data into a file.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(
        value = 1,
        jvmArgsAppend = {"-XX:+UnlockDiagnosticVMOptions",
                         "-XX:PrintAssemblyOptions=intel",
                         "-XX:CompileCommand=print,*UncontendedMemoryReads.bench_write*"})
@Measurement(iterations = 3, time = 10)
@Warmup(iterations = 3, time = 4)
public class DiskIOBenchmark {
    private static final int BLOCK_SIZE = 4_096; // Platform-dependent, works on 2017 rMBP.
    private static final int READ_LENGTH = BLOCK_SIZE * 256; // 1MiB read.

    // Explicitly NOT final to avoid DCE and constant folding.
    private File theFile;
    private InputStream istream;

    @Setup
    public void setup() throws IOException {
        // TODO(a10y): make sure this is being run in a "real" filesystem, not something like tmpfs backed by memory.
        theFile = Files.createTempFile("the-temp-file", "txt").toFile();
        theFile.deleteOnExit();

        // Write data in a number of times.
        var data = new byte[BLOCK_SIZE];
        for (var i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 128);
        }
    }

    @Setup(Level.Iteration)
    public void iterationSetup() throws IOException {
        // Setup a reader for the file before each iteration.
        // istream = Files.newInputStream(theFile.toPath());
        istream = Files.newInputStream(theFile.toPath(), ExtendedOpenOption.DIRECT);
    }

    @TearDown
    public void tearDown() {
        // Delete the file itself.
        theFile.delete();
        theFile = null;

        // We want to do this teardown for the entire benchmark.
        // Before each invocation we'd end up with this shit.
    }

    @Benchmark
    public byte[] bench_readDisk() throws IOException {
        istream.reset();
        return istream.readNBytes(BLOCK_SIZE);
    }
}
