package solutions.day22;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Solution {

    private record Position(int x, int y, int z) {

    }

    private static final class Brick {
        String name;
        List<Position> positions;

        Set<Brick> supportedBricks = new HashSet<>();
        Set<Brick> supportedByBricks = new HashSet<>();

        boolean markedToFall = false;

        private Brick(String name, List<Position> positions) {
            this.name = name;
            this.positions = positions;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Brick) obj;
            return Objects.equals(this.name, that.name) &&
                    Objects.equals(this.positions, that.positions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, positions);
        }

        @Override
        public String toString() {
            return "Brick{" +
                    "name='" + name + '\'' +
                    ", positions=" + positions +
                    ", supportedBricks=" + supportedBricks.stream().map(brick -> brick.name).toList() +
                    ", supportedByBricks=" + supportedByBricks.stream().map(brick -> brick.name).toList() +
                    '}';
        }
    }

    public int getSolution(List<String> lines) {
        List<Brick> bricks = getBricks(lines);
        bricks = bricks.stream().sorted(Comparator.comparing(brick -> brick.positions.getFirst().z)).toList();
//        System.out.println("Bricks before falling: ");
//        for (Brick brick : bricks) {
//            System.out.println(brick);
//        }

        letBricksFall(bricks);

//        System.out.println("Bricks after falling: ");
//        for (Brick brick : bricks) {
//            System.out.println(brick);
//        }

        List<Brick> bricksSafeToDisintegrate = bricks.stream()
                .filter(this::isSafeToDisintegrate)
                .toList();

//        System.out.println("Bricks safe to disintegrate: ");
//        for (Brick bricksNotSupportingAnyBrick : bricksSafeToDisintegrate) {
//            System.out.println(bricksNotSupportingAnyBrick);
//        }

        return bricksSafeToDisintegrate.size();
    }

    public int getSolution2(List<String> lines) {
        List<Brick> bricks = getBricks(lines);
        bricks = bricks.stream().sorted(Comparator.comparing(brick -> brick.positions.getFirst().z)).toList();

        letBricksFall(bricks);

        int totalNumberOfBricksToFall = 0;
        for (Brick brick : bricks) {
            Set<Brick> bricksToFallWhenDisintegrated = getBricksToFallWhenDisintegratedBFS(brick);
//            System.out.println("Bricks to fall when brick " + brick + " is disintegrated: " + bricksToFallWhenDisintegrated.size());
            totalNumberOfBricksToFall += bricksToFallWhenDisintegrated.size();
        }

        return totalNumberOfBricksToFall;
    }

    private List<Brick> getBricks(List<String> lines) {
        List<Brick> bricks = new ArrayList<>();

        int brickId = 1;
        for (String line : lines) {
            String[] split = line.split("~");
            String start = split[0];
            String end = split[1];

            int startX = Integer.parseInt(start.split(",")[0]);
            int startY = Integer.parseInt(start.split(",")[1]);
            int startZ = Integer.parseInt(start.split(",")[2]);

            int endX = Integer.parseInt(end.split(",")[0]);
            int endY = Integer.parseInt(end.split(",")[1]);
            int endZ = Integer.parseInt(end.split(",")[2]);

            List<Position> positions = new ArrayList<>();
            if (startX != endX) {
                for (int i = startX; i <= endX; i++) {
                    Position position = new Position(i, startY, startZ);
                    positions.add(position);
                }
            } else if (startY != endY) {
                for (int i = startY; i <= endY; i++) {
                    Position position = new Position(startX, i, startZ);
                    positions.add(position);
                }
            } else if (startZ != endZ) {
                for (int i = startZ; i <= endZ; i++) {
                    Position position = new Position(startX, startY, i);
                    positions.add(position);
                }
            } else {
                Position position = new Position(startX, startY, startZ);
                positions.add(position);
            }
            Brick brick = new Brick(String.valueOf(brickId), positions);
            bricks.add(brick);
            brickId++;
        }

        return bricks;
    }

    private void letBricksFall(List<Brick> bricks) {
        for (int i = 0; i < bricks.size(); i++) {
            Brick brickToFall = bricks.get(i);
            boolean blocked = false;
            while (!blocked) {
                if (brickToFall.positions.stream().anyMatch(position -> position.z == 1)) {
                    break;
                }

                for (Position position : brickToFall.positions) {
                    Position positionAfterFall = new Position(position.x, position.y, position.z - 1);
                    // check other bricks
                    for (int j = 0; j < i; j++) {
                        Brick brickToCheck = bricks.get(j);
                        if (brickToCheck.positions.contains(positionAfterFall)) {
                            blocked = true;
                            brickToCheck.supportedBricks.add(brickToFall);
                            brickToFall.supportedByBricks.add(brickToCheck);
                        }
                    }
                }
                if (blocked) {
                    continue;
                }

                brickToFall.positions.replaceAll(position -> new Position(position.x, position.y, position.z - 1));
            }
        }
    }


    private boolean isSafeToDisintegrate(Brick brick) {
        return brick.supportedBricks.isEmpty() ||
                brick.supportedBricks.stream().allMatch(supportedBrick -> supportedBrick.supportedByBricks.size() > 1);
    }

    private Set<Brick> getBricksToFallWhenDisintegratedBFS(Brick startingBrick) {
        startingBrick.markedToFall = true;

        Set<Brick> allBricksToFall = new HashSet<>();
        Queue<Brick> bricksQueue = new LinkedList<>();
        bricksQueue.add(startingBrick);

        while (!bricksQueue.isEmpty()) {
            Brick currentBrick = bricksQueue.poll();

            for (Brick supportedBrick : currentBrick.supportedBricks) {
                if (willFall(supportedBrick)) {
                    supportedBrick.markedToFall = true;
                    bricksQueue.add(supportedBrick);
                    allBricksToFall.add(supportedBrick);
                }
            }
        }

        startingBrick.markedToFall = false;
        allBricksToFall.forEach(brick -> brick.markedToFall = false);
        return allBricksToFall;
    }

    private boolean willFall(Brick supportedBrick) {
        return supportedBrick.supportedByBricks.stream().allMatch(brick -> brick.markedToFall);
    }

    public static void main(String[] args) throws IOException {
        Solution solution = new Solution();

        List<String> lines = Files.readAllLines(Paths.get("inputs/day22.txt"));
//        System.out.println(solution.getSolution(lines));
        System.out.println(solution.getSolution2(lines));
    }
}
