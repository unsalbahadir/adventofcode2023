package solutions.day24;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Solution {

    private static long MIN_BOUND = 200000000000000L;
    private static long MAX_BOUND = 400000000000000L;

    private record Position(double x, double y, double z) {

    }

    private record Velocity(double x, double y, double z) {

    }

    private static final class Hailstone {
        javax.swing.text.Position position;
        Velocity velocity;

        // line equation: y = m*x + b
        double m;
        double b;

        private Hailstone(javax.swing.text.Position position, Velocity velocity) {
            this.position = position;
            this.velocity = velocity;
            m = velocity.y / velocity.x;
            b = position.y - (position.x * m);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Hailstone) obj;
            return Objects.equals(this.position, that.position) &&
                    Objects.equals(this.velocity, that.velocity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, velocity);
        }

        @Override
        public String toString() {
            return "Hailstone[" +
                    "position=" + position + ", " +
                    "velocity=" + velocity + ']';
        }
    }

    public int getSolution(List<String> lines) {
        List<Hailstone> hailstones = getHailstones(lines);
//        System.out.println("Hailstones: ");
//        for (Hailstone hailstone : hailstones) {
//            System.out.println(hailstone);
//        }

        List<Pair<Hailstone, Hailstone>> futureIntersections = findFutureIntersections(hailstones);
//        System.out.println("Future intersections: " + futureIntersections);

        return futureIntersections.size();
    }

    private List<Hailstone> getHailstones(List<String> lines) {
        List<Hailstone> hailstones = new ArrayList<>();

        for (String line : lines) {
            String[] split = line.split(" @ ");

            String positionInput = split[0];
            List<Long> positionValues = Arrays.stream(positionInput.split(", "))
                    .map(s -> Long.parseLong(s.trim()))
                    .toList();

            Position position = new Position(positionValues.get(0), positionValues.get(1), positionValues.get(2));

            String velocityInput = split[1];
            List<Long> velocityValues = Arrays.stream(velocityInput.split(", "))
                    .map(s -> Long.parseLong(s.trim()))
                    .toList();
            Velocity velocity = new Velocity(velocityValues.get(0), velocityValues.get(1), velocityValues.get(2));

            Hailstone hailstone = new Hailstone(position, velocity);
            hailstones.add(hailstone);
        }

        return hailstones;
    }

    private List<Pair<Hailstone, Hailstone>> findFutureIntersections(List<Hailstone> hailstones) {
        List<Pair<Hailstone, Hailstone>> intersections = new ArrayList<>();

        for (int i = 0; i < hailstones.size(); i++) {
            for (int j = i + 1; j < hailstones.size(); j++) {
                Hailstone hailstone1 = hailstones.get(i);
                Hailstone hailstone2 = hailstones.get(j);
                boolean doHailstonesIntersectInFuture = doHailstonesIntersectInFuture(hailstone1, hailstone2);
                if (doHailstonesIntersectInFuture) {
                    intersections.add(Pair.of(hailstone1, hailstone2));
                }
            }
        }

        return intersections;
    }

    private boolean doHailstonesIntersectInFuture(Hailstone hailstone1, Hailstone hailstone2) {
        Position intersectionPosition = calculateIntersectionPosition(hailstone1, hailstone2);
//        System.out.println("Intersection point of " + hailstone1 + " and " + hailstone2 + ": " + intersectionPosition);

        if (intersectionPosition == null) {
            return false;
        }

        if (intersectionPosition.x < MIN_BOUND || intersectionPosition.y < MIN_BOUND
                || intersectionPosition.x > MAX_BOUND || intersectionPosition.y > MAX_BOUND) {
            return false;
        }

        double diff1 = intersectionPosition.x - hailstone1.position.x;
        double time1 = diff1 / hailstone1.velocity.x;
        if (time1 < 0) {
            return false;
        }

        double diff2 = intersectionPosition.x - hailstone2.position.x;
        double time2 = diff2 / hailstone2.velocity.x;
        if (time2 < 0) {
            return false;
        }

        return true;
    }

    private Position calculateIntersectionPosition(Hailstone hailstone1, Hailstone hailstone2) {
        return calculateIntersectionPosition(hailstone1.m, hailstone1.b, hailstone2.m, hailstone2.b);
    }

    // y = m*x + b
    private Position calculateIntersectionPosition(double m1, double b1, double m2, double b2) {
        if (m1 == m2) {
            return null;
        }

        double x = (b2 - b1) / (m1 - m2);
        double y = m1 * x + b1;

        return new Position(x, y, 0);
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day24.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
