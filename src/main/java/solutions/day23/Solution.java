package solutions.day23;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {

    private static int MAX_ROW;
    private static int MAX_COLUMN;

    private record Position(int row, int column) {

    }

    public int getSolution(List<String> lines) {
        char[][] grid = convertToGrid(lines);

        Position startingPosition = getStartingPosition(grid);
        Position endPosition = getEndPosition(grid);

        MAX_ROW = grid.length;
        MAX_COLUMN = grid[0].length;

        HashSet<Position> visitedPositions = new HashSet<>();
        visitedPositions.add(startingPosition);
        return findLongestHikeToEnd(grid, startingPosition, endPosition, visitedPositions);
    }

    private char[][] convertToGrid(List<String> lines) {
        char[][] grid = new char[lines.size()][lines.getFirst().length()];

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                grid[i][j] = c;
            }
        }
        return grid;
    }

    private Position getStartingPosition(char[][] grid) {
        for (int column = 0; column < grid[0].length; column++) {
            if (grid[0][column] == '.') {
                return new Position(0, column);
            }
        }
        return null;
    }

    private Position getEndPosition(char[][] grid) {
        int lastRow = grid.length - 1;
        for (int column = 0; column < grid[lastRow].length; column++) {
            if (grid[lastRow][column] == '.') {
                return new Position(lastRow, column);
            }
        }
        return null;
    }

    private int findLongestHikeToEnd(char[][] grid, Position currentPosition, Position endPosition, Set<Position> visitedPositions) {
        if (currentPosition.equals(endPosition)) {
            return visitedPositions.size();
        }

        List<Position> nextPossiblePositions = findNextPossiblePositions(grid, currentPosition, visitedPositions);
        int longestHikeDistance = -1;
        visitedPositions.add(currentPosition);
        for (Position nextPossiblePosition : nextPossiblePositions) {
            int possibleLongestHike = findLongestHikeToEnd(grid, nextPossiblePosition, endPosition, visitedPositions);
            longestHikeDistance = Math.max(longestHikeDistance, possibleLongestHike);
        }
        visitedPositions.remove(currentPosition);

        return longestHikeDistance;
    }

    private List<Position> findNextPossiblePositions(char[][] grid, Position position, Set<Position> visitedPositions) {
        List<Position> nextPossiblePositions = new ArrayList<>();

        char currentPositionChar = grid[position.row][position.column];
        if (currentPositionChar == '>') {
            nextPossiblePositions.add(new Position(position.row, position.column + 1));
        } else if (currentPositionChar == 'v') {
            nextPossiblePositions.add(new Position(position.row + 1, position.column));
        } else if (currentPositionChar == '<') {
            nextPossiblePositions.add(new Position(position.row, position.column - 1));
        } else if (currentPositionChar == '^') {
            nextPossiblePositions.add(new Position(position.row - 1, position.column));
        } else {
            nextPossiblePositions.add(new Position(position.row - 1, position.column));
            nextPossiblePositions.add(new Position(position.row, position.column + 1));
            nextPossiblePositions.add(new Position(position.row + 1, position.column));
            nextPossiblePositions.add(new Position(position.row, position.column - 1));
        }

        return nextPossiblePositions.stream()
                .filter(pos -> isValid(grid, pos, visitedPositions))
                .toList();
    }

    private boolean isValid(char[][] grid, Position position, Set<Position> visitedPositions) {
        return position.row >= 0 && position.row <= MAX_ROW &&
                position.column >= 0 && position.column <= MAX_COLUMN &&
                grid[position.row][position.column] != '#' &&
                !visitedPositions.contains(position);
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day23.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
