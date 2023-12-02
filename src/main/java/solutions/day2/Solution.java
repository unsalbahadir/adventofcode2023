package solutions.day2;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private enum Color {
        red,
        green,
        blue
    }

    private Map<Color, Integer> maxCounts = Map.of(
            Color.red, 12,
            Color.green, 13,
            Color.blue, 14
    );

    public int getSolution(List<String> lines) {
        int sumOfIds = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (isGamePossible(StringUtils.substringAfter(line, ": "))) {
                sumOfIds += i + 1;
            }
        }

        return sumOfIds;
    }

    // line: "3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"
    private boolean isGamePossible(String line) {
        String[] sets = line.split("; ");
        for (String set : sets) {
            Map<Color, Integer> colorCounts = countsColors(set);
            for (Map.Entry<Color, Integer> entry : colorCounts.entrySet()) {
                Integer maxCount = maxCounts.get(entry.getKey());
                if (entry.getValue() > maxCount) {
                    return false;
                }
            }
        }
        return true;
    }

    // set: "3 blue, 4 red"
    private Map<Color, Integer> countsColors(String set) {
        Map<Color, Integer> colorCounts = new HashMap<>();
        String[] split = set.split(", ");
        for (String s : split) {
            String[] countAndColor = s.split(" ");
            int count = Integer.parseInt(countAndColor[0]);
            Color color = Color.valueOf(countAndColor[1]);
            colorCounts.put(color, count);
        }
        return colorCounts;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day2.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
