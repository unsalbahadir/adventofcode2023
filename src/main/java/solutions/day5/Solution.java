package solutions.day5;

import org.apache.commons.lang3.NumberRange;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Solution {

    private final TreeMap<Long, Destination> seedToSoilMap = new TreeMap<>();
    private final TreeMap<Long, Destination> soilToFertilizerMap = new TreeMap<>();
    private final TreeMap<Long, Destination> fertilizerToWaterMap = new TreeMap<>();
    private final TreeMap<Long, Destination> waterToLightMap = new TreeMap<>();
    private final TreeMap<Long, Destination> lightToTemperatureMap = new TreeMap<>();
    private final TreeMap<Long, Destination> temperatureToHumidityMap = new TreeMap<>();
    private final TreeMap<Long, Destination> humidityToLocationMap = new TreeMap<>();

    record Destination(long start, long increment){}

    public long getSolution(List<String> lines) {
        String[] seedsAsString = StringUtils.substringAfter(lines.getFirst(), ": ").split(" ");
        List<Long> seeds = Arrays.stream(seedsAsString).map(Long::parseLong).toList();
        fillMaps(lines);

        return findMinLocation(seeds);
    }

    private void fillMaps(List<String> lines) {
        List<Map<Long, Destination>> mapOrder = List.of(
                seedToSoilMap,
                soilToFertilizerMap,
                fertilizerToWaterMap,
                waterToLightMap,
                lightToTemperatureMap,
                temperatureToHumidityMap,
                humidityToLocationMap
        );

        int index = 3;
        for (Map<Long, Destination> map : mapOrder) {
            String line = lines.get(index);
            while (!line.isBlank()) {
                putRangeIntoMap(map, line);
                index++;
                if (index >= lines.size()) {
                    break;
                }
                line = lines.get(index);
            }
            index += 2;
        }
    }

    private void putRangeIntoMap(Map<Long, Destination> map, String line) {
        String[] values = line.split(" ");
        long destinationStart = Long.parseLong(values[0]);
        long sourceStart = Long.parseLong(values[1]);
        long range = Long.parseLong(values[2]);
        Destination destination = new Destination(destinationStart, range);
        map.put(sourceStart, destination);
    }

    private long findMinLocation(List<Long> seeds) {
        long minLocation = Long.MAX_VALUE;

        for (Long seed : seeds) {
            Long soil = getFromMap(seedToSoilMap, seed);
            Long fertilizer = getFromMap(soilToFertilizerMap, soil);
            Long water = getFromMap(fertilizerToWaterMap, fertilizer);
            Long light = getFromMap(waterToLightMap, water);
            Long temperature = getFromMap(lightToTemperatureMap, light);
            Long humidity = getFromMap(temperatureToHumidityMap, temperature);
            Long location = getFromMap(humidityToLocationMap, humidity);

            minLocation = Math.min(minLocation, location);
        }

        return minLocation;
    }

    private Long getFromMap(TreeMap<Long, Destination> map, Long key) {
        Long result = key;
        Map.Entry<Long, Destination> entry = map.floorEntry(key);
        if (entry != null) {
            Long floorKey = entry.getKey();
            Destination destination = entry.getValue();
            if (key.equals(floorKey)) {
                result = destination.start;
            } else if (key <= floorKey + destination.increment) {
                long difference = key - floorKey;
                result = destination.start + difference;
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day5.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
