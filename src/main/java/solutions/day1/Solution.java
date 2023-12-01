package solutions.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Solution {

    public int getSolution(List<String> lines) {
        int sum = 0;

        for (String line : lines) {
            String firstDigit = getFirstDigit(line);
            String lastDigit = getLastDigit(line);

            String numAsString = firstDigit + lastDigit;
            int num = Integer.parseInt(numAsString);
            sum += num;
        }

        return sum;
    }

    private String getFirstDigit(String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isDigit(c)) {
                return String.valueOf(c);
            }
        }
        return "0";
    }

    private String getLastDigit(String line) {
        for (int i = line.length() - 1; i >= 0; i--) {
            char c = line.charAt(i);
            if (Character.isDigit(c)) {
                return String.valueOf(c);
            }
        }
        return "0";
    }


    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day1.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
