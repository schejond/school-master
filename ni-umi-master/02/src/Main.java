import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static void printBoard(final int[][] board) {
        System.out.println("-------------");
        for (int[] row : board) {
            System.out.print("|");
            for (int col = 0; col < row.length; col++) {
                System.out.print(row[col]);
                if ((col + 1) % 3 == 0) {
                    System.out.print("|");
                }
            }
            System.out.println();
            System.out.println("-------------");
        }
    }

    private static int[][] deepCopyBoard(final int[][] board) {
        int[][] newBoard = new int[9][9];
        for (int row = 0 ; row < board.length ; row++) {
            System.arraycopy(board[row], 0, newBoard[row], 0, board[row].length);
        }
        return newBoard;
    }

    public static void main(String[] args) {
        System.out.println("Zadejte sudoku. Prazdne pozice musi byt reprezentovany 0 (Bile znaky mezi jednotlivymi cisly jsou povoleny):");
        // load sudoku board
        List<String> inputLines = new ArrayList<>();
        Scanner input = new Scanner(System.in);
        String line = "";
        while (inputLines.size() < 9 && input.hasNext()) {
            line = input.nextLine().replaceAll("\\s+", "");
            inputLines.add(line);
        }

        // save loaded board
        int[][] board = new int[9][9];
        for (int row = 0; row < inputLines.size(); row++) {
            for (int col = 0; col < inputLines.get(row).length(); col++) {
                board[row][col] = inputLines.get(row).charAt(col) - '0';
            }
        }
//        System.out.println("Zadano:");
//        printBoard(board);

        Backtracking bt = new Backtracking();
        // BT
        int[][] boardCopy = deepCopyBoard(board);
        bt.restartIterationCounter();
        int[][] resultBoard = bt.simpleBacktracking(boardCopy, 0, 0);
        if (resultBoard == null) {
            System.out.println("Neexistuje reseni pro zadanou sudoku tabulku.");
            return;
        }
        System.out.println("Reseni existuje");

        System.out.print("BT - ");
        bt.printIterationCount();
//        System.out.println("Nalezene reseni BT:");
//        printBoard(resultBoard);

        // BJ
        boardCopy = deepCopyBoard(board);
        bt.restartIterationCounter();
        resultBoard = bt.backJumping(boardCopy, 0, 0).getBoard();
        if (resultBoard == null) {
            System.out.println("null");
            return;
        }
        System.out.print("BJ - ");
        bt.printIterationCount();
        System.out.println("Nalezene reseni BJ:");
        printBoard(resultBoard);
    }
}
