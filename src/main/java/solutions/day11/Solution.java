package solutions.day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    record Position(long row, long column) {}

    public long getSolution(List<String> lines) {
        List<List<Character>> grid = convertToGrid(lines);
        expand(grid);
//        printGrid(grid);
        List<Position> galaxyPositions = findGalaxies(grid);
//        for (Position galaxyPosition : galaxyPositions) {
//            System.out.println(galaxyPosition);
//        }
        return getSumOfDistancesBetweenGalaxies(galaxyPositions);
    }

    public long getSolution2(List<String> lines) {
        List<List<Character>> grid = convertToGrid(lines);
        List<Position> galaxyPositions = findGalaxies(grid);
//        System.out.println("Before expansion:");
//        for (Position galaxyPosition : galaxyPositions) {
//            System.out.println(galaxyPosition);
//        }
        expand2(grid, galaxyPositions, 1000000);
//        System.out.println("After expansion:");
//        for (Position galaxyPosition : galaxyPositions) {
//            System.out.println(galaxyPosition);
//        }
        return getSumOfDistancesBetweenGalaxies(galaxyPositions);
    }

    private List<List<Character>> convertToGrid(List<String> lines) {
        List<List<Character>> grid = new ArrayList<>();

        for (String line : lines) {
            List<Character> row = new ArrayList<>();
            grid.add(row);
            for (int j = 0; j < line.length(); j++) {
                row.add(line.charAt(j));
            }
        }
        return grid;
    }

    private void expand(List<List<Character>> grid) {
        List<Integer> rowsWithoutGalaxies = new ArrayList<>();
        for (int i = grid.size() - 1; i >= 0; i--) {
            List<Character> row = grid.get(i);
            if (!containsGalaxy(row)) {
                rowsWithoutGalaxies.add(i);
            }
        }

        List<Integer> columnsWithoutGalaxies = new ArrayList<>();
        for (int i = grid.size() - 1; i >= 0; i--) {
            List<Character> column = getColumn(grid, i);
            if (!containsGalaxy(column)) {
                columnsWithoutGalaxies.add(i);
            }
        }

        for (Integer rowIndex : rowsWithoutGalaxies) {
            List<Character> row = grid.get(rowIndex);
            List<Character> newRow = new ArrayList<>(row);
            grid.add(rowIndex, newRow);
        }

        for (Integer columnIndex : columnsWithoutGalaxies) {
            for (List<Character> characters : grid) {
                Character c = characters.get(columnIndex);
                characters.add(columnIndex, c);
            }
        }
    }

    private void expand2(List<List<Character>> grid, List<Position> galaxyPositions, int expandFactor) {
        List<Integer> rowsWithoutGalaxies = new ArrayList<>();
        for (int i = grid.size() - 1; i >= 0; i--) {
            List<Character> row = grid.get(i);
            if (!containsGalaxy(row)) {
                rowsWithoutGalaxies.add(i);
            }
        }

        List<Integer> columnsWithoutGalaxies = new ArrayList<>();
        for (int i = grid.size() - 1; i >= 0; i--) {
            List<Character> column = getColumn(grid, i);
            if (!containsGalaxy(column)) {
                columnsWithoutGalaxies.add(i);
            }
        }

        int linesToAdd = expandFactor - 1;

        for (Integer rowIndex : rowsWithoutGalaxies) {
            for (int i = 0; i < galaxyPositions.size(); i++) {
                Position position = galaxyPositions.get(i);
                if (position.row >= rowIndex) {
                    Position newPosition = new Position(position.row + linesToAdd, position.column);
                    galaxyPositions.set(i, newPosition);
                }
            }
        }

        for (Integer columnIndex : columnsWithoutGalaxies) {
            for (int i = 0; i < galaxyPositions.size(); i++) {
                Position position = galaxyPositions.get(i);
                if (position.column >= columnIndex) {
                    Position newPosition = new Position(position.row, position.column + linesToAdd);
                    galaxyPositions.set(i, newPosition);
                }
            }
        }
    }

    private List<Character> getColumn(List<List<Character>> grid, int columnIndex) {
        List<Character> column = new ArrayList<>();
        for (List<Character> row : grid) {
            column.add(row.get(columnIndex));
        }
        return column;
    }

    private boolean containsGalaxy(List<Character> arr) {
        return arr.contains('#');
    }

    private List<Position> findGalaxies(List<List<Character>> grid) {
        List<Position> galaxyPositions = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < grid.size(); rowIndex++) {
            List<Character> row = grid.get(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                if (row.get(columnIndex) == '#') {
                    galaxyPositions.add(new Position(rowIndex, columnIndex));
                }
            }
        }
        return galaxyPositions;
    }

    private long getSumOfDistancesBetweenGalaxies(List<Position> galaxyPositions) {
        long sum = 0;
        for (int i = 0; i < galaxyPositions.size(); i++) {
            for (int j = i + 1; j < galaxyPositions.size(); j++) {
                Position position1 = galaxyPositions.get(i);
                Position position2 = galaxyPositions.get(j);
                long distanceBetweenGalaxies = getDistanceBetweenGalaxies(position1, position2);
//                System.out.println("Distance between " + (i + 1) + " (" + position1 + ")" + " and "
//                        + (j + 1) + " (" + position2 + ")" + ": " + distanceBetweenGalaxies);
                sum += distanceBetweenGalaxies;
            }
        }
        return sum;
    }

    private long getDistanceBetweenGalaxies(Position position1, Position position2) {
        return Math.abs(position1.row - position2.row) + Math.abs(position1.column - position2.column);
    }

    private void printGrid(List<List<Character>> grid) {
        for (List<Character> characters : grid) {
            String row = characters.stream().map(Object::toString).collect(Collectors.joining());
            System.out.println(row);
        }
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day11.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
