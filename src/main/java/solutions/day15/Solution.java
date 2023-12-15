package solutions.day15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public long getSolution2(List<String> lines) {
        Map<Long, Map<String, Integer>> boxes = new HashMap<>(256);

        for (String line : lines) {
            String[] commands = line.split(",");
            for (String command : commands) {
                handleCommand(boxes, command);
            }
        }
        return getResult(boxes);
    }

    private void handleCommand(Map<Long, Map<String, Integer>> boxes, String command) {
        String label = getLabel(command);
        long hashResult = getHashResult(label);
        System.out.println("Result of label " + label + " : " + hashResult);

        Map<String, Integer> box = boxes.computeIfAbsent(hashResult, k -> new LinkedHashMap<>());
        char operation = command.charAt(label.length());
        if (operation == '-') {
            box.remove(label);
        } else {
            int focalLength = Character.getNumericValue(command.charAt(label.length() + 1));
            box.put(label, focalLength);
        }
    }

    private String getLabel(String command) {
        int operationIndex = 0;
        while (Character.isAlphabetic(command.charAt(operationIndex))) {
            operationIndex++;
        }
        return command.substring(0, operationIndex);
    }

    private long getResult(Map<Long, Map<String, Integer>> boxes) {
        long result = 0;
        for (Map.Entry<Long, Map<String, Integer>> entry : boxes.entrySet()) {
            Long boxIndex = entry.getKey();
            Map<String, Integer> box = entry.getValue();
            int slot = 1;
            for (Map.Entry<String, Integer> boxEntry : box.entrySet()) {
                Integer focalLength = boxEntry.getValue();
                long entryResult = (boxIndex + 1) * slot * focalLength;
                System.out.println("Box " + boxIndex + " " + boxEntry + " result: " + entryResult);

                result += entryResult;
                slot++;
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
        System.out.println(solution.getSolution2(lines));
    }
}
