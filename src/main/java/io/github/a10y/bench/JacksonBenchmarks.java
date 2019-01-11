package io.github.a10y.bench;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Output from my 2017 rMBP:
 *
 * Benchmark                                 Mode  Cnt   Score    Error  Units
 * JacksonBenchmarks.bench_convertValue_int  avgt    3  67.962 ± 20.098  ns/op
 * JacksonBenchmarks.bench_readValue_int     avgt    3  93.220 ± 28.999  ns/op
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Measurement(iterations = 3, time = 10)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 1, time = 10)
public class JacksonBenchmarks {

    private ObjectMapper mapper;

    @Setup
    public void setup() {
        mapper = new ObjectMapper().enable(MapperFeature.ALLOW_COERCION_OF_SCALARS);
    }

    @Benchmark
    public int bench_readValue_int() throws Exception {
        return mapper.readValue("1", Integer.class);
    }

    @Benchmark
    public int bench_convertValue_int() {
        return mapper.convertValue("1", Integer.class);
    }
}
