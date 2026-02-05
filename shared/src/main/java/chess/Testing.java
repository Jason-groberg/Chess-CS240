package chess;
import java.util.Scanner;


public class Testing {

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        System.out.println(board);
    }
}
