package solutions.day19;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class Solution {


    private record Rule(Predicate<Part> predicate, String output) {}
    private record Workflow(String key, List<Rule> rules) {}

    private record Part(int x, int m, int a, int s) {}

    private record Rule2(String field, String operator, int checkedValue, String output) {}
    private record Workflow2(String key, List<Rule2> rules) {}
    private record PartWithRange(Map<Field, Range<Integer>> ranges) {}
    private record Iteration(PartWithRange partWithRange, Workflow2 currentWorkflow) {}

    private enum Field {
        x,
        m,
        a,
        s
    }
    public int getSolution(List<String> lines) {
        Map<String, Workflow> workflows = new HashMap<>();
        int index = 0;
        while (!lines.get(index).isEmpty()) {
            String line = lines.get(index);
            Workflow workflow = getWorkflow(line);
            workflows.put(workflow.key, workflow);
            index++;
        }
        index++;

        List<Part> parts = new ArrayList<>();
        for (int i = index; i < lines.size(); i++) {
            String line = lines.get(i);
            Part part = getPart(line);
            parts.add(part);
        }

        List<Part> acceptedParts = getAcceptedParts(workflows, parts);
        System.out.println("Accepted parts:" + acceptedParts);

        int result = 0;
        for (Part acceptedPart : acceptedParts) {
            result += acceptedPart.x + acceptedPart.m + acceptedPart.a + acceptedPart.s;
        }

        return result;
    }

    private Workflow getWorkflow(String line) {
        String key = StringUtils.substringBefore(line, "{");
        List<Rule> rules = new ArrayList<>();

        String rulesString = StringUtils.substringBetween(line, "{", "}");
        String[] split = rulesString.split(",");
        for (String ruleString : split) {
            Rule rule;
            if (ruleString.contains(":")) {
                String field = ruleString.substring(0, 1);
                String operator = ruleString.substring(1, 2);
                int i = ruleString.indexOf(":");
                int checkedValue = Integer.parseInt(ruleString.substring(2, i));
                String output = ruleString.substring(i + 1);

                Predicate<Part> predicate = part -> {
                    int value;
                    if (field.equals("x")) {
                        value = part.x;
                    } else if (field.equals("m")) {
                        value = part.m;
                    } else if (field.equals("a")) {
                        value = part.a;
                    } else if (field.equals("s")) {
                        value = part.s;
                    } else {
                        System.out.println("Field is something else: " + field);
                        value = -1;
                    }

                    if (operator.equals(">")) {
                        return value > checkedValue;
                    } else if (operator.equals("<")) {
                        return value < checkedValue;
                    } else {
                        System.out.println("Operator is something else: " + operator);
                        return false;
                    }
                };
                rule = new Rule(predicate, output);
            } else {
                rule = new Rule(workflow -> true, ruleString);
            }
            rules.add(rule);
        }

        return new Workflow(key, rules);
    }

    private Part getPart(String line) {
        String values = StringUtils.substringBetween(line, "{", "}");
        String[] split = values.split(",");
        int x = Integer.parseInt(split[0].substring(2));
        int m = Integer.parseInt(split[1].substring(2));
        int a = Integer.parseInt(split[2].substring(2));
        int s = Integer.parseInt(split[3].substring(2));

        return new Part(x, m, a, s);
    }

    private List<Part> getAcceptedParts(Map<String, Workflow> workflows, List<Part> parts) {
        List<Part> acceptedParts = new ArrayList<>();

        for (Part part : parts) {
            if (isPartAccepted(workflows, part)) {
                acceptedParts.add(part);
            }
        }
        return acceptedParts;
    }

    private boolean isPartAccepted(Map<String, Workflow> workflows, Part part) {
//        StringBuilder path = new StringBuilder("Path for part: " + part);
//        path.append("-> in");
        Workflow currentWorkflow = workflows.get("in");
        while (currentWorkflow != null) {
            for (Rule rule : currentWorkflow.rules) {
                boolean test = rule.predicate.test(part);
                if (test) {
                    if (rule.output.equals("A")) {
//                        path.append(" -> A");
//                        System.out.println(path);
                        return true;
                    } else if (rule.output.equals("R")) {
//                        path.append(" -> R");
//                        System.out.println(path);
                        return false;
                    } else {
                        currentWorkflow = workflows.get(rule.output);
//                        path.append(" -> ").append(rule.output);
                        break;
                    }
                }
            }
        }
//        System.out.println(path);
        return false;
    }

    public long getSolution2(List<String> lines) {
        Map<String, Workflow2> workflows = new HashMap<>();
        int index = 0;
        while (!lines.get(index).isEmpty()) {
            String line = lines.get(index);
            Workflow2 workflow = getWorkflow2(line);
            workflows.put(workflow.key, workflow);
            index++;
        }

        List<PartWithRange> acceptedPartRanges = findAcceptedPartRanges(workflows);
        System.out.println(acceptedPartRanges);

        long result = 0;
        for (PartWithRange acceptedPartRange : acceptedPartRanges) {
            long combinations = 1;
            for (Range<Integer> value : acceptedPartRange.ranges.values()) {
                int size = value.getMaximum() - value.getMinimum() + 1;
                combinations *= size;
            }
            result += combinations;
        }
        return result;
    }

    private List<PartWithRange> findAcceptedPartRanges(Map<String, Workflow2> workflows) {
        List<PartWithRange> acceptedPartRanges = new ArrayList<>();

        Queue<Iteration> queue = new LinkedList<>();

        Map<Field, Range<Integer>> initialRanges = Map.of(
                Field.x, Range.of(1, 4000),
                Field.m, Range.of(1, 4000),
                Field.a, Range.of(1, 4000),
                Field.s, Range.of(1, 4000)
        );
        PartWithRange firstPartWithRange = new PartWithRange(initialRanges);
        Workflow2 firstWorkflow = workflows.get("in");
        Iteration iteration = new Iteration(firstPartWithRange, firstWorkflow);
        queue.add(iteration);

        while (!queue.isEmpty()) {
            Iteration poll = queue.poll();
            PartWithRange currentPart = poll.partWithRange;
            Workflow2 currentWorkflow = poll.currentWorkflow;

            for (Rule2 rule : currentWorkflow.rules) {
                if (rule.operator == null) {
                    if (rule.output.equals("A")) {
                        acceptedPartRanges.add(currentPart);
                        break;
                    }
                    if (rule.output.equals("R")) {
                        break;
                    }

                    Workflow2 newWorkflow = workflows.get(rule.output);
                    Iteration newIteration = new Iteration(currentPart, newWorkflow);
                    queue.add(newIteration);
                } else {
                    Field field = Field.valueOf(rule.field);
                    Range<Integer> fieldRange = getFieldRange(currentPart, field);
                    boolean isGreaterThan = rule.operator.equals(">");

                    List<Range<Integer>> ranges = splitRanges(fieldRange, isGreaterThan, rule.checkedValue);
                    Range<Integer> passingRange = ranges.getFirst();

                    Map<Field, Range<Integer>> newRangesForPassingPart = new HashMap<>(currentPart.ranges);
                    newRangesForPassingPart.put(field, passingRange);
                    PartWithRange newPart = new PartWithRange(newRangesForPassingPart);

                    if (rule.output.equals("A")) {
                        acceptedPartRanges.add(newPart);
                    } else if (!rule.output.equals("R")) {
                        Workflow2 newWorkflow = workflows.get(rule.output);
                        Iteration newIteration = new Iteration(newPart, newWorkflow);
                        queue.add(newIteration);
                    }

                    Range<Integer> notPassingRange = ranges.getLast();
                    Map<Field, Range<Integer>> newRangesForNotPassingPart = new HashMap<>(currentPart.ranges);
                    newRangesForNotPassingPart.put(field, notPassingRange);
                    currentPart = new PartWithRange(newRangesForNotPassingPart);
                }
            }
        }

        return acceptedPartRanges;
    }

    private Range<Integer> getFieldRange(PartWithRange part, Field field) {
        return part.ranges.get(field);
    }

    private List<Range<Integer>> splitRanges(Range<Integer> initialRange, boolean isGreaterThan, int checkedValue) {
        if (isGreaterThan) {
            Range<Integer> greaterRange = Range.of(checkedValue + 1, initialRange.getMaximum());
            Range<Integer> lesserRange = Range.of(initialRange.getMinimum(), checkedValue);

            return List.of(greaterRange, lesserRange);
        } else {
            Range<Integer> greaterRange = Range.of(checkedValue, initialRange.getMaximum());
            Range<Integer> lesserRange = Range.of(initialRange.getMinimum(), checkedValue - 1);

            return List.of(lesserRange, greaterRange);
        }
    }

    private Workflow2 getWorkflow2(String line) {
        String key = StringUtils.substringBefore(line, "{");
        List<Rule2> rules = new ArrayList<>();

        String rulesString = StringUtils.substringBetween(line, "{", "}");
        String[] split = rulesString.split(",");
        for (String ruleString : split) {
            Rule2 rule2;
            if (ruleString.contains(":")) {
                String field = ruleString.substring(0, 1);
                String operator = ruleString.substring(1, 2);
                int i = ruleString.indexOf(":");
                int checkedValue = Integer.parseInt(ruleString.substring(2, i));
                String output = ruleString.substring(i + 1);

                rule2 = new Rule2(field, operator, checkedValue, output);
            } else {
                rule2 = new Rule2(null, null, -1, ruleString);
            }
            rules.add(rule2);
        }

        return new Workflow2(key, rules);
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day19.txt"));
//        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
