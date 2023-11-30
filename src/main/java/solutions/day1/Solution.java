package solutions.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Solution {

    public int getSolution(List<String> lines) {

        return 0;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day1.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
