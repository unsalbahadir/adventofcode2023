package solutions.day18;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Solution {

    private enum Direction {
        R,
        D,
        L,
        U
    }

    private record Position(long row, long column) {}

    private record Boundary(long minRow, long maxRow, long minColumn, long maxColumn) {}

    public int getSolution(List<String> lines) {
        List<Position> positions = new ArrayList<>();
        Position startingPosition = new Position(0, 0);
        positions.add(startingPosition);

        for (String line : lines) {
            doCommand(line, positions);
        }
        Boundary boundary = getBoundary(positions);

        System.out.println("Before dig:");
        printGrid(positions, boundary);

        positions = digInterior(positions, boundary);

        System.out.println("After dig:");
        printGrid(positions, boundary);

        return positions.size();
    }

    public int getSolution2(List<String> lines) {
        List<Position> positions = new ArrayList<>();
        Position startingPosition = new Position(0, 0);
        positions.add(startingPosition);

        for (String line : lines) {
            doCommand2(line, positions);
        }
        Boundary boundary = getBoundary(positions);

//        System.out.println("Before dig:");
//        printGrid(positions, boundary);

        positions = digInterior(positions, boundary);

//        System.out.println("After dig:");
//        printGrid(positions, boundary);

        return positions.size();
    }

    private void doCommand(String line, List<Position> positions) {
        String[] split = line.split(" ");
        Direction direction = Direction.valueOf(split[0]);
        int distance = Integer.parseInt(split[1]);

        for (int i = 1; i <= distance; i++) {
            Position currentPosition = positions.getLast();
            Position newPosition = getPositionInDirection(currentPosition, direction);
            if (!positions.contains(newPosition)) {
                positions.add(newPosition);
            }
        }
    }

    private void doCommand2(String line, List<Position> positions) {
        String[] split = line.split(" ");
        String hex = StringUtils.substringBetween(split[2], "#", ")");

        Direction[] values = Direction.values();
        int directionValue = Character.getNumericValue(hex.charAt(hex.length() - 1));
        Direction direction = values[directionValue];

        int distance = Integer.parseInt(hex.substring(0, 5), 16);

        for (long i = 1; i <= distance; i++) {
            Position currentPosition = positions.getLast();
            Position newPosition = getPositionInDirection(currentPosition, direction);
            if (!positions.contains(newPosition)) {
                positions.add(newPosition);
            }
        }
    }


    private Position getPositionInDirection(Position position, Direction direction) {
        return switch (direction) {
            case U -> new Position(position.row - 1, position.column);
            case R -> new Position(position.row, position.column + 1);
            case D -> new Position(position.row + 1, position.column);
            case L -> new Position(position.row, position.column - 1);
        };
    }

    private Boundary getBoundary(List<Position> positions) {
        long maxRow = Integer.MIN_VALUE;
        long maxColumn = Integer.MIN_VALUE;
        long minRow = Integer.MAX_VALUE;
        long minColumn = Integer.MAX_VALUE;
        for (Position position : positions) {
            maxRow = Math.max(maxRow, position.row);
            minRow = Math.min(minRow, position.row);
            maxColumn = Math.max(maxColumn, position.column);
            minColumn = Math.min(minColumn, position.column);
        }
        return new Boundary(minRow, maxRow, minColumn, maxColumn);
    }

    private List<Position> digInterior(List<Position> positions, Boundary boundary) {
        Set<Position> trenchOutline = new HashSet<>(positions);

        Position startingPosition = new Position(1, 1);

        Queue<Position> queue = new LinkedList<>();
        queue.add(startingPosition);

        while (!queue.isEmpty()) {
            Position poll = queue.poll();

            if (!trenchOutline.contains(poll) && isValid(poll, boundary)) {
                trenchOutline.add(poll);
                List<Position> adjacentPositions = getAdjacentPositions(poll);
                queue.addAll(adjacentPositions);
            }
        }

        return new ArrayList<>(trenchOutline);
    }

    private List<Position> getAdjacentPositions(Position position) {
        return List.of(
                new Position(position.row - 1, position.column),
                new Position(position.row, position.column + 1),
                new Position(position.row + 1, position.column),
                new Position(position.row, position.column - 1)
        );
    }

    private boolean isValid(Position position, Boundary boundary) {
        return position.row >= boundary.minRow && position.row <= boundary.maxRow
                && position.column >= boundary.minColumn && position.column <= boundary.maxColumn;
    }

    private void printGrid(List<Position> positions, Boundary boundary) {
        Set<Position> positionSet = new HashSet<>(positions);
        for (long row = boundary.minRow; row <= boundary.maxRow; row++) {
            for (long column = boundary.minColumn; column <= boundary.maxColumn; column++) {
                Position position = new Position(row, column);
                if (positionSet.contains(position)) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day18.txt"));
        System.out.println(solution.getSolution(lines));
//        System.out.println(solution.getSolution2(lines));
    }
}
