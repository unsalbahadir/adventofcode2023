package solutions.day20;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    static Map<Pulse, Long> pulseCounter;

    static Queue<Module> mainQueue = new LinkedList<>();

    private static final String TARGET_MODULE_NAME = "rx";
    private static String INPUT_MODULE_OF_TARGET;

    private enum ModuleType {
        FLIP_FLOP,
        CONJUNCTION,
        BROADCASTER,
        BUTTON,
        DUMMY
    }

    private enum Pulse {
        LOW,
        HIGH
    }



    private abstract class Module {
        String name;
        ModuleType moduleType;
        Queue<Pulse> receivedPulses;
        List<String> destinationModuleNames;
        List<Module> destinationModules;


        public Module(String name, ModuleType moduleType, List<String> destinationModuleNames) {
            this.name = name;
            this.moduleType = moduleType;
            this.destinationModuleNames = destinationModuleNames;
            receivedPulses = new LinkedList<>();
        }

        void receivePulse(Module inputModule, Pulse pulse) {
            pulseCounter.merge(pulse, 1L, Long::sum);
        }

        abstract void processReceivedPulses(long buttonPresses);

        @Override
        public String toString() {
            return "Module{" +
                    "name='" + name + '\'' +
                    ", moduleType=" + moduleType +
                    '}';
        }
    }

    private class FlipFlowModule extends Module {
        boolean isOn = false;

        public FlipFlowModule(String name, List<String> destinationModuleNames) {
            super(name, ModuleType.FLIP_FLOP, destinationModuleNames);
        }

        @Override
        void receivePulse(Module inputModule, Pulse pulse) {
            super.receivePulse(inputModule, pulse);
            receivedPulses.add(pulse);
        }

        @Override
        void processReceivedPulses(long buttonPresses) {
            while (!receivedPulses.isEmpty()) {
                Pulse pulse = receivedPulses.poll();
                if (pulse == Pulse.LOW) {
                    Pulse pulseToSend = isOn ? Pulse.LOW : Pulse.HIGH;
                    for (Module destinationModule : destinationModules) {
                        if (destinationModule != null) {
                            printStep(name, pulseToSend, destinationModule.name);
                            destinationModule.receivePulse(this, pulseToSend);
                            mainQueue.add(destinationModule);
                        }
                    }
                    isOn = !isOn;
                }

            }
        }
    }

    private class ConjunctionModule extends Module {
        Map<Module, Pulse> mostRecentPulseFromInputs = new HashMap<>();

        Queue<Pair<Module, Pulse>> receivedPulsesWithInputs = new LinkedList<>();

        public ConjunctionModule(String name, List<String> destinationModuleNames) {
            super(name, ModuleType.CONJUNCTION, destinationModuleNames);
        }

        @Override
        void receivePulse(Module inputModule, Pulse pulse) {
            super.receivePulse(inputModule, pulse);
            receivedPulsesWithInputs.add(Pair.of(inputModule, pulse));
            if (name.equals("gh") && pulse == Pulse.HIGH) {
                System.out.println("gh received a High pulse from input module: " + inputModule);
            }
        }

        @Override
        void processReceivedPulses(long buttonPresses) {
            while (!receivedPulsesWithInputs.isEmpty()) {
                Pair<Module, Pulse> poll = receivedPulsesWithInputs.poll();
                Module inputModule = poll.getLeft();
                Pulse pulse = poll.getRight();

                mostRecentPulseFromInputs.put(inputModule, pulse);
                for (Module destinationModule : destinationModules) {
                    if (name.equals(INPUT_MODULE_OF_TARGET) && pulse == Pulse.HIGH) {
                        System.out.println("gh received a High pulse at step: " + buttonPresses + " from input module: " + inputModule);
                    }
                    if (destinationModule != null) {
                        Pulse pulseToSend;
                        if (mostRecentPulseFromInputs.values().stream().allMatch(p -> p == Pulse.HIGH)) {
                            pulseToSend = Pulse.LOW;
                        } else {
                            pulseToSend = Pulse.HIGH;
                        }
                        printStep(name, pulseToSend, destinationModule.name);
                        destinationModule.receivePulse(this, pulseToSend);
                        mainQueue.add(destinationModule);
                    }
                }
            }
        }
    }

    private class BroadcasterModule extends Module {

        public BroadcasterModule(String name, List<String> destinationModuleNames) {
            super(name, ModuleType.BROADCASTER, destinationModuleNames);
        }

        @Override
        void receivePulse(Module inputModule, Pulse pulse) {
            super.receivePulse(inputModule, pulse);
            for (Module destinationModule : destinationModules) {
                printStep(name, pulse, destinationModule.name);
                destinationModule.receivePulse(this, pulse);
                mainQueue.add(destinationModule);
            }
//            destinationModules.forEach(Module::processReceivedPulses);
        }

        @Override
        void processReceivedPulses(long buttonPresses) {
            // NO-OP
        }
    }

    private class DummyModule extends Module {

        public DummyModule(String name) {
            super(name, ModuleType.DUMMY, List.of());
        }

        @Override
        void receivePulse(Module inputModule, Pulse pulse) {
            super.receivePulse(inputModule, pulse);
            receivedPulses.add(pulse);
            if (name.equals("rx") && pulse == Pulse.LOW) {
                System.out.println("Low pulse received by rx");
            }
        }

        @Override
        void processReceivedPulses(long buttonPresses) {
            // NO-OP
        }
    }

    private class ButtonModule {
        BroadcasterModule broadcasterModule;

        public ButtonModule(BroadcasterModule broadcasterModule) {
            this.broadcasterModule = broadcasterModule;
        }

        void sendPulse() {
            printStep("button", Pulse.LOW, broadcasterModule.name);
            broadcasterModule.receivePulse(null, Pulse.LOW);
        }
    }

    public long getSolution(List<String> lines) {
        pulseCounter = new HashMap<>();
        pulseCounter.put(Pulse.HIGH, 0L);
        pulseCounter.put(Pulse.LOW, 0L);

        Map<String, Module> modules = createModules(lines);
        ButtonModule buttonModule = new ButtonModule((BroadcasterModule) modules.get("broadcaster"));

        for (int i = 0; i < 1000; i++) {
            buttonModule.sendPulse();
            while (!mainQueue.isEmpty()) {
                Module module = mainQueue.poll();
                module.processReceivedPulses(i);
            }
        }

        return pulseCounter.get(Pulse.LOW) * pulseCounter.get(Pulse.HIGH);
    }

    public long getSolution2(List<String> lines) {
        pulseCounter = new HashMap<>();
        pulseCounter.put(Pulse.HIGH, 0L);
        pulseCounter.put(Pulse.LOW, 0L);

        Map<String, Module> modules = createModules(lines);
        ButtonModule buttonModule = new ButtonModule((BroadcasterModule) modules.get("broadcaster"));

        Map<Module, Long> cycles = new HashMap<>();
        ConjunctionModule inputModuleOfTarget = (ConjunctionModule) modules.get(INPUT_MODULE_OF_TARGET);
        for (Module module : inputModuleOfTarget.mostRecentPulseFromInputs.keySet()) {
            cycles.put(module, -1L);
        }

        long buttonPresses = 0;
        while (true) {
            buttonPresses++;
            buttonModule.sendPulse();
            while (!mainQueue.isEmpty()) {
                Module module = mainQueue.poll();
                if (module.name.equals(INPUT_MODULE_OF_TARGET)) {
                    for (Pair<Module, Pulse> receivedPulsesWithInput : inputModuleOfTarget.receivedPulsesWithInputs) {
                        if (receivedPulsesWithInput.getRight() == Pulse.HIGH) {
                            cycles.put(receivedPulsesWithInput.getLeft(), buttonPresses);
                        }
                    }
                    if (cycles.values().stream().noneMatch(cycle -> cycle < 0)) {
                        return lcm(cycles.values());
                    }
                }
                module.processReceivedPulses(buttonPresses);
            }
        }
    }

    private Map<String, Module> createModules(List<String> lines) {
        Map<String, Module> modules = new HashMap<>();

        for (String line : lines) {
            Module module;
            String typeAndName = StringUtils.substringBefore(line, " ->");
            String destinations = StringUtils.substringAfter(line, "-> ");
            List<String> destinationList = Arrays.stream(destinations.split(", ")).toList();
            if (typeAndName.startsWith("%")) {
                module = new FlipFlowModule(typeAndName.substring(1), destinationList);
            } else if (typeAndName.startsWith("&")) {
                module = new ConjunctionModule(typeAndName.substring(1), destinationList);
            } else if (typeAndName.equals("broadcaster")) {
                module = new BroadcasterModule(typeAndName, destinationList);
            } else {
                System.out.println("Unknown module type: " + line);
                module = null;
            }

            modules.put(module.name, module);
        }
        List<String> dummyModuleNames = modules.values().stream()
                .flatMap(module -> module.destinationModuleNames.stream())
                .filter(s -> modules.get(s) == null)
                .toList();

        for (String dummyModuleName : dummyModuleNames) {
            Module dummyModule = new DummyModule(dummyModuleName);
            modules.put(dummyModuleName, dummyModule);
        }

        // put modules in place
        for (Module module : modules.values()) {
            if (module.destinationModuleNames.contains(TARGET_MODULE_NAME)) {
                INPUT_MODULE_OF_TARGET = module.name;
            }
            module.destinationModules = module.destinationModuleNames.stream()
                    .map(modules::get)
                    .collect(Collectors.toList());
        }

        for (Module inputModule : modules.values()) {
            for (Module destinationModule : inputModule.destinationModules) {
                if (destinationModule instanceof ConjunctionModule conjunctionModule) {
                    conjunctionModule.mostRecentPulseFromInputs.put(inputModule, Pulse.LOW);
                }
            }
        }
        return modules;
    }

    private long lcm(Collection<Long> numbers) {
        return numbers.stream().reduce(this::lcm).orElse(0L);
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

    private void printStep(String sendingModuleName, Pulse pulse, String receivingModuleName) {
//        System.out.println(String.format("%s -%s-> %s", sendingModuleName, Pulse.LOW, receivingModuleName));
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day20.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
