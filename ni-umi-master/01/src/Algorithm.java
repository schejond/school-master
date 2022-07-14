import java.util.*;

class Algorithm {
    private final int mazeHeight;
    private final int mazeWidth;
    private final int startCol;
    private final int startRow;

    Algorithm(final int mazeWidth, final int mazeHeight, final int startCol, final int startRow) {
        this.mazeHeight = mazeHeight;
        this.mazeWidth = mazeWidth;
        this.startCol = startCol;
        this.startRow = startRow;
    }

    String aStarRec(char[][] maze, final Position targetPos, List<Position> rubbishPositions,
                    final int distanceToStart, final Position startingPos) {
        if (targetPos == null) {
            return "";
        }

        PriorityQueue<PositionInMaze> openPositions = new PriorityQueue<>(new AStarComparator());

        PositionInMaze startingPosition = new PositionInMaze(startingPos.getX(), startingPos.getY(), targetPos);
        Algorithm.PositionInMaze[][] visitedPositionsMaze = new Algorithm.PositionInMaze[mazeWidth][mazeHeight];
        startingPosition.setDistanceFromStart(distanceToStart);
        visitedPositionsMaze[startingPosition.getX()][startingPosition.getY()] = startingPosition;
        openPositions.add(startingPosition);

        int col;
        int row;
        PositionInMaze currentPosition;

        String path = "";
        while (!openPositions.isEmpty()) {
            currentPosition = openPositions.poll();

            col = currentPosition.getX();
            row = currentPosition.getY();

            // check place to the right
            if (isPositionEmpty(maze, col + 1, row) || maze[col + 1][row] == 'O' && visitedPositionsMaze[col + 1][row] == null) {
                PositionInMaze nextPosition = new PositionInMaze(col + 1, row, targetPos);
                nextPosition.setAncestorsCoordinates(col, row);
                nextPosition.setDistanceFromStart(currentPosition.getDistanceFromStart() + 1 + distanceToStart);
                nextPosition.setDirectionArrow(" " + (char)62);
                visitedPositionsMaze[col + 1][row] = nextPosition;

                if (col + 1 == targetPos.getX() && row == targetPos.getY()) {
                    path = getAncestorsPath(maze, visitedPositionsMaze, nextPosition);
                    maze[col + 1][row] = 'R';
                    Position newTarget = rubbishPositions.isEmpty() ? null : rubbishPositions.remove(0);
                    return path + aStarRec(maze, newTarget, rubbishPositions, currentPosition.getDistanceFromStart() + 1, new Position(col + 1, row));
                }

                openPositions.add(nextPosition);
                maze[col + 1][row] = 'O';
            } else if (maze[col + 1][row] == 'O') {
                if (visitedPositionsMaze[col + 1][row].getDistanceFromStart() + visitedPositionsMaze[col + 1][row].getHeuristicValue() > (currentPosition.getDistanceFromStart() + 1 + distanceToStart + currentPosition.getHeuristicValue())) {
                    visitedPositionsMaze[col + 1][row].setAncestorsCoordinates(col, row);
                    visitedPositionsMaze[col + 1][row].setDirectionArrow(" " + (char) 62);
                }
            }

            // check place to the left
            if (isPositionEmpty(maze, col - 1, row) || maze[col - 1][row] == 'O' && visitedPositionsMaze[col - 1][row] == null) {
                PositionInMaze nextPosition = new PositionInMaze(col - 1, row, targetPos);
                nextPosition.setAncestorsCoordinates(col, row);
                nextPosition.setDistanceFromStart(currentPosition.getDistanceFromStart() + 1 + distanceToStart);
                nextPosition.setDirectionArrow(" " + (char)60);
                visitedPositionsMaze[col - 1][row] = nextPosition;

                if (col - 1 == targetPos.getX() && row == targetPos.getY()) {
                    path = getAncestorsPath(maze, visitedPositionsMaze, nextPosition);
                    maze[col - 1][row] = 'R';
                    Position newTarget = rubbishPositions.isEmpty() ? null : rubbishPositions.remove(0);
                    return path + aStarRec(maze, newTarget, rubbishPositions, currentPosition.getDistanceFromStart() + 1, new Position(col - 1, row));
                }

                openPositions.add(nextPosition);
                maze[col - 1][row] = 'O';
            } else if (maze[col - 1][row] == 'O') {
                if (visitedPositionsMaze[col - 1][row].getDistanceFromStart() + visitedPositionsMaze[col - 1][row].getHeuristicValue() > (currentPosition.getDistanceFromStart() + 1 + distanceToStart + currentPosition.getHeuristicValue())) {
                    visitedPositionsMaze[col - 1][row].setAncestorsCoordinates(col, row);
                    visitedPositionsMaze[col - 1][row].setDirectionArrow(" " + (char) 60);
                }
            }

            //check place down
            if (isPositionEmpty(maze, col, row + 1) || maze[col][row + 1] == 'O' && visitedPositionsMaze[col][row + 1] == null) {
                PositionInMaze nextPosition = new PositionInMaze(col, row + 1, targetPos);
                nextPosition.setAncestorsCoordinates(col, row);
                nextPosition.setDistanceFromStart(currentPosition.getDistanceFromStart() + 1 + distanceToStart);
                nextPosition.setDirectionArrow(" " + (char)95);
                visitedPositionsMaze[col][row + 1] = nextPosition;

                if (col == targetPos.getX() && row + 1 == targetPos.getY()) {
                    path = getAncestorsPath(maze, visitedPositionsMaze, nextPosition);
                    maze[col][row + 1] = 'R';
                    Position newTarget = rubbishPositions.isEmpty() ? null : rubbishPositions.remove(0);
                    return path + aStarRec(maze, newTarget, rubbishPositions, currentPosition.getDistanceFromStart() + 1, new Position(col, row + 1));
                }

                openPositions.add(nextPosition);
                maze[col][row + 1] = 'O';
            } else if (maze[col][row + 1] == 'O') {
                if (visitedPositionsMaze[col][row + 1].getDistanceFromStart() + visitedPositionsMaze[col][row + 1].getHeuristicValue() > (currentPosition.getDistanceFromStart() + 1 + distanceToStart + currentPosition.getHeuristicValue())) {
                    visitedPositionsMaze[col][row + 1].setAncestorsCoordinates(col, row);
                    visitedPositionsMaze[col][row + 1].setDirectionArrow(" " + (char) 95);
                }
            }

            // check place up
            if (isPositionEmpty(maze, col, row - 1) || maze[col][row - 1] == 'O' && visitedPositionsMaze[col][row - 1] == null) {
                PositionInMaze nextPosition = new PositionInMaze(col, row - 1, targetPos);
                nextPosition.setAncestorsCoordinates(col, row);
                nextPosition.setDistanceFromStart(currentPosition.getDistanceFromStart() + 1 + distanceToStart);
                nextPosition.setDirectionArrow(" " + (char)94);
                visitedPositionsMaze[col][row - 1] = nextPosition;

                if (col == targetPos.getX() && row - 1 == targetPos.getY()) {
                    path = getAncestorsPath(maze, visitedPositionsMaze, nextPosition);
                    maze[col][row - 1] = 'R';
                    Position newTarget = rubbishPositions.isEmpty() ? null : rubbishPositions.remove(0);
                    return path + aStarRec(maze, newTarget, rubbishPositions, currentPosition.getDistanceFromStart() + 1, new Position(col, row - 1));
                }

                openPositions.add(nextPosition);
                maze[col][row - 1] = 'O';
            } else if (maze[col][row - 1] == 'O') {
                if (visitedPositionsMaze[col][row - 1].getDistanceFromStart() + visitedPositionsMaze[col][row - 1].getHeuristicValue() > (currentPosition.getDistanceFromStart() + 1 + distanceToStart + currentPosition.getHeuristicValue())) {
                    visitedPositionsMaze[col][row - 1].setAncestorsCoordinates(col, row);
                    visitedPositionsMaze[col][row - 1].setDirectionArrow(" " + (char) 94);
                }
            }
        }
        return path;
    }

    // also checks if the coordinates are valid
    private boolean isPositionEmpty(char[][] maze, final int col, final int row) {
        return col <= mazeWidth && col >= 0 && row <= mazeHeight && row >= 0
                && maze[col][row] != 'X' && maze[col][row] != 'S' && maze[col][row] != 'O' && maze[col][row] != 'R';
    }

    private String getAncestorsPath(char[][] maze, PositionInMaze[][] positionMaze, PositionInMaze position) {
        if (maze[position.ancestorCol][position.ancestorRow] != 'S' && maze[position.ancestorCol][position.ancestorRow] != 'R'
                && maze[position.ancestorCol][position.ancestorRow] != 'X') {
            PositionInMaze ancestor = positionMaze[position.ancestorCol][position.ancestorRow];
            return getAncestorsPath(maze, positionMaze, ancestor) + position.getDirectionArrow();
        }
        return position.getDirectionArrow();
    }

    private static class AStarComparator implements Comparator<PositionInMaze> {
        @Override
        public int compare(PositionInMaze o1, PositionInMaze o2) {
            final int value1 = o1.getHeuristicValue() + o1.getDistanceFromStart();
            final int value2 = o2.getHeuristicValue() + o2.getDistanceFromStart();
            return Integer.compare(value1, value2);
        }
    }

    public static class PositionInMaze {
        private final int x;
        private final int y;
        private int ancestorCol = 0;
        private int ancestorRow = 0;
        private int distanceFromStart;
        private final int heuristicValue;
        private String directionArrow = "";

        PositionInMaze(final int x, final int y, Position targetPos) {
            this.x = x;
            this.y = y;
            this.distanceFromStart = 0;
            this.heuristicValue = Math.abs(targetPos.getX() - x) + Math.abs(targetPos.getY() - y);
        }

        int getHeuristicValue() {
            return heuristicValue;
        }

        int getDistanceFromStart() {
            return distanceFromStart;
        }

        void setDistanceFromStart(int distanceFromStart) {
            this.distanceFromStart = distanceFromStart;
        }

        public void setDirectionArrow(String directionArrow) {
            this.directionArrow = directionArrow;
        }

        public String getDirectionArrow() {
            return directionArrow;
        }

        void setAncestorsCoordinates(final int col, final int row) {
            this.ancestorCol = col;
            this.ancestorRow = row;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }
    }
}
