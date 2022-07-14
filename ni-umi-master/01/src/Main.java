import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static int mazeHeight;
    private static int mazeWidth;

    private static List<Position> orderRubbish(final Position startingPos, List<Position> rubbishPositions) {
        List<Position> orderedRubbishPositions = new ArrayList<>();

        Position currPos = startingPos;
        while(orderedRubbishPositions.size() != rubbishPositions.size()) {
            int manhattanDistToClosestPos = Integer.MAX_VALUE;
            int indexOfClosestPos = -1;
            for (int i = 0 ; i < rubbishPositions.size() ; i++) {
                if (manhattanDistToClosestPos > currPos.distanceTo(rubbishPositions.get(i))
                        && !rubbishPositions.get(i).isChecked()) {
                    manhattanDistToClosestPos = currPos.distanceTo(rubbishPositions.get(i));
                    indexOfClosestPos = i;
                }
            }

            rubbishPositions.get(indexOfClosestPos).setChecked(true);
            orderedRubbishPositions.add(rubbishPositions.get(indexOfClosestPos));
            currPos = rubbishPositions.get(indexOfClosestPos);
        }
        return orderedRubbishPositions;
    }

    static void printMaze(final char[][] maze) {
        for (int row = 0 ; row < mazeHeight ; row++) {
            for (int col = 0 ; col < mazeWidth ; col++) {
                System.out.print(maze[col][row]);
            }
            System.out.println();
        }
    }

    private static void printLegend() {
        System.out.println("------------------------------------");
        System.out.println("LEGEND:");
        System.out.println("^ for up");
        System.out.println("_ for down");
        System.out.println("> for right");
        System.out.println("< for left");
        System.out.println("------------------------------------");
    }

    public static void main (String[] args) {
        System.out.println("Insert maze, starting position and rubbish positions, position numbers can be divided by any divider.");
        System.out.println("Coordinate are expected in order: col, row");
        // load maze
        List<String> inputLines = new ArrayList<>();
        Scanner input = new Scanner(System.in);
        String line = "";
        while (input.hasNext()) {
            line = input.nextLine();
            if (line.startsWith("start")) {
                break;
            }
            inputLines.add(line);
        }

        // load starting coordinates
        String positions = line;
        positions += input.nextLine();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(positions);
        List<Integer> coordinates = new ArrayList<>();
        while(m.find()) {
            coordinates.add(Integer.valueOf(m.group()));
        }
        final Position startingPos = new Position(coordinates.get(0), coordinates.get(1));
        final int startingCol = coordinates.get(0);
        final int startingRow = coordinates.get(1);
        // load rubbish positions
        if (coordinates.size() < 4 || coordinates.size()%2 != 0) {
            System.out.println("WRONG INPUT! EXPECTED AT LEAST 1 RUBBISH AND TO OBTAIN EVEN NUMBER OF COORDINATES. Number of coordinates received: " + coordinates.size());
            return;
        }
        List<Position> rubbishPositions = new ArrayList<>();
        for (int i = 2 ; i < coordinates.size() ; i = i+2) {
            rubbishPositions.add(new Position(coordinates.get(i), coordinates.get(i+1)));
        }
        List<Position> orderedRubbishPositions = orderRubbish(startingPos, rubbishPositions);

        mazeHeight = inputLines.size();
        mazeWidth = inputLines.get(0).length();
        char[][] maze = new char[mazeWidth][mazeHeight];
        for (int lineNmb = 0 ; lineNmb < inputLines.size() ; lineNmb++ ) {
            for (int colNmb = 0 ; colNmb < inputLines.get(lineNmb).length() ; colNmb++) {
                maze[colNmb][lineNmb] = inputLines.get(lineNmb).charAt(colNmb);
            }
        }
        maze[startingCol][startingRow] = 'S';
//        printMaze(maze);

        Algorithm algo = new Algorithm(mazeWidth, mazeHeight, startingCol, startingRow);
        // start algo
        Position targetPos = orderedRubbishPositions.remove(0);
        final String path = algo.aStarRec(maze, targetPos, orderedRubbishPositions, 0, new Position(startingCol, startingRow));

        System.out.println("Found path: " + path);
        printLegend();
    }
}
