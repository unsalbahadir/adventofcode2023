package solutions.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Solution {

    private static final Map<String, String> digitsAsString = Map.of(
            "one", "1",
            "two", "2",
            "three","3",
            "four","4",
            "five","5",
            "six","6",
            "seven","7",
            "eight","8",
            "nine","9"
    );

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

    public int getSolution2(List<String> lines) {
        int sum = 0;

        for (String line : lines) {
            String firstDigit = getFirstDigit2(line);
            String lastDigit = getLastDigit2(line);

            String numAsString = firstDigit + lastDigit;
            int num = Integer.parseInt(numAsString);
            sum += num;
        }

        return sum;
    }

    private String getFirstDigit2(String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isDigit(c)) {
                return findFirstDigit(line.substring(0, i)).orElse(String.valueOf(c));
            }
        }
        return findFirstDigit(line).orElse(null);
    }

    private String getLastDigit2(String line) {
        for (int i = line.length() - 1; i >= 0; i--) {
            char c = line.charAt(i);
            if (Character.isDigit(c)) {
                return findLastDigit(line.substring(i + 1)).orElse(String.valueOf(c));
            }
        }
        return findLastDigit(line).orElse(null);
    }

    private Optional<String> findFirstDigit(String text) {
        return digitsAsString.keySet().stream()
                .filter(digit -> firstIndex(text, digit) != -1)
                .min((o1, o2) -> Integer.compare(firstIndex(text, o1), firstIndex(text, o2)))
                .map(digitsAsString::get);
    }

    private Optional<String> findLastDigit(String text) {
        return digitsAsString.keySet().stream()
                .filter(digit -> lastIndex(text, digit) != -1)
                .max((o1, o2) -> Integer.compare(lastIndex(text, o1), lastIndex(text, o2)))
                .map(digitsAsString::get);
    }

    private int firstIndex(String text, String searchWord) {
        return text.indexOf(searchWord);
    }

    private int lastIndex(String text, String searchWord) {
        return text.lastIndexOf(searchWord);
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day1.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
