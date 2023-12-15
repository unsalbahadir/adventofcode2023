package solutions.day15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Solution {

    public long getSolution(List<String> lines) {
        long result = 0;

        for (String line : lines) {
            String[] commands = line.split(",");
            for (String command : commands) {
                long hashResult = getHashResult(command);
                System.out.println("Result of command " + command + " : " + hashResult);
                result += hashResult;
            }
        }

        return result;
    }

    private long getHashResult(String s) {
        long result = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            result += (int) c;
            result *= 17;
            result %= 256;
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day15.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
