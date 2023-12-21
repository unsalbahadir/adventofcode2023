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

    public int getSolution(List<String> lines) {
        List<Position> gardenPositions = getGardenPositions(lines);
        Position startingPosition = gardenPositions.getLast();

        return countPossiblePositionsAfterSteps(new HashSet<>(gardenPositions), startingPosition, 10);
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

        int steps = 0;
        while (steps < stepsToTake) {
            Set<Position> newVisitingPositions = new HashSet<>();
            for (Position visitingPosition : visitingPositions) {
                List<Position> adjacentGardenPositions = getAdjacentGardenPositions(gardenPositions, visitingPosition);
                newVisitingPositions.addAll(adjacentGardenPositions);
            }
            visitingPositions = newVisitingPositions;
            steps++;
        }

//        System.out.println("Possible positions: " + visitingPositions);
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

    private int countPossiblePositionsAfterSteps2(Set<Position> gardenPositions, Position startingPosition,
                                                  int stepsToTake) {
        Set<Position> visitingPositions = new HashSet<>();
        visitingPositions.add(startingPosition);

        Map<Position, List<Position>> adjacentPositionsOfPosition = new HashMap<>();

        int steps = 0;
        while (steps < stepsToTake) {
            Set<Position> newVisitingPositions = new HashSet<>();

            for (Position visitingPosition : visitingPositions) {
                List<Position> adjacentGardenPositions;
                if (adjacentPositionsOfPosition.containsKey(visitingPosition)) {
                    adjacentGardenPositions = adjacentPositionsOfPosition.get(visitingPosition);
                } else {
                    adjacentGardenPositions =
                            getAdjacentGardenPositions2(gardenPositions, visitingPosition);
                    adjacentPositionsOfPosition.put(visitingPosition, adjacentGardenPositions);
//                    List<Position> positionsOutsideInitial = adjacentGardenPositions.stream()
//                            .filter(pos -> isOutsideInitialGarden(pos, rowCount, columnCount))
//                            .toList();
//                    if (!positionsOutsideInitial.isEmpty()) {
//                        System.out.println("Positions outside initial garden: " + positionsOutsideInitial);
//                    }
                }

                newVisitingPositions.addAll(adjacentGardenPositions);
            }

            visitingPositions = newVisitingPositions;
            steps++;

            if (steps % 100 == 0) {
                System.out.println("Current steps: " + steps);
                System.out.println("Current possible positions: " + visitingPositions.size());
            }
        }
//        System.out.println(adjacentPositionsOfPosition);

//        System.out.println("Possible positions: " + visitingPositions);
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

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day21.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
