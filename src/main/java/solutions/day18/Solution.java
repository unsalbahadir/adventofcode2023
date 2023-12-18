package solutions.day18;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Solution {

    private enum Direction {
        R,
        D,
        L,
        U
    }

    private record Position(long row, long column) {}

    public long getSolution(List<String> lines) {
        List<Position> corners = new ArrayList<>();
        Position startingPosition = new Position(0, 0);
        corners.add(startingPosition);

        for (String line : lines) {
            addCorner(line, corners);
        }
        return getArea(corners);
    }

    public long getSolution2(List<String> lines) {
        List<Position> corners = new ArrayList<>();
        Position startingPosition = new Position(0, 0);
        corners.add(startingPosition);

        for (String line : lines) {
            addCorner2(line, corners);
        }
        return getArea(corners);
    }

    private void addCorner(String line, List<Position> corners) {
        String[] split = line.split(" ");
        Direction direction = Direction.valueOf(split[0]);
        int distance = Integer.parseInt(split[1]);

        Position lastCorner = corners.getLast();
        Position newPosition = getPositionInDirection(lastCorner, direction, distance);
        corners.add(newPosition);
    }

    private void addCorner2(String line, List<Position> corners) {
        String[] split = line.split(" ");
        String hex = StringUtils.substringBetween(split[2], "#", ")");

        Direction[] values = Direction.values();
        int directionValue = Character.getNumericValue(hex.charAt(hex.length() - 1));
        Direction direction = values[directionValue];

        int distance = Integer.parseInt(hex.substring(0, 5), 16);

        Position lastCorner = corners.getLast();
        Position newPosition = getPositionInDirection(lastCorner, direction, distance);
        corners.add(newPosition);
    }

    private Position getPositionInDirection(Position position, Direction direction, int distance) {
        return switch (direction) {
            case U -> new Position(position.row - distance, position.column);
            case R -> new Position(position.row, position.column + distance);
            case D -> new Position(position.row + distance, position.column);
            case L -> new Position(position.row, position.column - distance);
        };
    }

    private long getArea(List<Position> corners) {
        long area = 0;
        long circumference = 0;

        for (int i = 0; i < corners.size(); i++) {
            Position currentPosition = corners.get(i);

            Position nextPosition;
            if (i == corners.size() - 1) {
                nextPosition = corners.get(0);
            } else {
                nextPosition = corners.get(i + 1);
            }
            circumference += Math.abs(nextPosition.row - currentPosition.row) + Math.abs(nextPosition.column - currentPosition.column);
            area += (currentPosition.row * nextPosition.column) - (nextPosition.row * currentPosition.column);
        }
        System.out.println("Circumference:" + circumference);
        long result = Math.abs(area) / 2;
        System.out.println("Area before adding circumference: " + result);
        result += circumference / 2;
        result++;
        return result;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day18.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
