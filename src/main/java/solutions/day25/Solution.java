package solutions.day25;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Solution {

    private static final class Component {
        String name;
        List<Component> connectedComponents;

        private Component(String name, List<Component> connectedComponents) {
            this.name = name;
            this.connectedComponents = connectedComponents;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Component) obj;
            return Objects.equals(this.name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "Component[" +
                    "name=" + name + ", " +
                    "connectedComponents=" + connectedComponents.stream().map(component -> component.name).toList() + ']';
        }
    }

    public int getSolution(List<String> lines) {
        Map<String, Component> components = getComponents(lines);
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        for (Component component : components.values()) {
            graph.addVertex(component.name);
        }
        for (Component component : components.values()) {
            for (Component connectedComponent : component.connectedComponents) {
                graph.addEdge(component.name, connectedComponent.name);
            }
        }

        StoerWagnerMinimumCut<String, DefaultEdge> minimumCut = new StoerWagnerMinimumCut<>(graph);
        Set<String> oneGroup = minimumCut.minCut();

        return oneGroup.size() * (components.size() - oneGroup.size());
    }

    private Map<String, Component> getComponents(List<String> lines) {
        Map<String, Component> components = new HashMap<>();

        for (String line : lines) {
            String name = StringUtils.substringBefore(line, ":");
            Component component = components.computeIfAbsent(name, n -> new Component(n, new ArrayList<>()));

            String[] connectedNames = StringUtils.substringAfter(line, ": ").split(" ");
            for (String connectedName : connectedNames) {
                Component connectedComponent = components.computeIfAbsent(connectedName, n -> new Component(n, new ArrayList<>()));

                component.connectedComponents.add(connectedComponent);
                connectedComponent.connectedComponents.add(component);
            }
        }

        return components;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day25.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
