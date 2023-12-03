package solutions.day3;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Solution {

    record Position(int row, int column) {}
    record Number(int value, List<Position> positions) {}

    public int getSolution(List<String> lines) {
        List<Number> numbers = new ArrayList<>();
        Set<Position> specialCharacterPositions = new HashSet<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Pair<List<Number>, Set<Position>> result = parseLine(line, i);
            numbers.addAll(result.getLeft());
            specialCharacterPositions.addAll(result.getRight());
        }

        List<Number> numbersAdjacentToSpecialCharacter = getNumbersAdjacentToSpecialCharacter(numbers, specialCharacterPositions);
        int sum = numbersAdjacentToSpecialCharacter.stream().mapToInt(number -> number.value).sum();

        return sum;
    }

    private Pair<List<Number>, Set<Position>> parseLine(String line, int row) {
        List<Number> numbers = new ArrayList<>();
        Set<Position> specialCharacterPositions = new HashSet<>();

        StringBuilder currentNumber = new StringBuilder();
        List<Position> currentNumberPositions = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (!Character.isDigit(c)) {
                if (!currentNumber.isEmpty()) {
                    int value = Integer.parseInt(currentNumber.toString());
//                    int characterBeforeNumber = currentNumberPositions.getFirst().column - 1;
//                    if (characterBeforeNumber >= 0 && line.charAt(characterBeforeNumber) == '-') {
//                        value = -value;
//                    }
                    numbers.add(new Number(value, currentNumberPositions));
                    currentNumber = new StringBuilder();
                    currentNumberPositions = new ArrayList<>();
                }
            } else {
                currentNumber.append(c);
                currentNumberPositions.add(new Position(row, i));
            }
            if (isSpecialCharacter(c)) {
                specialCharacterPositions.add(new Position(row, i));
            }
        }
        // add if number is at the end
        if (!currentNumber.isEmpty()) {
            numbers.add(new Number(Integer.parseInt(currentNumber.toString()), currentNumberPositions));
        }
        return Pair.of(numbers, specialCharacterPositions);
    }

    private List<Number> getNumbersAdjacentToSpecialCharacter(List<Number> numbers, Set<Position> specialCharacterPositions) {
        return numbers.stream()
                .filter(number -> number.positions.stream().anyMatch(position -> isAdjacentToASpecialCharacter(position, specialCharacterPositions)))
                .collect(Collectors.toList());
    }

    private boolean isAdjacentToASpecialCharacter(Position numberPosition, Set<Position> specialCharacterPositions) {
        return specialCharacterPositions.contains(new Position(numberPosition.row - 1, numberPosition.column - 1)) ||
                specialCharacterPositions.contains(new Position(numberPosition.row - 1, numberPosition.column)) ||
                specialCharacterPositions.contains(new Position(numberPosition.row - 1, numberPosition.column + 1)) ||
                specialCharacterPositions.contains(new Position(numberPosition.row, numberPosition.column - 1)) ||
                specialCharacterPositions.contains(new Position(numberPosition.row, numberPosition.column)) ||
                specialCharacterPositions.contains(new Position(numberPosition.row, numberPosition.column + 1)) ||
                specialCharacterPositions.contains(new Position(numberPosition.row + 1, numberPosition.column - 1)) ||
                specialCharacterPositions.contains(new Position(numberPosition.row + 1, numberPosition.column)) ||
                specialCharacterPositions.contains(new Position(numberPosition.row + 1, numberPosition.column + 1));
    }

    private boolean isSpecialCharacter(Character character) {
        return !Character.isLetterOrDigit(character) && character != '.';
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day3.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
