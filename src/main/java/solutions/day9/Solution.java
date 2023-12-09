package solutions.day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {

    public int getSolution(List<String> lines) {
        int result = 0;
        for (String line : lines) {
            List<Integer> values = Arrays.stream(line.split(" ")).map(Integer::parseInt).toList();
            int nextValue = getNextValue(values);
            result += nextValue;
        }

        return result;
    }

    private int getNextValue(List<Integer> values) {
        if (values.stream().allMatch(integer -> integer == 0)) {
            return 0;
        }

        List<Integer> diffs = new ArrayList<>();
        for (int i = 1; i < values.size(); i++) {
            int diff = values.get(i) - values.get(i - 1);
            diffs.add(diff);
        }
        int nextValue = getNextValue(diffs);
        return values.getLast() + nextValue;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day9.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
