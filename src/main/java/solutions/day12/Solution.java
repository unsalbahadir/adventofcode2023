package solutions.day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    public long getSolution(List<String> lines) {
        long possibleArrangementCount = 0;

        for (String line : lines) {
            String[] split = line.split(" ");
            String springs = split[0];
            List<Integer> damagedSpringGroups = Arrays.stream(split[1].split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
//            List<String> possibleArrangements = getPossibleArrangements(springs, damagedSpringGroups);
//            System.out.println("Possible arrangements size: " + possibleArrangements.size());
//            possibleArrangementCount += possibleArrangements.size();
            long count = countArrangements(new HashMap<>(), springs, damagedSpringGroups, 0);
//            System.out.println("Count for " + line + ": " + count);
            possibleArrangementCount += count;
        }

        return possibleArrangementCount;
    }

    public long getSolution2(List<String> lines) {
        long possibleArrangementCount = 0;

        for (String line : lines) {
            line = unfold(line);
            String[] split = line.split(" ");
            String springs = split[0];
            List<Integer> damagedSpringGroups = Arrays.stream(split[1].split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
//            List<String> possibleArrangements = getPossibleArrangements(springs, damagedSpringGroups);
//            System.out.println("Possible arrangements size: " + possibleArrangements.size());
//            possibleArrangementCount += possibleArrangements.size();
            long count = countArrangements(new HashMap<>(), springs, damagedSpringGroups, 0);
            System.out.println("Count for " + line + ": " + count);
            possibleArrangementCount += count;
        }

        return possibleArrangementCount;
    }

    private String unfold(String line) {
        StringBuilder result = new StringBuilder();
        String[] split = line.split(" ");

        String springs = IntStream.range(0, 5)
                .mapToObj(i -> split[0])
                .collect(Collectors.joining("?"));

        result.append(springs);
        result.append(" ");

        String damagedSpringGroups = IntStream.range(0, 5)
                .mapToObj(i -> split[1])
                .collect(Collectors.joining(","));
        result.append(damagedSpringGroups);

//        System.out.println("Unfolded: " + result);
        return result.toString();
    }

    private List<String> getPossibleArrangements(String springs, List<Integer> damagedSpringGroups) {
        List<String> possibleArrangements = new ArrayList<>();
        tryPossibleArrangements(springs, damagedSpringGroups, possibleArrangements);

        return possibleArrangements;
    }

    private long countArrangements(Map<String, Long> results, String springs, List<Integer> damagedSpringGroups, int currentGroupLength) {
        String key = getKey(springs, damagedSpringGroups, currentGroupLength);
        if (results.containsKey(key)) {
            return results.get(key);
        }
        if (springs.isEmpty()) {
            if (damagedSpringGroups.isEmpty() || currentGroupLength == damagedSpringGroups.getFirst()) {
                return 1;
            } else {
                return 0;
            }
        }
        if (damagedSpringGroups.isEmpty()) {
            if (springs.contains("#")) {
                return 0;
            } else {
                return 1;
            }
        }

        Integer remainingTotalSprings = damagedSpringGroups.stream().reduce(Integer::sum).orElse(0);
        if (remainingTotalSprings > (springs.length() + currentGroupLength)) {
            return 0;
        }

        char firstChar = springs.charAt(0);
        long result;
        String remainingSprings = springs.substring(1);
        if (firstChar == '.') {
            if (currentGroupLength > 0) {
                if (currentGroupLength == damagedSpringGroups.getFirst()) {
                    result = countArrangements(results, remainingSprings, damagedSpringGroups.subList(1, damagedSpringGroups.size()), 0);
                } else {
                    result = 0;
                }
            } else {
                result = countArrangements(results, remainingSprings, damagedSpringGroups, 0);
            }
        } else if (firstChar == '#') {
            result = countArrangements(results, remainingSprings, damagedSpringGroups, currentGroupLength + 1);
        } else {
            // set '.'
            long withDot;
            if (currentGroupLength > 0) {
                if (currentGroupLength == damagedSpringGroups.getFirst()) {
                    withDot = countArrangements(results, remainingSprings, damagedSpringGroups.subList(1, damagedSpringGroups.size()), 0);
                } else {
                    withDot = 0;
                }
            } else {
                withDot = countArrangements(results, remainingSprings, damagedSpringGroups, 0);
            }
            // set '#'
            long withPound = countArrangements(results, remainingSprings, damagedSpringGroups, currentGroupLength + 1);
            result = withDot + withPound;
        }
        results.put(key, result);
        return result;
    }

    private String getKey(String springs, List<Integer> damagedSpringGroups, int currentGroupLength) {
        return String.format("%s-%s-%s", springs, damagedSpringGroups, currentGroupLength);
    }

    private void tryPossibleArrangements(String springs, List<Integer> damagedSpringGroups, List<String> results) {
        int unknownCharIndex = springs.indexOf("?");
        if (unknownCharIndex == -1) {
            if (isValid(springs, damagedSpringGroups)) {
                results.add(springs);
            }
            return;
        }

        String withEmpty = replaceCharacter(springs, unknownCharIndex, '.');
        tryPossibleArrangements(withEmpty, damagedSpringGroups, results);

        String withFilled = replaceCharacter(springs, unknownCharIndex, '#');
        tryPossibleArrangements(withFilled, damagedSpringGroups, results);
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
        System.out.println(solution.getSolution2(lines));
    }
}
