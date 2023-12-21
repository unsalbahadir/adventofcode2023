package solutions.day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {

    private record Position(int row, int column) {}

    public int getSolution(List<String> lines) {
        List<Position> gardenPositions = getGardenPositions(lines);
        Position startingPosition = gardenPositions.getLast();

        return countPossiblePositionsAfterSteps(new HashSet<>(gardenPositions), startingPosition, 64);
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

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day21.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
