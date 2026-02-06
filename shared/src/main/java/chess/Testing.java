package chess;
import java.util.Collection;
import java.util.Scanner;


public class Testing {

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        Collection<ChessMove> gameMoves = game.getGameMoves();
        ChessPosition kingPos = game.findKing(ChessGame.TeamColor.WHITE, board);
        Castle castle = new Castle(gameMoves, board);
        System.out.println(castle.toString());
    }
}
