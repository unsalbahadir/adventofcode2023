package solutions.day8;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Solution {

    private static class Node {
        String element;
        Node left;
        Node right;

        public Node(String element) {
            this.element = element;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Node) obj;
            return Objects.equals(this.element, that.element) &&
                    Objects.equals(this.left, that.left) &&
                    Objects.equals(this.right, that.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element, left, right);
        }

        @Override
        public String toString() {
            return "Node[" +
                    "element=" + element + ", " +
                    "left=" + left.element + ", " +
                    "right=" + right.element + ']';
        }
    }

    public int getSolution(List<String> lines) {
        String commands = lines.getFirst();
        Map<String, Node> nodeMap = buildNodes(lines.subList(2, lines.size()));
        Node firstNode = nodeMap.get("AAA");
        return navigate(commands, firstNode);
    }

    private Map<String, Node> buildNodes(List<String> lines) {
        Map<String, Node> nodeMap = new HashMap<>();
        for (String line : lines) {
            String[] split = line.split(" = ");

            String element = split[0];
            Node node = getNode(nodeMap, element);

            String[] children = StringUtils.substringBetween(split[1], "(", ")").split(", ");
            Node left = getNode(nodeMap, children[0]);
            Node right = getNode(nodeMap, children[1]);

            node.left = left;
            node.right = right;
        }

        return nodeMap;
    }

    private Node getNode(Map<String, Node> nodeMap, String element) {
        Node node = nodeMap.get(element);
        if (node == null) {
            node = new Node(element);
            nodeMap.put(element, node);
        }
        return node;
    }

    private int navigate(String commands, Node currentNode) {
        int steps = 0;
        int commandIndex = 0;

        while (!currentNode.element.equals("ZZZ")) {
            char c = commands.charAt(commandIndex);
            if (c == 'L') {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
            commandIndex++;
            if (commandIndex >= commands.length()) {
                commandIndex = 0;
            }
            steps++;
        }
        return steps;
    }

    public long getSolution2(List<String> lines) {
        String commands = lines.getFirst();
        Map<String, Node> nodeMap = buildNodes(lines.subList(2, lines.size()));
        List<Node> startingNodes = nodeMap.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith("A"))
                .map(Map.Entry::getValue)
                .toList();
        List<Long> results = startingNodes.parallelStream()
                .map(node -> (long) navigate2(commands, node))
                .toList();
        return results.stream().reduce(this::lcm).orElse(0L);
    }

    private int navigate2(String commands, Node currentNode) {
        int steps = 0;
        int commandIndex = 0;

        while (!currentNode.element.endsWith("Z")) {
            char c = commands.charAt(commandIndex);
            if (c == 'L') {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
            commandIndex++;
            if (commandIndex >= commands.length()) {
                commandIndex = 0;
            }
            steps++;
        }
        return steps;
    }

    private long lcm(long number1, long number2) {
        long gcd = gcd(number1, number2);
        return (number1 * number2) / gcd;
    }

    private long gcd(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return number1 + number2;
        }
        long biggerNumber = Math.max(number1, number2);
        long smallerNumber = Math.min(number1, number2);
        return gcd(biggerNumber % smallerNumber, smallerNumber);
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day8.txt"));
//        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
