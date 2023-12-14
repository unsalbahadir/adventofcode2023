package solutions.day14;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public long getSolution(List<String> lines) {
        List<List<Character>> grid = convertToGrid(lines);
        tiltNorth(grid);
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
//            System.out.println("Grid after column " + columnIndex + " is rolled north");
            printGrid(grid);
        }
    }

    private void rollNorth(List<List<Character>> grid, int rowIndex, int columnIndex) {
        while (rowIndex > 0 && grid.get(rowIndex - 1).get(columnIndex) == '.') {
            grid.get(rowIndex).set(columnIndex, '.');
            grid.get(rowIndex - 1).set(columnIndex, 'O');
            rowIndex--;
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
        System.out.println(solution.getSolution(lines));
    }
}
