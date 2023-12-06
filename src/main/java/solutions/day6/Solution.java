package solutions.day6;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Solution {

    private record Race(long time, long distance){}

    public int getSolution(List<String> lines) {
        int result = 1;

        String[] timesArray = StringUtils.substringAfter(lines.getFirst(), "Time:      ").split(" ");
        String[] distancesArray = StringUtils.substringAfter(lines.getLast(), "Distance:  ").split(" ");
        List<String> times = getWithoutEmptyStrings(timesArray);
        List<String> distances = getWithoutEmptyStrings(distancesArray);
        for (int i = 0; i < times.size(); i++) {
            Race race = new Race(Integer.parseInt(times.get(i)), Integer.parseInt(distances.get(i)));
            int numberOfWaysToWinRace = getNumberOfWaysToWinRace(race);
            result *= numberOfWaysToWinRace;
        }

        return result;
    }

    public int getSolution2(List<String> lines) {
        String[] timesArray = StringUtils.substringAfter(lines.getFirst(), "Time:      ").split(" ");
        String[] distancesArray = StringUtils.substringAfter(lines.getLast(), "Distance:  ").split(" ");
        List<String> times = getWithoutEmptyStrings(timesArray);
        List<String> distances = getWithoutEmptyStrings(distancesArray);

        long time = Long.parseLong(String.join("", times));
        long distance = Long.parseLong(String.join("", distances));
        Race race = new Race(time, distance);
        return getNumberOfWaysToWinRace(race);
    }

    private List<String> getWithoutEmptyStrings(String[] strings) {
        return Arrays.stream(strings)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private int getNumberOfWaysToWinRace(Race race) {
        int numberOfWays = 0;
        for (long numberOfSecondsToHoldButton = 0; numberOfSecondsToHoldButton <= race.time; numberOfSecondsToHoldButton++) {
            long travelTime = race.time - numberOfSecondsToHoldButton;
            long distanceTravelled = numberOfSecondsToHoldButton * travelTime;
            if (distanceTravelled > race.distance) {
                numberOfWays++;
            }
        }
        return numberOfWays;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day6.txt"));
        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
