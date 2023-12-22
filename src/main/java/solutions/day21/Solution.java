package solutions.day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {
    private static int ROW_COUNT = 0;
    private static int COLUMN_COUNT = 0;


    private record Position(int row, int column) {
    }

    private static final class Farm {
        Position farmPosition;
        Set<Position> visitingPositions;

        Farm parentFarm;

        // cycle
        Set<Integer> possiblePositionCounts = new HashSet<>();
        boolean cycleStarted = false;
        Map<Boolean, Integer> results = new HashMap<>();

        private Farm(Position farmPosition, Set<Position> visitingPositions, Farm parentFarm) {
            this.farmPosition = farmPosition;
            this.visitingPositions = visitingPositions;
            this.parentFarm = parentFarm;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Farm) obj;
            return Objects.equals(this.farmPosition, that.farmPosition);
        }

        @Override
        public int hashCode() {
            return Objects.hash(farmPosition);
        }

        @Override
        public String toString() {
            return "Farm[" +
                    "farmPosition=" + farmPosition + ", " +
                    "visitingPositions=" + visitingPositions + ']';
        }

    }

    public int getSolution(List<String> lines) {
        List<Position> gardenPositions = getGardenPositions(lines);
        Position startingPosition = gardenPositions.getLast();

        return countPossiblePositionsAfterSteps(new HashSet<>(gardenPositions), startingPosition, 500);
    }

    public long getSolution2(List<String> lines) {
        List<Position> gardenPositions = getGardenPositions(lines);
        Position startingPosition = gardenPositions.getLast();

        ROW_COUNT = lines.size();
        COLUMN_COUNT = lines.getFirst().length();

//        int result2 = countPossiblePositionsAfterSteps2(new HashSet<>(gardenPositions), startingPosition, 500);
//        System.out.println("Result of version 2: " + result2);
        long result3 = countPossiblePositionsAfterSteps3(new HashSet<>(gardenPositions), startingPosition, 1000);
        System.out.println("Result of version 3: " + result3);
        return result3;
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

            int increaseInStep = newVisitingPositions.size() - visitingPositions.size();
            visitingPositions = newVisitingPositions;
            steps++;

            int possiblePositionCount = visitingPositions.size();
//            System.out.println("Possible positions after " + steps + " steps:" + possiblePositionCount + " Increase in step: " + increaseInStep);
        }

//        Set<Position> outsideFarm = visitingPositions.stream()
//                .filter(this::isOutsideFarm)
//                .collect(Collectors.toSet());
//
//        visitingPositions.removeAll(outsideFarm);

//        System.out.println("Visiting positions of version 2 (outside): " + outsideFarm);
//        System.out.println("Visiting positions of version 2 (inside): " + visitingPositions);
//        return visitingPositions.size() + outsideFarm.size();
        return visitingPositions.size();
    }

    private long countPossiblePositionsAfterSteps3(Set<Position> gardenPositions, Position startingPosition, int stepsToTake) {
        Map<Position, Farm> farms = new HashMap<>();
        Map<Integer, Long> stepResults = new HashMap<>();

        Position startingFarmPosition = new Position(0, 0);
        Set<Position> firstFarmVisitingPositions = new HashSet<>();
        firstFarmVisitingPositions.add(startingPosition);

        Farm startingFarm = new Farm(startingFarmPosition, firstFarmVisitingPositions, null);
        farms.put(startingFarmPosition, startingFarm);

//        Set<Integer> possiblePositionCounts = new HashSet<>();
//        boolean cycleStarted = false;
//        Map<Boolean, Integer> results = new HashMap<>();

        Map<Farm, Map<Boolean, Integer>> farmResults = new HashMap<>();

        Map<Position, List<Position>> adjacentPositionsOfPosition = new HashMap<>();

        int steps = 0;
        while (steps < stepsToTake) {
            List<Farm> newFarms = new ArrayList<>();
            for (Farm currentFarm : farms.values()) {
                if (farmResults.containsKey(currentFarm)) {
                    continue;
                }

                Set<Position> visitingPositions = currentFarm.visitingPositions;

                Set<Position> newVisitingPositions = new HashSet<>();
                for (Position visitingPosition : visitingPositions) {
                    List<Position> adjacentGardenPositions = adjacentPositionsOfPosition.get(visitingPosition);
                    if (adjacentGardenPositions == null) {
                        adjacentGardenPositions = getAdjacentGardenPositions2(gardenPositions, visitingPosition);
                        adjacentPositionsOfPosition.put(visitingPosition, adjacentGardenPositions);
                    }
                    newVisitingPositions.addAll(adjacentGardenPositions);
                }
                Set<Position> positionsOutsideFarm = newVisitingPositions.stream()
                        .filter(this::isOutsideFarm)
                        .collect(Collectors.toSet());

                for (Position positionOutside : positionsOutsideFarm) {
                    Position farmPosition = getFarmPosition(currentFarm, positionOutside);
                    if (!farms.containsKey(farmPosition)) {
                        Set<Position> newFarmVisitingPositions = new HashSet<>();
                        newFarmVisitingPositions.add(getProjectedPosition(positionOutside));
                        Farm newFarm = new Farm(farmPosition, newFarmVisitingPositions, currentFarm);
                        newFarms.add(newFarm);

//                        System.out.println("New farm created: " + newFarm);
                    } /*else {
                        Farm farm = farms.get(farmPosition);
                        if (!farm.equals(currentFarm.parentFarm)) {
                            farm.visitingPositions.add(getProjectedPosition(positionOutside));
                        }
                    }*/
                }

                newVisitingPositions.removeAll(positionsOutsideFarm);

//                int increaseInStep = newVisitingPositions.size() - visitingPositions.size();
                currentFarm.visitingPositions = newVisitingPositions;

                int possiblePositionCount = currentFarm.visitingPositions.size();
//                System.out.println("Farm: " + currentFarm.farmPosition + ", possible visitingPositions after " + (steps + 1) + " steps:"
//                        + possiblePositionCount + " Increase in step: " + increaseInStep);

                // cycle detection
                if (currentFarm.possiblePositionCounts.contains(possiblePositionCount)) {
                    // cycle starts
                    boolean isEven = (steps + 1) % 2 == 0;
                    currentFarm.results.put(isEven, possiblePositionCount);
                    if (currentFarm.cycleStarted) {
                        // have both results in the map now
//                        System.out.println("Cycle end found, results for isEven: " + currentFarm.results);
//                        return results.get(isEven);
                        farmResults.put(currentFarm, currentFarm.results);
                    } else {
//                        System.out.println("Possible cycle start found at step: " + steps);
                        currentFarm.cycleStarted = true;
                    }
                } else {
                    if (currentFarm.cycleStarted) {
                        currentFarm.cycleStarted = false;
                    }
                }
                currentFarm.possiblePositionCounts.add(possiblePositionCount);
            }
            for (Farm newFarm : newFarms) {
                farms.put(newFarm.farmPosition, newFarm);
            }
//            long currentCount = 0;
//            for (Farm farm : farms.values()) {
//                currentCount += farm.visitingPositions.size();
//            }
            steps++;
//            long resultOfCurrentStep = getResultOfAllFarms(steps, farms, farmResults);
//            stepResults.put(steps, resultOfCurrentStep);
//            long resultOfPreviousStep = stepResults.getOrDefault(steps - 1, 0L);
//            System.out.println("Total possible visitingPositions count after " + steps + " steps:" +
//                    resultOfCurrentStep + ", increase from previous: " +
//                    (resultOfCurrentStep - resultOfPreviousStep));
            if (steps % 100 == 0) {
                long resultOfCurrentStep = getResultOfAllFarms(steps, farms, farmResults);
                System.out.println("Total possible visitingPositions count after " + steps + " steps:" +
                    resultOfCurrentStep);
            }
        }

//        Set<Position> visitingPositionsToPrint = new HashSet<>();
//        for (Farm farm : farms.values()) {
//            int rowDelta = farm.farmPosition.row * ROW_COUNT;
//            int columnDelta = farm.farmPosition.column * COLUMN_COUNT;
//            Set<Position> visitingPositionsOfFarm = new HashSet<>();
//            for (Position visitingPosition : farm.visitingPositions) {
//                Position positionWithDelta = new Position(visitingPosition.row + rowDelta, visitingPosition.column + columnDelta);
//                visitingPositionsToPrint.add(positionWithDelta);
//                visitingPositionsOfFarm.add(positionWithDelta);
//            }
//            System.out.println("Visiting positions of farm " + farm.farmPosition + " : " + visitingPositionsOfFarm);
//        }
//        System.out.println("Visiting positions of version 3:" + visitingPositionsToPrint);

        long result = getResultOfAllFarms(stepsToTake, farms, farmResults);
        return result;
    }

    private long getResultOfAllFarms(int steps, Map<Position, Farm> farms, Map<Farm, Map<Boolean, Integer>> farmResults) {
        long result = 0;
        boolean isEven = steps % 2 == 0;
        for (Farm farm : farms.values()) {
            if (farmResults.containsKey(farm)) {
                Map<Boolean, Integer> results = farmResults.get(farm);
                Integer farmResult = results.get(isEven);
                result += farmResult;
            } else {
                result += farm.visitingPositions.size();
            }
        }
        return result;
    }

    private boolean isOutsideFarm(Position position) {
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

    private Position getFarmPosition(Farm currentFarm, Position positionOutsideFarm) {
        Position newFarmPosition;
        if (positionOutsideFarm.row < 0) {
            newFarmPosition = new Position(currentFarm.farmPosition.row - 1, currentFarm.farmPosition.column);
        } else if (positionOutsideFarm.row >= ROW_COUNT) {
            newFarmPosition = new Position(currentFarm.farmPosition.row + 1, currentFarm.farmPosition.column);
        } else if (positionOutsideFarm.column < 0) {
            newFarmPosition = new Position(currentFarm.farmPosition.row, currentFarm.farmPosition.column - 1);
        } else if (positionOutsideFarm.column > 0) {
            newFarmPosition = new Position(currentFarm.farmPosition.row, currentFarm.farmPosition.column + 1);
        } else {
            System.out.println("Invalid positionOutsideFarm: " + positionOutsideFarm);
            newFarmPosition = null;
        }

        return newFarmPosition;
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
