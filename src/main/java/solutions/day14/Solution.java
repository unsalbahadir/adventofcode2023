package solutions.day14;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Solution {

    public long getSolution(List<String> lines) {
        List<List<Character>> grid = convertToGrid(lines);
        tiltNorth(grid);
        return calculateLoad(grid);
    }

    public long getSolution2(List<String> lines) {
        List<List<Character>> grid = convertToGrid(lines);
        int cycleCount = 1000000000;
        Set<List<List<Character>>> gridCache = new HashSet<>();
        boolean cacheHitOnce = false;
        int indexInLoop = -1;
        for (int i = 0; i < cycleCount; i++) {
            tiltNorth(grid);
            tiltWest(grid);
            tiltSouth(grid);
            tiltEast(grid);
            if (gridCache.contains(grid)) {
                System.out.println("Grid cache hit after: " + i);
                if (cacheHitOnce) {
                    int loopInterval = gridCache.size();
                    indexInLoop = (cycleCount - i) % loopInterval;
                    System.out.println("Loop interval: " + loopInterval);
                    System.out.println("Found index in loop:" + indexInLoop);
                } else {
                    cacheHitOnce = true;
                }
                gridCache.clear();
                gridCache.add(grid);
            } else {
                gridCache.add(grid);
                if (indexInLoop != -1 && gridCache.size() == indexInLoop) {
                    System.out.println("Found correct grid in loop. Breaking out");
                    break;
                }
            }
        }
//        System.out.println("Grid after " + cycleCount + " cycles: ");
//        printGrid(grid);
        return calculateLoad(grid);
    }


    private List<List<Character>> convertToGrid(List<String> lines) {
        List<List<Character>> grid = new ArrayList<>();
        for (String line : lines) {
            List<Character> row = new ArrayList<>();
            grid.add(row);
            for (int i = 0; i < line.length(); i++) {
                row.add(line.charAt(i));
            }
        }
        return grid;
    }

    private void tiltNorth(List<List<Character>> grid) {
        for (int columnIndex = 0; columnIndex < grid.getFirst().size(); columnIndex++) {
            for (int rowIndex = 0; rowIndex < grid.size(); rowIndex++) {
                Character c = grid.get(rowIndex).get(columnIndex);
                if (c == 'O') {
                    rollNorth(grid, rowIndex, columnIndex);
                }
            }
        }
//        System.out.println("Grid after tilt north:");
//        printGrid(grid);
    }

    private void tiltWest(List<List<Character>> grid) {
        for (int rowIndex = 0; rowIndex < grid.size(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < grid.getFirst().size(); columnIndex++) {
                Character c = grid.get(rowIndex).get(columnIndex);
                if (c == 'O') {
                    rollWest(grid, rowIndex, columnIndex);
                }
            }
        }
//        System.out.println("Grid after tilt west:");
//        printGrid(grid);
    }

    private void tiltSouth(List<List<Character>> grid) {
        for (int columnIndex = 0; columnIndex < grid.getFirst().size(); columnIndex++) {
            for (int rowIndex = grid.size() - 1; rowIndex >= 0; rowIndex--) {
                Character c = grid.get(rowIndex).get(columnIndex);
                if (c == 'O') {
                    rollSouth(grid, rowIndex, columnIndex);
                }
            }
        }
//        System.out.println("Grid after tilt south:");
//        printGrid(grid);
    }

    private void tiltEast(List<List<Character>> grid) {
        for (int rowIndex = 0; rowIndex < grid.size(); rowIndex++) {
            for (int columnIndex = grid.getFirst().size() - 1; columnIndex >= 0; columnIndex--) {
                Character c = grid.get(rowIndex).get(columnIndex);
                if (c == 'O') {
                    rollEast(grid, rowIndex, columnIndex);
                }
            }
        }
//        System.out.println("Grid after tilt west:");
//        printGrid(grid);
    }

    private void rollNorth(List<List<Character>> grid, int rowIndex, int columnIndex) {
        while (rowIndex > 0 && grid.get(rowIndex - 1).get(columnIndex) == '.') {
            grid.get(rowIndex).set(columnIndex, '.');
            grid.get(rowIndex - 1).set(columnIndex, 'O');
            rowIndex--;
        }
    }

    private void rollWest(List<List<Character>> grid, int rowIndex, int columnIndex) {
        while (columnIndex > 0 && grid.get(rowIndex).get(columnIndex - 1) == '.') {
            grid.get(rowIndex).set(columnIndex, '.');
            grid.get(rowIndex).set(columnIndex - 1, 'O');
            columnIndex--;
        }
    }

    private void rollSouth(List<List<Character>> grid, int rowIndex, int columnIndex) {
        while (rowIndex < grid.size() - 1 && grid.get(rowIndex + 1).get(columnIndex) == '.') {
            grid.get(rowIndex).set(columnIndex, '.');
            grid.get(rowIndex + 1).set(columnIndex, 'O');
            rowIndex++;
        }
    }

    private void rollEast(List<List<Character>> grid, int rowIndex, int columnIndex) {
        while (columnIndex < grid.size() - 1 && grid.get(rowIndex).get(columnIndex + 1) == '.') {
            grid.get(rowIndex).set(columnIndex, '.');
            grid.get(rowIndex).set(columnIndex + 1, 'O');
            columnIndex++;
        }
    }

    private long calculateLoad(List<List<Character>> grid) {
        long totalLoad = 0;
        long loadForRow = grid.size();
        for (List<Character> characters : grid) {
            long numberOfRoundedRocks = 0;
            for (Character character : characters) {
                if (character == 'O') {
                    numberOfRoundedRocks++;
                }
            }
            long rowLoad = loadForRow * numberOfRoundedRocks;
            totalLoad += rowLoad;
            loadForRow--;
        }
        return totalLoad;
    }

    private void printGrid(List<List<Character>> grid) {
        for (List<Character> row : grid) {
            String rowCombined = row.stream().map(Object::toString).collect(Collectors.joining(""));
            System.out.println(rowCombined);
        }
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day14.txt"));
//        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
