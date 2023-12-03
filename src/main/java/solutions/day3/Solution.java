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

    public long getSolution2(List<String> lines) {
        List<Number> numbers = new ArrayList<>();
        Set<Position> gearPositions = new HashSet<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Pair<List<Number>, Set<Position>> result = parseLine2(line, i);
            numbers.addAll(result.getLeft());
            gearPositions.addAll(result.getRight());
        }

        return getGearRatioSum(numbers, gearPositions);
    }

    private Pair<List<Number>, Set<Position>> parseLine2(String line, int row) {
        List<Number> numbers = new ArrayList<>();
        Set<Position> gearPositions = new HashSet<>();

        StringBuilder currentNumber = new StringBuilder();
        List<Position> currentNumberPositions = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (!Character.isDigit(c)) {
                if (!currentNumber.isEmpty()) {
                    int value = Integer.parseInt(currentNumber.toString());
                    numbers.add(new Number(value, currentNumberPositions));
                    currentNumber = new StringBuilder();
                    currentNumberPositions = new ArrayList<>();
                }
            } else {
                currentNumber.append(c);
                currentNumberPositions.add(new Position(row, i));
            }
            if (c == '*') {
                gearPositions.add(new Position(row, i));
            }
        }
        // add if number is at the end
        if (!currentNumber.isEmpty()) {
            numbers.add(new Number(Integer.parseInt(currentNumber.toString()), currentNumberPositions));
        }
        return Pair.of(numbers, gearPositions);
    }

    private long getGearRatioSum(List<Number> numbers, Set<Position> gearPositions) {
        long result = 0;
        for (Position gearPosition : gearPositions) {
            List<Number> numbersAdjacentToGear = getNumbersAdjacentToGear(numbers, gearPosition);
            if (numbersAdjacentToGear.size() == 2) {
                long ratio = 1;
                for (Number number : numbersAdjacentToGear) {
                    ratio *= number.value;
                }
                result += ratio;
            }
        }

        return result;
    }

    private List<Number> getNumbersAdjacentToGear(List<Number> numbers, Position gearPosition) {
        List<Number> adjacentNumbers = new ArrayList<>();
        for (Number number : numbers) {
            if (isNumberAdjacentToGear(number, gearPosition)) {
                adjacentNumbers.add(number);
                if (adjacentNumbers.size() == 2) {
                    break;
                }
            }
        }
        return adjacentNumbers;
    }

    private boolean isNumberAdjacentToGear(Number number, Position gearPosition) {
        return number.positions.stream().anyMatch(position -> {
            return new Position(position.row - 1, position.column - 1).equals(gearPosition) ||
                    new Position(position.row - 1, position.column).equals(gearPosition) ||
                    new Position(position.row - 1, position.column + 1).equals(gearPosition) ||
                    new Position(position.row, position.column - 1).equals(gearPosition) ||
                    new Position(position.row, position.column).equals(gearPosition) ||
                    new Position(position.row, position.column + 1).equals(gearPosition) ||
                    new Position(position.row + 1, position.column - 1).equals(gearPosition) ||
                    new Position(position.row + 1, position.column).equals(gearPosition) ||
                    new Position(position.row + 1, position.column + 1).equals(gearPosition);
        });
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day3.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
