package solutions.day19;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Solution {


    private record Rule(Predicate<Part> predicate, String output) {}
    private record Workflow(String key, List<Rule> rules) {}

    private record Part(int x, int m, int a, int s) {}


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

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day19.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
