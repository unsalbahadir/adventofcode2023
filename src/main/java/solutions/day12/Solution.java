package solutions.day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    public long getSolution(List<String> lines) {
        long result = 0;

        for (String line : lines) {
            long possibleArrangementCount = getPossibleArrangementCount(line);
            result += possibleArrangementCount;
        }
        return result;
    }

    public long getSolution2(List<String> lines) {
        long result = 0;

        for (String line : lines) {
            line = unfold(line);
            long possibleArrangementCount = getPossibleArrangementCount(line);
            result += possibleArrangementCount;
        }
        return result;
    }

    private long getPossibleArrangementCount(String line) {
        String[] split = line.split(" ");
        String springs = split[0];
        List<Integer> damagedSpringGroups = Arrays.stream(split[1].split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        long count = countArrangements(new HashMap<>(), springs, damagedSpringGroups, 0);
        System.out.println("Count for " + line + ": " + count);
        return count;
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
            result = getResultWithDot(results, remainingSprings, damagedSpringGroups, currentGroupLength);
        } else if (firstChar == '#') {
            result = getResultWithPound(results, remainingSprings, damagedSpringGroups, currentGroupLength);
        } else {
            // set '.'
            long withDot = getResultWithDot(results, remainingSprings, damagedSpringGroups, currentGroupLength);
            // set '#'
            long withPound = getResultWithPound(results, remainingSprings, damagedSpringGroups, currentGroupLength);
            result = withDot + withPound;
        }
        results.put(key, result);
        return result;
    }

    private long getResultWithDot(Map<String, Long> results, String remainingSprings, List<Integer> damagedSpringGroups, int currentGroupLength) {
        long result;
        if (currentGroupLength > 0) {
            if (currentGroupLength == damagedSpringGroups.getFirst()) {
                result = countArrangements(results, remainingSprings, damagedSpringGroups.subList(1, damagedSpringGroups.size()), 0);
            } else {
                result = 0;
            }
        } else {
            result = countArrangements(results, remainingSprings, damagedSpringGroups, 0);
        }
        return result;
    }

    private long getResultWithPound(Map<String, Long> results, String remainingSprings, List<Integer> damagedSpringGroups, int currentGroupLength) {
        return countArrangements(results, remainingSprings, damagedSpringGroups, currentGroupLength + 1);
    }

    private String getKey(String springs, List<Integer> damagedSpringGroups, int currentGroupLength) {
        return String.format("%s-%s-%s", springs, damagedSpringGroups, currentGroupLength);
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day12.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
