package solutions.day10;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {

    private enum Direction {
        N,
        E,
        W,
        S
    }

    private static final Map<Direction, Direction> reverseDirections = Map.of(
            Direction.N, Direction.S,
            Direction.E, Direction.W,
            Direction.W, Direction.E,
            Direction.S, Direction.N
    );

    private enum Pipe {
        NS('|'),
        EW('-'),
        NE('L'),
        NW('J'),
        SW('7'),
        SE('F');

        private final char character;

        Pipe(char c) {
            this.character = c;
        }

        public char getCharacter() {
            return character;
        }
    }

    private record Board(Set<Position> positions, Map<Position, Character> characters) {
    }

    private record Position(int row, int column) {
    }

    public int getSolution(List<String> lines) {
        Board board = convertToBoard(lines);
        Position startingPosition = getStartingPosition(board);
        List<Position> loop = findLoop(board, startingPosition);
        return loop.size() / 2;
    }

    private Board convertToBoard(List<String> lines) {
        Set<Position> positions = new HashSet<>();
        Map<Position, Character> characters = new HashMap<>();
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            for (int column = 0; column < line.length(); column++) {
                char character = line.charAt(column);
                Position position = new Position(row + 1, column + 1);
                positions.add(position);
                characters.put(position, character);
            }
        }
        return new Board(positions, characters);
    }

    private Position getStartingPosition(Board board) {
        return board.positions.stream()
                .filter(position -> board.characters.get(position) == 'S')
                .findFirst()
                .orElse(null);
    }

    private List<Position> findLoop(Board board, Position startingPosition) {
        List<Position> loop = new ArrayList<>();
        loop.add(startingPosition);
        Position currentPosition = startingPosition;
        Direction directionComingFrom = null;
        boolean firstIteration = true;
        while (firstIteration || !currentPosition.equals(startingPosition)) {
            Pair<Position, Direction> nextPositionAndDirection;
            Position previousPosition = null;
            if (firstIteration) {
                nextPositionAndDirection = getFirstPipe(board, currentPosition);
                firstIteration = false;
            } else {
                nextPositionAndDirection = getNextPosition(board, currentPosition, directionComingFrom);
                previousPosition = loop.get(loop.size() - 2);
            }
            currentPosition = nextPositionAndDirection.getLeft();
            directionComingFrom = reverseDirections.get(nextPositionAndDirection.getRight());
//            System.out.println("CurrentPosition: " + currentPosition + "(" + board.characters.get(currentPosition) + ")" + ", PreviousPosition: " + previousPosition);
            loop.add(currentPosition);
        }

        return loop;
    }

    private Pair<Position, Direction> getFirstPipe(Board board, Position currentPosition) {
        Map<Direction, Position> positionInDirection = getAdjacentPositions(currentPosition);
        for (Map.Entry<Direction, Position> entry : positionInDirection.entrySet()) {
            Direction direction = entry.getKey();
            Position pipePosition = entry.getValue();
            Character character = board.characters.get(pipePosition);
            if (character == '.') {
                continue;
            }
            boolean isValid = switch (direction) {
                case N -> Stream.of(Pipe.NS, Pipe.SE, Pipe.SW).anyMatch(pipe -> character == pipe.character);
                case E -> Stream.of(Pipe.EW, Pipe.SW, Pipe.NW).anyMatch(pipe -> character == pipe.character);
                case W -> Stream.of(Pipe.EW, Pipe.SE, Pipe.NE).anyMatch(pipe -> character == pipe.character);
                case S -> Stream.of(Pipe.NS, Pipe.NE, Pipe.NW).anyMatch(pipe -> character == pipe.character);
            };
            if (isValid) {
                return Pair.of(pipePosition, direction);
            }
        }
        return null;
    }

    private Map<Direction, Position> getAdjacentPositions(Position position) {
        return Arrays.stream(Direction.values())
                .collect(Collectors.toMap(direction -> direction, direction -> getPositionInDirection(position, direction)));
    }

    private Pair<Position, Direction> getNextPosition(Board board, Position position, Direction directionComingFrom) {
        Character character = board.characters.get(position);
        Direction directionToGo = switch (directionComingFrom) {
            case N -> {
                if (character == Pipe.NE.character) {
                    yield Direction.E;
                } else if (character == Pipe.NW.character) {
                    yield Direction.W;
                } else if (character == Pipe.NS.character) {
                    yield Direction.S;
                } else {
                    yield null;
                }
            }
            case E -> {
                if (character == Pipe.NE.character) {
                    yield Direction.N;
                } else if (character == Pipe.EW.character) {
                    yield Direction.W;
                } else if (character == Pipe.SE.character) {
                    yield Direction.S;
                } else {
                    yield null;
                }
            }
            case W -> {
                if (character == Pipe.NW.character) {
                    yield Direction.N;
                } else if (character == Pipe.EW.character) {
                    yield Direction.E;
                } else if (character == Pipe.SW.character) {
                    yield Direction.S;
                } else {
                    yield null;
                }
            }
            case S -> {
                if (character == Pipe.SE.character) {
                    yield Direction.E;
                } else if (character == Pipe.SW.character) {
                    yield Direction.W;
                } else if (character == Pipe.NS.character) {
                    yield Direction.N;
                } else {
                    yield null;
                }
            }
        };
        Position nextPosition = getPositionInDirection(position, directionToGo);
        return Pair.of(nextPosition, directionToGo);
    }

    private Position getPositionInDirection(Position position, Direction direction) {
        return switch (direction) {
            case N -> new Position(position.row - 1, position.column);
            case E -> new Position(position.row, position.column + 1);
            case W -> new Position(position.row, position.column - 1);
            case S -> new Position(position.row + 1, position.column);
        };
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day10.txt"));
        System.out.println(solution.getSolution(lines));
    }
}
