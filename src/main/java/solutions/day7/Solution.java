package solutions.day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    enum LetterLabel {
        A,
        K,
        Q,
        J,
        T
    }

    enum Type {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD
    }

    record Hand(String cards, long bid, Type type){}

    public long getSolution(List<String> lines) {
        List<Hand> hands = new ArrayList<>();
        for (String line : lines) {
            Hand hand = createHand(line);
            hands.add(hand);
        }
        List<Hand> sortedHands = sortHands(hands);

        long result = 0;
        long rank = 1;
        for (Hand sortedHand : sortedHands) {
            long winning = rank * sortedHand.bid;
            result += winning;
            rank++;
        }

        return result;
    }

    private Hand createHand(String line) {
        String[] split = line.split(" ");

        String cards = split[0];
        Type typeOfHand = getTypeOfHand(cards);
        Hand hand = new Hand(cards, Long.parseLong(split[1]), typeOfHand);

        return hand;
    }

    private Type getTypeOfHand(String cards) {
        Map<Character, Integer> labelCounts = new HashMap<>();
        for (int i = 0; i < cards.length(); i++) {
            char c = cards.charAt(i);
            labelCounts.merge(c, 1, Integer::sum);
        }
        Type type;
        switch (labelCounts.size()) {
            case 1 -> type = Type.FIVE_OF_A_KIND;
            case 2 -> {
                if (labelCounts.containsValue(4)) {
                    type = Type.FOUR_OF_A_KIND;
                } else {
                    type = Type.FULL_HOUSE;
                }
            }
            case 3 -> {
                if (labelCounts.containsValue(3)) {
                    type = Type.THREE_OF_A_KIND;
                } else {
                    type = Type.TWO_PAIR;
                }
            }
            case 4 -> type = Type.ONE_PAIR;
            default -> type = Type.HIGH_CARD;
        }
        return type;
    }

    private List<Hand> sortHands(List<Hand> hands) {
        List<Hand> sortedHands = hands.stream().sorted(Solution::compareHands).toList();
        return sortedHands;
    }

    private static int compareHands(Hand hand1, Hand hand2) {
        Type type1 = hand1.type;
        Type type2 = hand2.type;

        if (type1 != type2) {
            return -type1.compareTo(type2);
        } else {
            for (int i = 0; i < hand1.cards.length(); i++) {
                char char1 = hand1.cards.charAt(i);
                char char2 = hand2.cards.charAt(i);

                if (char1 != char2) {
                    if (Character.isDigit(char1) && Character.isDigit(char2)) {
                        return Integer.compare(Character.getNumericValue(char1), Character.getNumericValue(char2));
                    } else if (Character.isDigit(char1)) {
                        return -1;
                    } else if (Character.isDigit(char2)) {
                        return 1;
                    } else {
                        LetterLabel letterLabel1 = LetterLabel.valueOf(String.valueOf(char1));
                        LetterLabel letterLabel2 = LetterLabel.valueOf(String.valueOf(char2));
                        return -letterLabel1.compareTo(letterLabel2);
                    }
                }
            }
        }
        return 0;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day7.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
