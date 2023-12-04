package solutions.day4;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Solution {

    public int getSolution(List<String> lines) {
        int totalScore = 0;

        for (String line : lines) {
            String card = StringUtils.substringAfter(line, ": ");
            int matchCount = countMatchingNumbers(card);
            int score = (int) Math.pow(2, matchCount - 1);
            totalScore += score;
        }

        return totalScore;
    }

    public int getSolution2(List<String> lines) {
        int totalCopyCount = 0;

        Map<Integer, Integer> cardCopyCounts = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            int cardNumber = i + 1;
            cardCopyCounts.merge(cardNumber, 1, Integer::sum);
            Integer currentCopyCount = cardCopyCounts.get(cardNumber);
            totalCopyCount += currentCopyCount;

            String line = lines.get(i);
            String card = StringUtils.substringAfter(line, ": ");
            int matchCount = countMatchingNumbers(card);
            for (int j = 1; j <= matchCount; j++) {
                cardCopyCounts.merge(cardNumber + j, currentCopyCount, Integer::sum);
            }
        }

        return totalCopyCount;
    }

    // "41 48 83 86 17 | 83 86  6 31 17  9 48 53"
    private int countMatchingNumbers(String card) {
        int count = 0;

        String winningNumbersString = StringUtils.substringBefore(card, " |"); // "41 48 83 86 17"
        String numbersPresentString = StringUtils.substringAfter(card, "| "); // "83 86  6 31 17  9 48 53"

        Set<Integer> winningNumbers = new HashSet<>();
        String[] winningNumbersSplit = winningNumbersString.split(" ");
        for (String s : winningNumbersSplit) {
            if (s.isBlank()) {
                continue;
            }
            winningNumbers.add(Integer.parseInt(s.trim()));
        }

        String[] numbersPresentSplit = numbersPresentString.split(" ");
        for (String s : numbersPresentSplit) {
            if (s.isBlank()) {
                continue;
            }
            int number = Integer.parseInt(s.trim());
            if (winningNumbers.contains(number)) {
                count++;
            }
        }

        return count;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day4.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
