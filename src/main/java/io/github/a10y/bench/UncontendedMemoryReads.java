package io.github.a10y.bench;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Test uncontended reads of an integer with different protections (atomic, volatile, non-volatile).
 *
 * Results on a 2017 rMBP:
 *
 * Benchmark                                  Mode  Cnt  Score   Error  Units
 * UncontendedMemoryReads.bench_readAtomic    avgt    3  2.217 ± 0.597  ns/op
 * UncontendedMemoryReads.bench_readDirect    avgt    3  2.192 ± 0.607  ns/op
 * UncontendedMemoryReads.bench_readVolatile  avgt    3  2.159 ± 0.117  ns/op
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Measurement(iterations = 3, time = 10)
@Warmup(iterations = 3, time = 4)
public class UncontendedMemoryReads {
    volatile int xVol = 0xFFFF00CC; // whatevs
    int x = 0xFFFF00CC; // whatevs
    AtomicInteger xAtomic = new AtomicInteger(0xFFFF00CC);

    @Benchmark
    public int bench_readVolatile() {
        return xVol + 1;
    }

    @Benchmark
    public int bench_readDirect() {
        return x + 1;
    }

    @Benchmark
    public int bench_readAtomic() {
        return xAtomic.get() + 1;
    }
}
