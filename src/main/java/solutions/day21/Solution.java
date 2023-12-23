package solutions.day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
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

//        int result2 = countPossiblePositionsAfterSteps2(new HashSet<>(gardenPositions), startingPosition, 982);
//        System.out.println("Result of version 2: " + result2);
        long start = System.currentTimeMillis();
        long result3 = countPossiblePositionsAfterSteps3(new HashSet<>(gardenPositions), startingPosition, 982);
        long end = System.currentTimeMillis();
        long duration = Duration.ofMillis(end - start).toSeconds();
        System.out.println("Result of version 3: " + result3 + ", time taken: " + duration);
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

            visitingPositions = newVisitingPositions;
            steps++;
            if (steps % 131 == 65) {
                int possiblePositionCount = visitingPositions.size();
                System.out.println("Possible positions after " + steps + " steps:" + possiblePositionCount);
            }
        }
        return visitingPositions.size();
    }

    private long countPossiblePositionsAfterSteps3(Set<Position> gardenPositions, Position startingPosition, int stepsToTake) {
        Map<Position, Farm> farms = new HashMap<>();

        Position startingFarmPosition = new Position(0, 0);
        Set<Position> firstFarmVisitingPositions = new HashSet<>();
        firstFarmVisitingPositions.add(startingPosition);

        Farm startingFarm = new Farm(startingFarmPosition, firstFarmVisitingPositions, null);
        farms.put(startingFarmPosition, startingFarm);

        Map<Farm, Map<Boolean, Integer>> farmResults = new HashMap<>();

        Map<Position, List<Position>> adjacentPositionsOfPosition = new HashMap<>();

        int steps = 0;
        while (steps < stepsToTake) {
            Set<Farm> newFarms = new HashSet<>();
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
                    if (!farms.containsKey(farmPosition) && newFarms.stream().noneMatch(farm -> farm.farmPosition.equals(farmPosition))) {
                        Set<Position> newFarmVisitingPositions = new HashSet<>();
                        newFarmVisitingPositions.add(getProjectedPosition(positionOutside));
                        Farm newFarm = new Farm(farmPosition, newFarmVisitingPositions, currentFarm);
                        newFarms.add(newFarm);

//                        System.out.println("New farm created: " + newFarm);
                    }
                }
                newVisitingPositions.removeAll(positionsOutsideFarm);

                currentFarm.visitingPositions = newVisitingPositions;

                int possiblePositionCount = currentFarm.visitingPositions.size();
                // cycle detection
                if (currentFarm.possiblePositionCounts.contains(possiblePositionCount)) {
                    // cycle starts
                    boolean isEven = (steps + 1) % 2 == 0;
                    currentFarm.results.put(isEven, possiblePositionCount);
                    if (currentFarm.cycleStarted) {
                        // have both results in the map now
                        farmResults.put(currentFarm, currentFarm.results);
                    } else {
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
            steps++;

            if (steps % 131 == 65) {
                int n = steps / 131;
                String stepsString = String.format("%s (n=%s)", steps, n);
//                long resultOfCurrentStep = getResultOfAllFarms(steps, farms, farmResults);
                long resultOfCurrentStepWithFormula = getResultOfAllFarmsWithFormula(farms, farmResults, steps);
//                System.out.println("Possible positions after " + stepsString + " steps: " + resultOfCurrentStep +
//                        ", with formula: " +
//                System.out.println("Possible positions after " + stepsString + " steps: " + resultOfCurrentStep +
//                        ", active farm count: " + getActiveFarmCount(steps) +
//                        ", finished farm count: " + getFinishedFarmCount(steps));

//                System.out.println("Active farm count after " + stepsString + " steps: " + (farms.size() - farmResults.size()) +
//                        ", Active farm formula result: " + getActiveFarmCount(steps));
//                System.out.println("Finished farm count after " + stepsString + " steps: " + farmResults.size() +
//                        ", Finished farm formula result: " + getFinishedFarmCount(steps));
            }
        }

        return getResultOfAllFarms(stepsToTake, farms, farmResults);
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

    private int getActiveFarmCount(int steps) {
        return (steps / 131) * 8;
    }

    private int getFinishedFarmCount(int steps) {
        int n = steps / 131;
        return (int) (2 * Math.pow(n, 2) - (2 * n) + 1);
    }

    private long getResultOfAllFarmsWithFormula(Map<Position, Farm> farms, Map<Farm, Map<Boolean, Integer>> farmResults, int steps) {
        List<Farm> activeFarms = new ArrayList<>();
        List<Farm> finishedFarms = new ArrayList<>();

        for (Farm farm : farms.values()) {
            if (farmResults.containsKey(farm)) {
                finishedFarms.add(farm);
            } else {
                activeFarms.add(farm);
            }
        }

        int n = steps / 131;
        String stepsString = String.format("%s (n=%s)", steps, n);

        long resultOfActiveFarms = getResultOfActiveFarms(activeFarms);
//        System.out.println("Steps: " + stepsString + ", Result of active farms: " + resultOfActiveFarms);

        long countOf7201 = 0;
        long countOf7218 = 0;
        boolean isEven = steps % 2 == 0;
        long resultOfFinishedFarms = 0;
        for (Farm finishedFarm : finishedFarms) {
            Integer result = farmResults.get(finishedFarm).get(isEven);
            if (result == 7201) {
                countOf7201++;
            } else if (result == 7218) {
                countOf7218++;
            } else {
                System.out.println("Result other than 7201 and 7218: " + result);
            }
            resultOfFinishedFarms += result;
        }

//        System.out.println("Steps: " + stepsString + ", Result of finished farms: " + resultOfFinishedFarms +
//                ", with formula: " + getResultOfFinishedFarmsAtStep(steps) +
//                ", farm count with 7201: " + countOf7201 + ", with formula: " + getCountOfFarmsWith7201(steps) +
//                ", farm count with 7218: " + countOf7218 + ", with formula: " + getCountOfFarmsWith7218(steps));

        long totalResult = resultOfActiveFarms + resultOfFinishedFarms;
        long totalResultWithFormula = resultOfActiveFarms + getResultOfFinishedFarmsAtStep(steps);
        System.out.println("Steps: " + stepsString + ", Total result: " + totalResult +
                ", with formula: " + totalResultWithFormula);

        return totalResult;
    }

    private long getResultOfActiveFarms(List<Farm> farms) {
        return farms.stream()
                .map(farm -> (long) farm.visitingPositions.size())
                .reduce(Long::sum)
                .orElse(0L);
    }

    private long getResultOfFinishedFarms(List<Farm> farms, Map<Farm, Integer> farmResults) {
        return farms.stream()
                .map(farm -> (long) farmResults.get(farm))
                .reduce(Long::sum)
                .orElse(0L);
    }

    // formula for 7201 = 4n^2. n = steps / 2
    private long getResultOfFinishedFarmsAtStep(int steps) {
        int farmCountWith7201 = getCountOfFarmsWith7201(steps);
        long resultOfFarmsWith7201 = 7201L * farmCountWith7201;

        int farmCountWith7218 = getCountOfFarmsWith7218(steps);
        long resultOfFarmsWith7218 = 7218L * farmCountWith7218;

        return resultOfFarmsWith7201 + resultOfFarmsWith7218;
    }

    // formula for 7201 = 4n^2. n = steps / 2
    // formula for 7201 = (n-1)^2
    private int getCountOfFarmsWith7201(int steps) {
        int n = steps / 131;

//        int farmCountWith7201 = (int) (4 * Math.pow(nFor7201, 2));
        return (int) Math.pow((n-1), 2);
    }

    // formula for 7218 = 4n^2 - 4n + 1. n = steps / 2 rounded up
    private int getCountOfFarmsWith7218(int steps) {
        int n = steps / 131;

//        int nFor7218 = Math.ceilDiv(n, 2);
//        int farmCountWith7218 = (int) (4 * Math.pow(nFor7218, 2) - (4 * nFor7218) + 1);

        return (int) Math.pow(n, 2);
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

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day21.txt"));
//        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
