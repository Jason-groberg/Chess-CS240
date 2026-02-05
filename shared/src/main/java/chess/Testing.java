package chess;
import java.util.Scanner;


public class Testing {

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.setTeamTurn(ChessGame.TeamColor.WHITE);
        ChessBoard board = game.getBoard();
        System.out.println(board);
        ChessPosition e2 = new ChessPosition(2,2);
        ChessPosition e4 = new ChessPosition(4,2);
    }
}
