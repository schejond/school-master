import java.util.HashSet;
import java.util.Set;

public class BoardConflictPair {
    private int[][] board;
    Set<Position> conflicts = new HashSet<>();

    public BoardConflictPair(int[][] board, Set<Position> conflicts) {
        this.board = board;
        this.conflicts = conflicts;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public Set<Position> getConflicts() {
        return conflicts;
    }

    public void setConflicts(Set<Position> conflicts) {
        this.conflicts = conflicts;
    }
}
