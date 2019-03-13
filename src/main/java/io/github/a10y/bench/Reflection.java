package io.github.a10y.bench;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
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

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Measurement(iterations = 3, time = 10)
@Warmup(iterations = 3, time = 10)
public class Reflection {
    private static final ReflectTest REFLECT_TEST = new ReflectTest();
    private static final Method METHOD;
    private static final MethodHandle HANDLE;

    static {
        try {
            METHOD = ReflectTest.class.getDeclaredMethod("getTheAnswer");
            METHOD.setAccessible(true);
            HANDLE = MethodHandles.lookup()
                    .findVirtual(ReflectTest.class, "getTheAnswer", MethodType.methodType(String.class))
                    .bindTo(REFLECT_TEST);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public Object reflection() throws Throwable {
        return METHOD.invoke(REFLECT_TEST);
    }

    @Benchmark
    public Object methodHandle() throws Throwable {
        return HANDLE.invoke();
    }

    @Benchmark
    public Object methodHandleExact() throws Throwable {
        return HANDLE.invokeExact();
    }

    @Benchmark
    public Object directCall() {
        return REFLECT_TEST.getTheAnswer();
    }

    static class ReflectTest {
        public String getTheAnswer() {
            return "the answer";
        }
    }
}
