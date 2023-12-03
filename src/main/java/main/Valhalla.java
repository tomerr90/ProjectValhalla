package main;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(
    jvmArgs = {
        "-XX:-TieredCompilation",
        "-Xms24g",
        "-XX:+EnablePrimitiveClasses"
})
public class Valhalla {

    public static final int NUM_OF_ELEMENTS = 1_000_000;
    private final Point[] points = new Point[NUM_OF_ELEMENTS];
    private final PrimitivePoint[] primitivePoints = new PrimitivePoint[NUM_OF_ELEMENTS];

    @Benchmark
    public void sort(Blackhole blackhole) {
        Arrays.sort(points, (p1, p2) -> {
            int c = Integer.compare(p1.x, p2.x);

            if (c != 0) {
                return c;
            }

            return Integer.compare(p1.y, p2.y);
        });

        blackhole.consume(points[0].x);
    }

    @Benchmark
    public void sortPrimitive(Blackhole blackhole) {
        Arrays.sort(primitivePoints, (p1, p2) -> {
            int c = Integer.compare(p1.x, p2.x);

            if (c != 0) {
                return c;
            }

            return Integer.compare(p1.y, p2.y);
        });

        blackhole.consume(primitivePoints[0].x);
    }

    @Benchmark
    public void acc(Blackhole blackhole) {
        Point acc = new Point(0,0);

        for (int i = 0; i < points.length; i++) {
            acc = acc.add(points[i]);
        }

        blackhole.consume(acc.x);
    }

    @Benchmark
    public void accPrimitive(Blackhole blackhole) {
        PrimitivePoint acc = new PrimitivePoint(0,0);

        for (int i = 0; i < primitivePoints.length; i++) {
            acc = acc.add(primitivePoints[i]);
        }

        blackhole.consume(acc.x);
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        Random r = new Random();

        for (int i = 0; i < NUM_OF_ELEMENTS; i++) {
            int x = r.nextInt();
            int y = r.nextInt();

            points[i] = new Point(x, y);
            primitivePoints[i] = new PrimitivePoint(x, y);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
            .include(Valhalla.class.getSimpleName())
            .build();

        new Runner(options).run();
    }

    private static class Point {

        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public Point add(Point p) {
            return new Point(x+p.x, y+p.y);
        }
    }

    private static primitive class PrimitivePoint {

        public int x;
        public int y;

        public PrimitivePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public PrimitivePoint add(PrimitivePoint p) {
            return new PrimitivePoint(x+p.x, y+p.y);
        }
    }
}
