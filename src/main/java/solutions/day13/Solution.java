package solutions.day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Solution {

    boolean fixSmudge = false;

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
        if (fixSmudge) {
            return isHorizontalReflectionWithSmudge(pattern);
        } else {
            return isHorizontalReflectionWithoutSmudge(pattern);
        }
    }

    private long isHorizontalReflectionWithoutSmudge(List<String> pattern) {
        List<Integer> startingPointWithoutSmudge = getStartingPointsWithoutSmudge(pattern);

        for (Integer i : startingPointWithoutSmudge) {
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

    private long isHorizontalReflectionWithSmudge(List<String> pattern) {
        long existingHorizontalReflection = isHorizontalReflectionWithoutSmudge(pattern);
        List<Integer> startingPointWithoutSmudge = getStartingPointsWithoutSmudge(pattern).stream()
                .filter(integer -> integer != existingHorizontalReflection)
                .toList();
        for (Integer i : startingPointWithoutSmudge) {
            if (isStartingPointValidWithSmudge(pattern, i)) {
                return i;
            }
        }

        List<Integer> startingPointsWithSmudge = getStartingPointsWithSmudge(pattern);
        for (Integer i : startingPointsWithSmudge) {
            if (isStartingPointValidWithSmudge(pattern, i)) {
                return i;
            }
        }

        return -1;
    }

    private boolean isStartingPointValidWithSmudge(List<String> pattern, int startingPoint) {
        int left = startingPoint - 1;
        int right = startingPoint;

        boolean smudgeRemoved = false;
        while (left >= 0 && right < pattern.size()) {
            String line1 = pattern.get(left);
            String line2 = pattern.get(right);
            if (!line1.equals(line2)) {
                if (smudgeRemoved) { // already removed one smudge
                    return false;
                }
                int smudge = findSmudge(line1, line2);
                if (smudge == -1) {
                    return false;
                }
                smudgeRemoved = true;
            }
            left--;
            right++;
        }

        return true;
    }

    private List<Integer> getStartingPointsWithoutSmudge(List<String> pattern) {
        List<Integer> startingPoints = new ArrayList<>();
        for (int i = 1; i < pattern.size(); i++) {
            if (pattern.get(i).equals(pattern.get(i - 1))) {
                startingPoints.add(i);
            }
        }
        return startingPoints;
    }

    private List<Integer> getStartingPointsWithSmudge(List<String> pattern) {
        List<Integer> startingPoints = new ArrayList<>();
        for (int i = 1; i < pattern.size(); i++) {
            String line1 = pattern.get(i);
            String line2 = pattern.get(i - 1);
            int smudge = findSmudge(line1, line2);
            if (smudge != -1) {
                startingPoints.add(i);
            }
        }
        return startingPoints;
    }

    private String replaceChar(String s, int index, char newChar) {
        StringBuilder stringBuilder = new StringBuilder(s);
        stringBuilder.setCharAt(index, newChar);
        return stringBuilder.toString();
    }

    private int findSmudge(String line1, String line2) {
        int diffCharIndex = -1;
        for (int i = 0; i < line1.length(); i++) {
            if (line1.charAt(i) != line2.charAt(i)) {
                if (diffCharIndex != -1) {
                    return -1;
                } else {
                    diffCharIndex = i;
                }
            }
        }
        return diffCharIndex;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day13.txt"));
        System.out.println(solution.getSolution(lines));
        solution.fixSmudge = true;
        System.out.println(solution.getSolution(lines));
    }
}
