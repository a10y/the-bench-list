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
 * This is a Java adaptation of the benchmarks that illustrate CPU Pipeline performance
 * at http://lolengine.net/blog/2011/9/17/playing-with-the-cpu-pipeline. Note that the numbers in the blog post
 * are reflective of a consumer CPU circa 2011.
 *
 * Results from running on 2017 rMBP in January 2019:
 *
 * Benchmark                       Mode  Cnt   Score   Error  Units
 * SineBenchmark.bench_MathDotSin  avgt    3  39.505 ± 1.954  ns/op
 * SineBenchmark.bench_sine1       avgt    3  13.247 ± 1.773  ns/op
 * SineBenchmark.bench_sine2       avgt    3   6.257 ± 1.201  ns/op
 * SineBenchmark.bench_sine3       avgt    3   6.343 ± 1.627  ns/op
 * SineBenchmark.bench_sine4       avgt    3   5.187 ± 1.669  ns/op
 * SineBenchmark.bench_sine5       avgt    3   5.423 ± 1.234  ns/op
 * SineBenchmark.bench_sine6       avgt    3   5.065 ± 1.172  ns/op
 * SineBenchmark.bench_sine7       avgt    3   4.991 ± 1.250  ns/op
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Measurement(iterations = 3, time = 10)
@Warmup(iterations = 3, time = 10)
public class SineBenchmark {
    private static double a0 = +1.0;
    private static double a1 = -1.666666666666580809419428987894207e-1;
    private static double a2 = +8.333333333262716094425037738346873e-3;
    private static double a3 = -1.984126982005911439283646346964929e-4;
    private static double a4 = +2.755731607338689220657382272783309e-6;
    private static double a5 = -2.505185130214293595900283001271652e-8;
    private static double a6 = +1.604729591825977403374012010065495e-10;
    private static double a7 = -7.364589573262279913270651228486670e-13;

    // Explicitly NOT final.
    double rad = Math.PI / 5.0;

    private double sine1(double x) {
        return a0 * x
                + a1 * x * x * x
                + a2 * x * x * x * x * x
                + a3 * x * x * x * x * x * x * x
                + a4 * x * x * x * x * x * x * x * x * x
                + a5 * x * x * x * x * x * x * x * x * x * x * x
                + a6 * x * x * x * x * x * x * x * x * x * x * x * x * x
                + a7 * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x;
    }

    private double sine2(double x) {
        double ret, y = x, x2 = x * x;
        ret = a0 * y; y *= x2;
        ret += a1 * y; y *= x2;
        ret += a2 * y; y *= x2;
        ret += a3 * y; y *= x2;
        ret += a4 * y; y *= x2;
        ret += a5 * y; y *= x2;
        ret += a6 * y; y *= x2;
        ret += a7 * y;
        return ret;
    }

    private double sine3(double x) {
        double x2 = x * x;
        return x * (a0 + x2 * (a1 + x2 * (a2 + x2 * (a3 + x2 * (a4 + x2 * (a5 + x2 * (a6 + x2 * a7)))))));
    }

    private double sine4(double x) {
        double x2 = x * x;
        double x4 = x2 * x2;
        double A = a0 + x4 * (a2 + x4 * (a4 + x4 * a6));
        double B = a1 + x4 * (a3 + x4 * (a5 + x4 * a7));
        return x * (A + x2 * B);
    }

    private double sine5(double x) {
        double x2 = x * x;
        double x4 = x2 * x2;
        double x6 = x4 * x2;
        double A = a0 + x6 * (a3 + x6 * a6);
        double B = a1 + x6 * (a4 + x6 * a7);
        double C = a2 + x6 * a5;
        return x * (A + x2 * B + x4 * C);
    }

    private double sine6(double x) {
        double x2 = x * x;
        double x4 = x2 * x2;
        double x8 = x4 * x4;
        double A = a0 + x2 * (a1 + x2 * (a2 + x2 * a3));
        double B = a4 + x2 * (a5 + x2 * (a6 + x2 * a7));
        return x * (A + x8 * B);
    }

    private double sine7(double x) {
        double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x2 * x2;
        double x8 = x4 * x4;
        double x9 = x8 * x;
        double A = x3 * (a1 + x2 * (a2 + x2 * a3));
        double B = a4 + x2 * (a5 + x2 * (a6 + x2 * a7));
        double C = a0 * x;
        return A + C + x9 * B;
    }

    @Benchmark
    public double bench_MathDotSin() {
        return Math.sin(rad);
    }

    @Benchmark
    public double bench_sine1() {
        return sine1(rad);
    }

    @Benchmark
    public double bench_sine2() {
        return sine2(rad);
    }

    @Benchmark
    public double bench_sine3() {
        return sine3(rad);
    }

    @Benchmark
    public double bench_sine4() {
        return sine4(rad);
    }

    @Benchmark
    public double bench_sine5() {
        return sine5(rad);
    }

    @Benchmark
    public double bench_sine6() {
        return sine6(rad);
    }

    @Benchmark
    public double bench_sine7() {
        return sine7(rad);
    }
}
