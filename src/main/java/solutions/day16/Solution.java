package solutions.day16;

import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Solution {

    private enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    private record Position(int row, int column) {}

    private static final class Beam {
        final List<Position> positions;
        Direction direction;

        private Beam(List<Position> positions, Direction direction) {
            this.positions = positions;
            this.direction = direction;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Beam) obj;
            return Objects.equals(this.positions, that.positions) &&
                    Objects.equals(this.direction, that.direction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(positions, direction);
        }

        @Override
        public String toString() {
            return "Beam[" +
                    "positions=" + positions + ", " +
                    "direction=" + direction + ']';
        }
    }

    public int getSolution(List<String> lines) {
        int maxRow = lines.size() - 1;
        int maxColumn = lines.getFirst().length() - 1;
        Map<Position, Character> mirrors = getMirrors(lines);
        List<Position> energizedPositions = getEnergizedPositions(mirrors, maxRow, maxColumn);
        return energizedPositions.size();
    }

    private Map<Position, Character> getMirrors(List<String> lines) {
        Set<Character> mirrorCharacters = Set.of('|', '-', '/', '\\');

        Map<Position, Character> mirrors = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                if (mirrorCharacters.contains(c)) {
                    Position position = new Position(i, j);
                    mirrors.put(position, c);
                }
            }
        }
        return mirrors;
    }

    private List<Position> getEnergizedPositions(Map<Position, Character> mirrors, int maxRow, int maxColumn) {
        List<Beam> beams = new ArrayList<>();
        Beam firstBeam = new Beam(new ArrayList<>(), Direction.EAST);
        beams.add(firstBeam);

        Position startingPosition = new Position(0, 0);
        firstBeam.positions.add(startingPosition);

        Set<String> cache = new HashSet<>();

        Queue<Beam> movingBeams = new LinkedList<>(beams);
        while (!movingBeams.isEmpty()) {
            Beam currentBeam = movingBeams.poll();
            Position currentPosition = currentBeam.positions.getLast();
            Direction directionGoingTo = currentBeam.direction;

            String key = getKey(currentPosition, directionGoingTo);
            if (cache.contains(key)) { // handled this beam before (loop). same result
                continue;
            }
            cache.add(key);

            Position nextPosition;
            Character mirrorInCurrentPosition = mirrors.get(currentPosition);
            if (mirrorInCurrentPosition != null) {
                Triple<Position, Direction, Beam> nextPositionWithMirror = getNextPositionWithMirror(currentPosition, directionGoingTo, mirrorInCurrentPosition);
                Beam newBeam = nextPositionWithMirror.getRight();
                if (newBeam != null && isValid(newBeam.positions.getFirst(), maxRow, maxColumn)) {
                    beams.add(newBeam);
                    movingBeams.add(newBeam);
                }

                nextPosition = nextPositionWithMirror.getLeft();
                currentBeam.direction = nextPositionWithMirror.getMiddle();
            } else {
                nextPosition = getNextPositionWithoutMirror(currentPosition, directionGoingTo);
            }
            if (isValid(nextPosition, maxRow, maxColumn)) {
                currentBeam.positions.add(nextPosition);
                movingBeams.add(currentBeam);
            }
        }

        return beams.stream()
                .flatMap(beam -> beam.positions.stream())
                .distinct()
                .toList();
    }

    private String getKey(Position currentPosition, Direction direction) {
        return String.format("%s-%s", currentPosition, direction);
    }

    private Triple<Position, Direction, Beam> getNextPositionWithMirror(Position position, Direction direction, Character mirror) {
        if (mirror == '-') {
            if (direction == Direction.EAST || direction == Direction.WEST) {
                return Triple.of(getNextPositionWithoutMirror(position, direction), direction, null);
            } else {
                Beam newBeam = new Beam(new ArrayList<>(), Direction.EAST);
                newBeam.positions.add(getNextPositionWithoutMirror(position, Direction.EAST));

                return Triple.of(getNextPositionWithoutMirror(position, Direction.WEST), Direction.WEST, newBeam);
            }
        } else if (mirror == '|') {
            if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                return Triple.of(getNextPositionWithoutMirror(position, direction), direction, null);
            } else {
                Beam newBeam = new Beam(new ArrayList<>(), Direction.NORTH);
                newBeam.positions.add(getNextPositionWithoutMirror(position, Direction.NORTH));

                return Triple.of(getNextPositionWithoutMirror(position, Direction.SOUTH), Direction.SOUTH, newBeam);
            }
        } else if (mirror == '\\') {
            switch (direction) {
                case NORTH -> {
                    return Triple.of(new Position(position.row, position.column - 1), Direction.WEST, null);
                }
                case EAST -> {
                    return Triple.of(new Position(position.row + 1, position.column), Direction.SOUTH, null);
                }
                case SOUTH -> {
                    return Triple.of(new Position(position.row, position.column + 1), Direction.EAST, null);
                }
                case WEST -> {
                    return Triple.of(new Position(position.row - 1, position.column), Direction.NORTH, null);
                }
            }
        } else if (mirror == '/') {
            switch (direction) {
                case NORTH -> {
                    return Triple.of(new Position(position.row, position.column + 1), Direction.EAST, null);
                }
                case EAST -> {
                    return Triple.of(new Position(position.row - 1, position.column), Direction.NORTH, null);
                }
                case SOUTH -> {
                    return Triple.of(new Position(position.row, position.column - 1), Direction.WEST, null);
                }
                case WEST -> {
                    return Triple.of(new Position(position.row + 1, position.column), Direction.SOUTH, null);
                }
            }
        } else {
            throw new RuntimeException("Invalid mirror character: " + mirror);
        }
        return null;
    }

    private Position getNextPositionWithoutMirror(Position position, Direction direction) {
        return switch (direction) {
            case NORTH -> new Position(position.row - 1, position.column);
            case EAST -> new Position(position.row, position.column + 1);
            case SOUTH -> new Position(position.row + 1, position.column);
            case WEST -> new Position(position.row, position.column - 1);
        };
    }

    private boolean isValid(Position position, int maxRow, int maxColumn) {
        return position.row >= 0 && position.row <= maxRow && position.column >= 0 && position.column <= maxColumn;
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day16.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
