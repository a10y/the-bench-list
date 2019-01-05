package io.github.a10y.bench;

import java.util.concurrent.TimeUnit;
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
 * Comparison between {@link String#format(String, Object...)} and simple string concatenation.
 *
 * Results on a 2017 rMBP:
 *
 * Benchmark                                  Mode  Cnt    Score    Error  Units
 * StringConcatBenchmark.bench_String_format  avgt    3  887.939 ± 69.697  ns/op
 * StringConcatBenchmark.bench_concat         avgt    3   24.468 ±  1.249  ns/op
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Measurement(iterations = 3, time = 5)
@Warmup(iterations = 3, time = 5)
public class StringConcatBenchmark {
    private String first = "Always";
    private String second = "Apprentice";
    private int two = 2;

    @Benchmark
    public String bench_String_format() {
        return String.format("%s %d there were, a Master and an %s", first, two, second);
    }

    @Benchmark
    public String bench_concat() {
        return first + " " + two + " there were, a Master and an " + second;
    }
}
