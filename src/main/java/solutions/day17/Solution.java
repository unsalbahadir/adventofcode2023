package solutions.day17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Solution {

    private static final class Position {
        final int row;
        final int column;
        int distanceFromStart = Integer.MAX_VALUE;
        Direction directionFromPrevious;
        int timesMovedStraight = 1;

        private Position(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public Position(int row, int column, Direction directionFromPrevious) {
            this.row = row;
            this.column = column;
            this.directionFromPrevious = directionFromPrevious;
        }

        public Position(int row, int column, Direction directionFromPrevious, int timesMovedStraight) {
            this.row = row;
            this.column = column;
            this.directionFromPrevious = directionFromPrevious;
            this.timesMovedStraight = timesMovedStraight;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Position) obj;
            return this.row == that.row &&
                    this.column == that.column &&
                    this.directionFromPrevious == that.directionFromPrevious &&
                    this.timesMovedStraight == that.timesMovedStraight;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column, directionFromPrevious, timesMovedStraight);
        }

        @Override
        public String toString() {
            return "Position[" +
                    "row=" + row + ", " +
                    "column=" + column + ", " +
                    "distanceFromStart=" + distanceFromStart + ", " +
                    "directionFromPrevious=" + directionFromPrevious + ", " +
                    "timesMovedStraight=" + timesMovedStraight +
                    ']';
        }
    }

    private enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST;

        static Direction getOppositeDirection(Direction direction) {
            return switch (direction) {
                case NORTH -> Direction.SOUTH;
                case EAST -> Direction.WEST;
                case SOUTH -> Direction.NORTH;
                case WEST -> Direction.EAST;
            };
        }
    }

    public int getSolution(List<String> lines) {
        int[][] positionCosts = getPositionCosts(lines);
        int maxRow = lines.size() - 1;
        int maxColumn = lines.getFirst().length() - 1;

//        int minCostToEnd = getMinCostToEnd(grid, startingPosition, null,
//                0, 0, new HashSet<>());
        int minCostToEnd = getMinCostToEnd(positionCosts, maxRow, maxColumn);
        return minCostToEnd;
    }

    private int getMinCostToEnd(int[][] positionCosts, int maxRow, int maxColumn) {
//        Map<Position, Map<Direction, Integer>> distancesOfPositionByDirection = new HashMap<>();
        Map<Position, Integer> distances = new HashMap<>();
        for (int row = 0; row < positionCosts.length; row++) {
            for (int column = 0; column < positionCosts[row].length; column++) {
                for (Direction direction : Direction.values()) {
                    for (int timesMovedStraight = 1; timesMovedStraight <= 3; timesMovedStraight++) {
                        Position positionInDirection = new Position(row, column, direction, timesMovedStraight);
                        distances.put(positionInDirection, Integer.MAX_VALUE);
                    }
                }
            }
        }

        Comparator<Position> positionComparator = (Comparator.comparing(position -> position.distanceFromStart));
        PriorityQueue<Position> positionsQueue = new PriorityQueue<>(positionComparator);

        Position startingPosition = new Position(0, 0, Direction.EAST);
        startingPosition.distanceFromStart = 0;

        distances.put(startingPosition, 0);

        positionsQueue.add(startingPosition);

        Map<Position, Position> previousPositions = new HashMap<>();

        while (!positionsQueue.isEmpty()) {
            // go through adjacent positions
            Position currentPosition = positionsQueue.poll();
//            Position previousPosition = previousPositions.get(currentPosition);
//            int timesMovedStraight = countTimesMovedStraight(previousPositions, currentPosition, previousPosition);
//            if (timesMovedStraight != currentPosition.timesMovedStraight) {
//                System.out.println("Wrong");
//            }
//            if (currentPosition.row == maxRow && currentPosition.column == maxColumn) {
//                System.out.println("End position:" + currentPosition);
//            }

            int timesMovedStraight = currentPosition.timesMovedStraight;

            List<Position> adjacentPositions = getAdjacentPositions(currentPosition,
                    timesMovedStraight < 3, maxRow, maxColumn);

            for (Position adjacentPosition : adjacentPositions) {
//                Position actualAdjacentPosition = findPosition(distances.keySet(), adjacentPosition);
                int possibleNewDistance = distances.get(currentPosition) + positionCosts[adjacentPosition.row][adjacentPosition.column];
                int currentDistanceByDirection = distances.get(adjacentPosition);

                if (possibleNewDistance < currentDistanceByDirection) {
                    adjacentPosition.distanceFromStart = possibleNewDistance;
//                    actualAdjacentPosition.directionFromPrevious = adjacentPosition.directionFromPrevious;
//                    actualAdjacentPosition.timesMovedStraight = adjacentPosition.timesMovedStraight;

                    previousPositions.put(adjacentPosition, currentPosition);
                    distances.put(adjacentPosition, possibleNewDistance);
                    if (!positionsQueue.contains(adjacentPosition)) {
                        positionsQueue.add(adjacentPosition);
                    }
                }
            }
        }
//        printGrid(distances.keySet(), maxRow, maxColumn);

        Position endPosition = findPosition(distances, new Position(maxRow, maxColumn));
        endPosition.distanceFromStart = distances.get(endPosition);
//        printPathTakenToStart(previousPositions, endPosition);
        return endPosition.distanceFromStart;
    }

    private int countTimesMovedStraight(Map<Position, Position> previousPositions, Position currentPosition, Position previousPosition) {
        int timesMovedStraight = 1; // always at least one
        if (previousPosition == null) {
            return timesMovedStraight;
        }
        Direction direction = previousPosition.directionFromPrevious;
        while (direction == currentPosition.directionFromPrevious) {
            timesMovedStraight++;
            previousPosition = previousPositions.get(previousPosition);
            if (previousPosition == null) {
                direction = null;
            } else {
                direction = previousPosition.directionFromPrevious;
            }
        }
        return timesMovedStraight;
    }

    private List<Position> getAdjacentPositions(Position position, boolean canMoveStraight, int maxRow, int maxColumn) {
        List<Position> possibleAdjacentPositions = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (direction == Direction.getOppositeDirection(position.directionFromPrevious)) { // can't go back
                continue;
            }
            if (!canMoveStraight && direction == position.directionFromPrevious) { // can't go straight anymore
                continue;
            }

            Position positionInDirection = getPositionInDirection(position, direction);
            if (isValid(positionInDirection, maxRow, maxColumn)) {
                if (direction == position.directionFromPrevious) {
                    positionInDirection.timesMovedStraight = position.timesMovedStraight + 1;
                }
                possibleAdjacentPositions.add(positionInDirection);
            }
        }

        return possibleAdjacentPositions;
    }

    private Position getPositionInDirection(Position position, Direction direction) {
        return switch (direction) {
            case NORTH -> new Position(position.row - 1, position.column, direction);
            case EAST -> new Position(position.row, position.column + 1, direction);
            case SOUTH -> new Position(position.row + 1, position.column, direction);
            case WEST -> new Position(position.row, position.column - 1, direction);
        };
    }

    private Position getPositionInSameDirection(Position position, Position previousPosition) {
        if (position.row == previousPosition.row) {
            if (position.column > previousPosition.column) {
                return new Position(position.row, position.column + 1);
            } else {
                return new Position(position.row, position.column - 1);
            }
        } else { // same column
            if (position.row > previousPosition.row) {
                return new Position(position.row + 1, position.column);
            } else {
                return new Position(position.row - 1, position.column);
            }
        }
    }

    private boolean isValid(Position position, int maxRow, int maxColumn) {
        return position.row >= 0 && position.row <= maxRow && position.column >= 0 && position.column <= maxColumn;
    }

    private int[][] getPositionCosts(List<String> lines) {
        int[][] positionCosts = new int[lines.size()][lines.getFirst().length()];

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                positionCosts[i][j] = Character.getNumericValue(c);
            }
        }
        return positionCosts;
    }

    private Position findPosition(Map<Position, Integer> positions, Position position) {
        return positions.entrySet().stream()
                .filter(entry -> entry.getKey().row == position.row && entry.getKey().column == position.column)
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private void printPathTakenToStart(Map<Position, Position> previousPositions, Position endPosition) {
        char[][] grid = new char[endPosition.row + 1][endPosition.column + 1];
        int[][] distances = new int[endPosition.row + 1][endPosition.column + 1];
        for (char[] chars : grid) {
            Arrays.fill(chars, '.');
        }
        grid[endPosition.row][endPosition.column] = getCharacterOfDirection(endPosition);
        distances[endPosition.row][endPosition.column] = endPosition.distanceFromStart;

        Set<Position> visited = new HashSet<>();
        Position previousPosition = endPosition;
        while (previousPositions.get(previousPosition) != null) {
            previousPosition = previousPositions.get(previousPosition);
            if (visited.contains(previousPosition)) {
                break;
            }
            char c = getCharacterOfDirection(previousPosition);
            grid[previousPosition.row][previousPosition.column] = c;
            distances[previousPosition.row][previousPosition.column] = previousPosition.distanceFromStart;
            visited.add(previousPosition);
            
//            System.out.println(previousPosition);
        }
        System.out.println("Path taken: ");
        for (char[] chars : grid) {
            System.out.println(new String(chars));
        }

        System.out.println("Distances: ");
        for (int[] distance : distances) {
            System.out.println(Arrays.toString(distance));
        }
    }

    private static char getCharacterOfDirection(Position previousPosition) {
        return switch (previousPosition.directionFromPrevious) {
            case NORTH -> '^';
            case EAST -> '>';
            case SOUTH -> 'v';
            case WEST -> '<';
        };
    }

    private void printGrid(Set<Position> positions, int maxRow, int maxColumn) {
        int[][] grid = new int[maxRow + 1][maxColumn + 1];
        for (Position position : positions) {
            grid[position.row][position.column] = position.distanceFromStart;
        }
        System.out.println("Distances: ");
        for (int[] ints : grid) {
            System.out.println(Arrays.toString(ints));
        }
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day17.txt"));
        System.out.println(solution.getSolution(lines));

//        List<String> linesExample = Files.readAllLines(Paths.get("inputs/day17-2.txt"));
//        int result = 0;
//        for (int i = 0; i < linesExample.size(); i++) {
//            String lineExample = linesExample.get(i);
//            for (int j = 0; j < lineExample.length(); j++) {
//                char c = lineExample.charAt(j);
//                if (!Character.isDigit(c)) {
//                    result += Character.getNumericValue(lines.get(i).charAt(j));
//                    System.out.print(c);
//                } else {
//                    System.out.print('.');
//                }
//            }
//            System.out.println();
//        }
//        System.out.println("Result from example: " + result);
    }
}