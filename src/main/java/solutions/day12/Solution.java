package solutions.day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public int getSolution(List<String> lines) {
        int possibleArrangementCount = 0;

        for (String line : lines) {
            String[] split = line.split(" ");
            String springs = split[0];
            List<Integer> damagedSpringGroups = Arrays.stream(split[1].split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            List<String> possibleArrangements = getPossibleArrangements(springs, damagedSpringGroups);
            System.out.println("Possible arrangements size: " + possibleArrangements.size());
            possibleArrangementCount += possibleArrangements.size();
        }

        return possibleArrangementCount;
    }

    private List<String> getPossibleArrangements(String springs, List<Integer> damagedSpringGroups) {
        List<String> possibleArrangements = new ArrayList<>();
        tryPossibleArrangements(springs, damagedSpringGroups, possibleArrangements);

        return possibleArrangements;
    }

    private List<String> tryPossibleArrangements(String springs, List<Integer> damagedSpringGroups, List<String> results) {
        int unknownCharIndex = springs.indexOf("?");
        if (unknownCharIndex == -1) {
            if (isValid(springs, damagedSpringGroups)) {
                results.add(springs);
            }
            return results;
        }

        String withEmpty = replaceCharacter(springs, unknownCharIndex, '.');
        tryPossibleArrangements(withEmpty, damagedSpringGroups, results);

        String withFilled = replaceCharacter(springs, unknownCharIndex, '#');
        tryPossibleArrangements(withFilled, damagedSpringGroups, results);

        return results;
    }

    private String replaceCharacter(String s, int index, char newChar) {
        StringBuilder stringBuilder = new StringBuilder(s);
        stringBuilder.setCharAt(index, newChar);
        return stringBuilder.toString();
    }

    private boolean isValid(String springs, List<Integer> damagedSpringGroups) {
        List<String> groups = getGroups(springs);

        if (groups.size() != damagedSpringGroups.size()) {
            return false;
        }

        for (int i = 0; i < groups.size(); i++) {
            String group = groups.get(i);
            Integer damagedSpringGroupLength = damagedSpringGroups.get(i);

            if (group.length() != damagedSpringGroupLength) {
                return false;
            }
        }
        return true;
    }

    private List<String> getGroups(String springs) {
        List<String> groups = new ArrayList<>();
        StringBuilder currentGroup = null;
        for (int i = 0; i < springs.length(); i++) {
            char c = springs.charAt(i);
            if (c == '.') {
                if (currentGroup != null) {
                    groups.add(currentGroup.toString());
                    currentGroup = null;
                }
            } else {
                if (currentGroup == null) {
                    currentGroup = new StringBuilder();
                }
                currentGroup.append(c);
            }
        }
        if (currentGroup != null) {
            groups.add(currentGroup.toString());
        }
        return groups;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day12.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
