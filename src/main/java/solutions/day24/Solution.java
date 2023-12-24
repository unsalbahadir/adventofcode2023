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
        Position position;
        Velocity velocity;

        // line equation: y = m*x + b
        double m;
        double b;

        private Hailstone(Position position, Velocity velocity) {
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

        List<Pair<Hailstone, Hailstone>> futureIntersections = findFutureIntersections(hailstones);

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

    public long getSolution2(List<String> lines) {
        List<Hailstone> hailstones = getHailstones(lines);
        List<Hailstone> hailstonesWithMatchingXVelocity = getHailstonesWithMatchingXVelocity(hailstones);
        Position rockPosition = findRockPosition(hailstones, hailstonesWithMatchingXVelocity);

        if (rockPosition == null) {
            return -1;
        }
        return (long) (rockPosition.x + rockPosition.y + rockPosition.z);
    }

    private List<Hailstone> getHailstonesWithMatchingXVelocity(List<Hailstone> hailstones) {
        for (int i = 0; i < hailstones.size(); i++) {
            for (int j = i + 1; j < hailstones.size(); j++) {
                Hailstone firstHailstone = hailstones.get(i);
                Hailstone secondHailstone = hailstones.get(j);
                if (firstHailstone.velocity.x == secondHailstone.velocity.x) {
                    return List.of(firstHailstone, secondHailstone);
                }
            }
        }
        return List.of(hailstones.get(0), hailstones.get(1));
    }

    private Position findRockPosition(List<Hailstone> hailstones, List<Hailstone> hailstonesForEquation) {
        for (double vx = -300; vx < 300; vx++) {
            for (double vy = -300; vy < 300; vy++) {
                for (double vz = -300; vz < 300; vz++) {
                    Position rockPosition = tryVelocity(hailstones, hailstonesForEquation, new Velocity(vx, vy, vz));
                    if (rockPosition != null) {
                        return rockPosition;
                    }
                }
            }
        }
        return null;
    }

    // px, py, pz = rock position
    // vx, vy, vz = rock velocity
    // h1x, h1y, h1z = hailstone 1 position
    // h1vx, h1vy, h1vz = hailstone 1 velocity
    // h2x, h2y, h2z = hailstone 2 position
    // h2vx, h2vy, h2vz = hailstone 2 velocity
    // t1 = time taken for rock and hailstone 1 intersection
    // t2 = time taken for rock and hailstone 2 intersection

    // px + (vx * t1) = h1x + (h1vx * t1)
    // h1vx = h2vx -> hvx
    // px = h1x + t1 * (hvx - vx) = h2x + t2 * (h2vx - vx)
    // (hvx - vx) -> a
    // t2 = ((h1x - h2x) / a) + t1

    // py = h1y + t1 * (hv1y - vy) = h2y + t2 * (hv2y - vy)
    // (hv1y - vy) -> b, (hv2y - vy) -> c
    // t2 = (h1y - h2y + (t1 * b) / c

    // ((h1x - h2x) / a) + t1 = (h1y - h2y + (t1 * b) / c
    // t1 - ((t1 * b) / c) = ((h1y - h2y) / c) - ((h1x - h2x) / a)
    // t1 * (c - b) = (a * (h1y - h2y) - (c * (h1x - h2x))) / a
    // t1 = (a * (h1y - h2y) - (c * (h1x - h2x))) / (a * (c - b))

    private Position tryVelocity(List<Hailstone> hailstones, List<Hailstone> hailstonesForEquation, Velocity rockVelocity) {
        Hailstone hailstone1 = hailstonesForEquation.getFirst();
        Hailstone hailstone2 = hailstonesForEquation.getLast();

        double vx = rockVelocity.x;
        double vy = rockVelocity.y;
        double vz = rockVelocity.z;

        double hv1x = hailstone1.velocity.x; // same as hv2x
        double hv1y = hailstone1.velocity.y;
        double hv1z = hailstone1.velocity.z;

        double hv2y = hailstone2.velocity.y;
        double hv2z = hailstone2.velocity.z;

        double h1x = hailstone1.position.x;
        double h1y = hailstone1.position.y;
        double h1z = hailstone1.position.z;

        double h2x = hailstone2.position.x;
        double h2y = hailstone2.position.y;
        double h2z = hailstone2.position.z;

        double a = hv1x - vx;
        double b = hv1y - vy;
        double c = hv2y - vy;

        // t1 = (a * (h1y - h2y) - (c * (h1x - h2x))) / (a * (c - b))
        double t1 = (a * (h1y - h2y) - (c * (h1x - h2x))) / (a * (c - b));

        // px = h1x + t1 * (hvx - vx)
        double px = h1x + (t1 * a);
        double py = h1y + (t1 * (hv1y - vy));
        double pz = h1z + (t1 * (hv1z - vz));

        Position rockPosition = new Position(px, py, pz);
        Hailstone rock = new Hailstone(rockPosition, rockVelocity);

        // check if it can hit all hailstones
        for (Hailstone hailstone : hailstones) {
            boolean canHit = canHit(hailstone, rock);
            if (!canHit) {
                return null;
            }
        }

        System.out.println("Valid rock found: " + rock);
        return rockPosition;
    }

    private boolean canHit(Hailstone hailstone, Hailstone rock) {
        double timeToHit = getTimeToHit(hailstone, rock);

        return getReachedPositionXAfterTime(hailstone, timeToHit) == getReachedPositionXAfterTime(rock, timeToHit)
                && getReachedPositionYAfterTime(hailstone, timeToHit) == getReachedPositionYAfterTime(rock, timeToHit)
                && getReachedPositionZAfterTime(hailstone, timeToHit) == getReachedPositionZAfterTime(rock, timeToHit);
    }

    // px + (vx * t) = hx + (hvx * t)
    // t = (hx - px) / (vx - hvx)
    private double getTimeToHit(Hailstone hailstone, Hailstone rock) {
        double hx = hailstone.position.x;
        double px = rock.position.x;

        double vx = rock.velocity.x;
        double hvx = hailstone.velocity.x;

        return (hx - px) / (vx - hvx);
    }

    private double getReachedPositionXAfterTime(Hailstone hailstone, double time) {
        return hailstone.position.x + (hailstone.velocity.x * time);
    }

    private double getReachedPositionYAfterTime(Hailstone hailstone, double time) {
        return hailstone.position.y + (hailstone.velocity.y * time);
    }

    private double getReachedPositionZAfterTime(Hailstone hailstone, double time) {
        return hailstone.position.z + (hailstone.velocity.z * time);
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day24.txt"));
//        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
