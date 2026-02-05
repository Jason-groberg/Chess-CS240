package chess;
import java.util.Scanner;


public class testing {

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        ChessBoard board = new ChessBoard();
        board.addPiece(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        board.addPiece(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        board.addPiece(new ChessPosition(7,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        board.addPiece(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        board.addPiece(new ChessPosition(7,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        board.addPiece(new ChessPosition(6,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        board.addPiece(new ChessPosition(5,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        game.setBoard(board);
        game.setTeamTurn(ChessGame.TeamColor.BLACK);
        game.isInCheckmate(ChessGame.TeamColor.BLACK);
        System.out.println(board);



    }
}




//        Scanner scanner = new Scanner(System.in);
//        while (!game.isInCheckmate(game.getTeamTurn()) && !game.isInStalemate(game.getTeamTurn())) {
//            System.out.println(game.getBoard());
//            System.out.println(game.getTeamTurn() + "'s turn. Enter a move: \b");
//            String move = scanner.nextLine();
//            if(move.equalsIgnoreCase("q")){
//                break;
//            }
//        }
//        scanner.close();
