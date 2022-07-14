import java.util.*;

public class Backtracking {

    private final static int BOARD_DIMENSION = 9;

    public Backtracking() {
    }

    // iterationCounter for comparison of
    private int iterationCounter = 0;

    public void restartIterationCounter() {
        this.iterationCounter = 0;
    }

    public void printIterationCount() {
        System.out.println("Celkovy pocet iteraci: " + this.iterationCounter);
    }

    private void incrementIterationCounter() {
        this.iterationCounter += 1;
    }

    // all different methods
    private boolean rowOkForValue(final int[][] board, final int row, final int val) {
        for (int col = 0; col < BOARD_DIMENSION; col++)
            if (board[row][col] == val) {
                return false;
            }
        return true;
    }

    private boolean colOkForValue(final int[][] board, final int col, final int val) {
        for (int row = 0; row < BOARD_DIMENSION; row++) {
            if (board[row][col] == val) {
                return false;
            }
        }
        return true;
    }

    private boolean boxOkForValue(final int[][] board, final int row, final int col, final int val) {
        final int boxStartRow = row - row % 3;
        final int boxStartCol = col - col % 3;

        for (int i = 0 ; i < 3 ; i++) {
            for (int j = 0 ; j < 3 ; j++) {
                if (board[boxStartRow + i][boxStartCol + j] == val) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean allDifferent(final int[][] board, final int row, final int col, final int newValue) {
        return rowOkForValue(board, row, newValue) && colOkForValue(board, col, newValue) && boxOkForValue(board, row, col, newValue);
    }

    private boolean isBoardFilled(final int[][] board) {
        for (int row = 0 ; row < BOARD_DIMENSION ; row++) {
            for (int col = 0 ; col < BOARD_DIMENSION ; col++) {
                if (board[row][col] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] simpleBacktracking(int[][] board, final int row, final int col) {
        this.incrementIterationCounter();
        if (row >= BOARD_DIMENSION) {
            if (this.isBoardFilled(board)) {
                return board;
            }
            return null;
        }

        // if the value is filled, move to next position
        if (col >= BOARD_DIMENSION || board[row][col] != 0) {
            if (col + 1 < BOARD_DIMENSION) {
                return simpleBacktracking(board, row, col + 1);
            } else {
                return simpleBacktracking(board, row + 1, 0);
            }
        }

        // search for a valid number
        for (int val = 1; val <= BOARD_DIMENSION; val++) {
            if (this.allDifferent(board, row, col, val)) {
                board[row][col] = val;

                int[][] possibleSolution;
                if (col + 1 < BOARD_DIMENSION) {
                    possibleSolution = simpleBacktracking(board, row, col + 1);
                } else {
                    possibleSolution = simpleBacktracking(board, row + 1, 0);
                }

                if (possibleSolution != null) {
                    return possibleSolution;
                }

                // no valid solution was found
                board[row][col] = 0;
            }
        }

        return null;
    }

    // basically same as allDifferent but returns conflicting positions
    private Set<Position> findConflicts(final int[][] board, final int row, final int col, final int newValue) {
        Set<Position> foundConflicts = new HashSet<>();
        // find conflicts in row
        for (int c = 0 ; c < BOARD_DIMENSION ; c++) {
            if (board[row][c] == newValue) {
                foundConflicts.add(new Position(row, c));
            }
        }
        // find conflicts in col
        for (int r = 0 ; r < BOARD_DIMENSION ; r++) {
            if (board[r][col] == newValue) {
                foundConflicts.add(new Position(r, col));
            }
        }

        // check conflicts in box
        final int boxStartRow = row - row % 3;
        final int boxStartCol = col - col % 3;

        for (int r = 0 ; r < 3 ; r++) {
            for (int c = 0 ; c < 3 ; c++) {
                if (board[boxStartRow + r][boxStartCol + c] == newValue) {
                    foundConflicts.add(new Position(boxStartRow + r, boxStartCol + c));
                }
            }
        }

        foundConflicts.add(new Position(row, col));
        return foundConflicts;
    }

    public BoardConflictPair backJumping(int[][] board, final int row, final int col) {
        this.incrementIterationCounter();
        if (row >= BOARD_DIMENSION) {
            if (this.isBoardFilled(board)) {
                return new BoardConflictPair(board, new HashSet<>());
            }
            return new BoardConflictPair(null, new HashSet<>());
        }

        Set<Position> conflicts = new HashSet<>();

        // if the value is filled, move to next position
        if (col >= BOARD_DIMENSION || board[row][col] != 0) {
            if (col + 1 < BOARD_DIMENSION) {
                return backJumping(board, row, col + 1);
            } else {
                return backJumping(board, row + 1, 0);
            }
        }

        for (int val = 1; val <= BOARD_DIMENSION; val++) {
            // obtain set of all conflicts (all positions which are relevant and == val)
            Set<Position> newConflicts = findConflicts(board, row, col, val);

            BoardConflictPair possibleSolution = new BoardConflictPair(null,new HashSet<>());;
            if (newConflicts.size() == 1) {
                board[row][col] = val;
                if (col + 1 < BOARD_DIMENSION) {
                    possibleSolution = backJumping(board, row, col + 1);
                } else {
                    possibleSolution = backJumping(board, row + 1, 0);
                }
                newConflicts = possibleSolution.getConflicts();
            }
            if (possibleSolution.getBoard() != null) {
                return new BoardConflictPair(possibleSolution.getBoard(), new HashSet<>());
            }
            board[row][col] = 0;
            if (!newConflicts.contains(new Position(row, col))) {
                return new BoardConflictPair(null, newConflicts);
            }

            newConflicts.remove(new Position(row, col));
            conflicts.addAll(newConflicts);
        }
        return new BoardConflictPair(null, conflicts);
    }
}
