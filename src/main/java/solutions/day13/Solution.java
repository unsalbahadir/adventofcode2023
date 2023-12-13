package solutions.day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Solution {

    public long getSolution(List<String> lines) {
        long result = 0;

        List<String> currentPattern = new ArrayList<>();

        for (String line : lines) {
            if (line.isEmpty()) {
                long patternResult = getPatternResult(currentPattern);
                System.out.println("Pattern result: " + patternResult);
                result += patternResult;
                currentPattern.clear();
            } else {
                currentPattern.add(line);
            }
        }
        // last pattern
        long patternResult = getPatternResult(currentPattern);
        System.out.println("Pattern result: " + patternResult);
        result += patternResult;

        return result;
    }

    private long getPatternResult(List<String> pattern) {
        long verticalReflectionColumnCount = isVerticalReflection(pattern);
        if (verticalReflectionColumnCount != -1) {
            return verticalReflectionColumnCount;
        }

        long horizontalReflectionRowCount = isHorizontalReflection(pattern);
        if (horizontalReflectionRowCount == -1) {
            System.out.println("No reflection in pattern: " + pattern);
            return -1;
        }
        return horizontalReflectionRowCount * 100;
    }

    private long isVerticalReflection(List<String> pattern) {
        List<String> columns = getColumns(pattern);
        return isHorizontalReflection(columns);
    }

    private List<String> getColumns(List<String> pattern) {
        int columnLength = pattern.getFirst().length();
        List<String> columns = new ArrayList<>(columnLength);
        for (int columnIndex = 0; columnIndex < columnLength; columnIndex++) {
            StringBuilder column = new StringBuilder();
            for (String s : pattern) {
                column.append(s.charAt(columnIndex));
            }
            columns.add(column.toString());
        }
        return columns;
    }

    private long isHorizontalReflection(List<String> pattern) {
        List<Integer> startingPoint = getStartingPoints(pattern);

        for (Integer i : startingPoint) {
            if (isStartingPointValid(pattern, i)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isStartingPointValid(List<String> pattern, int startingPoint) {
        int left = startingPoint - 1;
        int right = startingPoint;

        while (left >= 0 && right < pattern.size()) {
            if (!pattern.get(left).equals(pattern.get(right))) {
                return false;
            }
            left--;
            right++;
        }

        return true;
    }

    private List<Integer> getStartingPoints(List<String> pattern) {
        List<Integer> startingPoints = new ArrayList<>();
        for (int i = 1; i < pattern.size(); i++) {
            if (pattern.get(i).equals(pattern.get(i - 1))) {
                startingPoints.add(i);
            }
        }
        return startingPoints;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day13.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
