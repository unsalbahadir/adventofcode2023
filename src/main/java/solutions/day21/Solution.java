package solutions.day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Solution {
    private static int ROW_COUNT = 0;
    private static int COLUMN_COUNT = 0;


    private record Position(int row, int column) {
    }

    private record Farm(int row, int column, Set<Position> positions) {
    }

    public int getSolution(List<String> lines) {
        List<Position> gardenPositions = getGardenPositions(lines);
        Position startingPosition = gardenPositions.getLast();

        return countPossiblePositionsAfterSteps(new HashSet<>(gardenPositions), startingPosition, 500);
    }

    public int getSolution2(List<String> lines) {
        List<Position> gardenPositions = getGardenPositions(lines);
        Position startingPosition = gardenPositions.getLast();

        ROW_COUNT = lines.size();
        COLUMN_COUNT = lines.getFirst().length();

        return countPossiblePositionsAfterSteps2(new HashSet<>(gardenPositions), startingPosition, 500);
    }

    private List<Position> getGardenPositions(List<String> lines) {
        List<Position> gardenPositions = new ArrayList<>();
        Position startingPosition = null;

        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            for (int column = 0; column < line.length(); column++) {
                if (line.charAt(column) == '.') {
                    Position position = new Position(row, column);
                    gardenPositions.add(position);
                } else if (line.charAt(column) == 'S') {
                    startingPosition = new Position(row, column);
                }
            }
        }
        gardenPositions.add(startingPosition);
        return gardenPositions;
    }

    private int countPossiblePositionsAfterSteps(Set<Position> gardenPositions, Position startingPosition, int stepsToTake) {
        Set<Position> visitingPositions = new HashSet<>();
        visitingPositions.add(startingPosition);

        Set<Integer> possiblePositionCounts = new HashSet<>();
        boolean cycleStarted = false;
        Map<Boolean, Integer> results = new HashMap<>();

        Map<Position, List<Position>> adjacentPositionsOfPosition = new HashMap<>();

        int steps = 0;
        while (steps < stepsToTake) {
            Set<Position> newVisitingPositions = new HashSet<>();
            for (Position visitingPosition : visitingPositions) {
                List<Position> adjacentGardenPositions = adjacentPositionsOfPosition.get(visitingPosition);
                if (adjacentGardenPositions == null) {
                    adjacentGardenPositions = getAdjacentGardenPositions(gardenPositions, visitingPosition);
                    adjacentPositionsOfPosition.put(visitingPosition, adjacentGardenPositions);
                }
                newVisitingPositions.addAll(adjacentGardenPositions);
            }
            visitingPositions = newVisitingPositions;
            steps++;

            int possiblePositionCount = visitingPositions.size();

            // cycle detection
            if (possiblePositionCounts.contains(possiblePositionCount)) {
                // cycle starts
                boolean isEven = steps % 2 == 0;
                results.put(isEven, possiblePositionCount);
                if (cycleStarted) {
                    // have both results in the map now
                    System.out.println("Cycle end found, results for isEven: " + results);
                    return results.get(isEven);
                } else {
                    System.out.println("Possible cycle start found at step: " + steps);
                    cycleStarted = true;
                }
            } else {
                if (cycleStarted) {
                    cycleStarted = false;
                }
            }
            possiblePositionCounts.add(possiblePositionCount);
        }
        return visitingPositions.size();
    }

    private List<Position> getAdjacentGardenPositions(Set<Position> gardenPositions, Position position) {
        List<Position> adjacentPositions = List.of(
                new Position(position.row - 1, position.column),
                new Position(position.row, position.column + 1),
                new Position(position.row + 1, position.column),
                new Position(position.row, position.column - 1)
        );

        return adjacentPositions.stream()
                .filter(gardenPositions::contains)
                .toList();
    }

    private int countPossiblePositionsAfterSteps2(Set<Position> gardenPositions, Position startingPosition, int stepsToTake) {
        Set<Position> visitingPositions = new HashSet<>();
        visitingPositions.add(startingPosition);

//        Set<Integer> possiblePositionCounts = new HashSet<>();
//        boolean cycleStarted = false;
//        Map<Boolean, Integer> results = new HashMap<>();
//
        Map<Position, List<Position>> adjacentPositionsOfPosition = new HashMap<>();

        int steps = 0;
        while (steps < stepsToTake) {
            Set<Position> newVisitingPositions = new HashSet<>();
            for (Position visitingPosition : visitingPositions) {
                List<Position> adjacentGardenPositions = adjacentPositionsOfPosition.get(visitingPosition);
                if (adjacentGardenPositions == null) {
                    adjacentGardenPositions = getAdjacentGardenPositions2(gardenPositions, visitingPosition);
                    adjacentPositionsOfPosition.put(visitingPosition, adjacentGardenPositions);
                }
                newVisitingPositions.addAll(adjacentGardenPositions);
            }
//            Set<Position> positionsOutsideInitial = newVisitingPositions.stream()
//                    .filter(this::isOutsideInitialGarden)
//                    .collect(Collectors.toSet());

//            for (Position position : positionsOutsideInitial) {
//                int i = countPossiblePositionsAfterSteps2(gardenPositions, getProjectedPosition(position), stepsToTake - steps - 1);
//                otherFarmResults += i;
//            }
//            newVisitingPositions.removeAll(positionsOutsideInitial);

            int increaseInStep = newVisitingPositions.size() - visitingPositions.size();
            visitingPositions = newVisitingPositions;
            steps++;

            int possiblePositionCount = visitingPositions.size();
            System.out.println("Possible positions after " + steps + " steps:" + possiblePositionCount + " Increase in step: " + increaseInStep);

//            if (steps % 100 == 0) {
//                System.out.println("Possible positions after " + steps + " steps:" + possiblePositionCount);
//            }
//
//            // cycle detection
//            if (possiblePositionCounts.contains(possiblePositionCount)) {
//                // cycle starts
//                boolean isEven = steps % 2 == 0;
//                results.put(isEven, possiblePositionCount);
//                if (cycleStarted) {
//                    // have both results in the map now
//                    System.out.println("Cycle end found, results for isEven: " + results);
//                    return results.get(isEven) + otherFarmResults;
//                } else {
//                    System.out.println("Possible cycle start found at step: " + steps);
//                    cycleStarted = true;
//                }
//            } else {
//                if (cycleStarted) {
//                    cycleStarted = false;
//                }
//            }
//            possiblePositionCounts.add(possiblePositionCount);

//            if (!positionsOutsideInitial.isEmpty()) {
//                System.out.println("Step " + steps + ": Positions outside initial garden: " + positionsOutsideInitial);
//
//                List<Position> projectedPositions = positionsOutsideInitial.stream()
//                        .map(this::getProjectedPosition)
//                        .toList();
//
//                System.out.println("Step " + steps + ": Their projections: " + projectedPositions);
//            }
//            printVisitingPositions(newVisitingPositions, gardenPositions);
        }
        return visitingPositions.size();
    }

    private boolean isOutsideInitialGarden(Position position) {
        return position.row < 0 || position.row >= ROW_COUNT || position.column < 0 || position.column >= COLUMN_COUNT;
    }

    private List<Position> getAdjacentGardenPositions2(Set<Position> gardenPositions, Position position) {
        List<Position> adjacentPositions = List.of(
                new Position(position.row - 1, position.column),
                new Position(position.row, position.column + 1),
                new Position(position.row + 1, position.column),
                new Position(position.row, position.column - 1)
        );

        return adjacentPositions.stream()
                .filter(pos -> isGarden(gardenPositions, pos))
                .toList();
    }

    private boolean isGarden(Set<Position> gardenPositions, Position position) {
        return gardenPositions.contains(getProjectedPosition(position));
    }

    private Position getProjectedPosition(Position position) {
        int projectedRow = Math.floorMod(position.row, ROW_COUNT);
        int projectedColumn = Math.floorMod(position.column, COLUMN_COUNT);
        return new Position(projectedRow, projectedColumn);
    }

//    private void printVisitingPositions(Set<Position> newVisitingPositions, Set<Position> gardenPositions) {
//        int minRow = Integer.MAX_VALUE;
//        int maxRow = Integer.MIN_VALUE;
//        int minColumn = Integer.MAX_VALUE;
//        int maxColumn = Integer.MIN_VALUE;
//
//        for (Position newVisitingPosition : newVisitingPositions) {
//            minRow = Math.min(minRow, newVisitingPosition.row);
//            maxRow = Math.max(maxRow, newVisitingPosition.row);
//            minColumn = Math.min(minColumn, newVisitingPosition.column);
//            maxColumn = Math.max(maxColumn, newVisitingPosition.column);
//        }
//
//        for (int row = minRow; row <= maxRow; row++) {
//            for (int column = minColumn; column <= maxColumn; column++) {
//                Position position = new Position(row, column);
//                if (isOutsideInitialGarden(position)) {
//                    position = getProjectedPosition(position);
//                }
//                char charToPrint = '#';
//                if (newVisitingPositions.contains(position)) {
//                    charToPrint = 'O';
//                } else if (gardenPositions.contains(position)) {
//                    charToPrint = '.';
//                }
//                System.out.print(charToPrint);
//            }
//            System.out.println();
//        }
//    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day21.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
