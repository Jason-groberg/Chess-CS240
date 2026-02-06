package chess;

import java.util.Collection;

public class Castle {

    

    public boolean checkCastle(ChessPosition kingPos, ChessBoard board, Collection<ChessMove> kingMoves, Collection<ChessMove> rookMoves){
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;
        ChessPiece king = board.getPiece(kingPos);
        ChessGame.TeamColor kingColor = king.getTeamColor();
        ChessPiece.PieceType rook = ChessPiece.PieceType.ROOK;
        ChessPosition whiteKingStart = new ChessPosition(1,5);
        ChessPosition whiteRook2Start = new ChessPosition(1,8);
        ChessPosition whiteKnight2Start = new ChessPosition(1,7);
        ChessPosition whiteBishop2Start = new ChessPosition(1,6);
        if(board.getPiece(whiteKingStart).equals(king) && board.getPiece(whiteRook2Start).getPieceType() == rook
                && board.getPiece(whiteKnight2Start)==null && board.getPiece(whiteBishop2Start)==null){
            ChessMove rookMove = new ChessMove(whiteRook2Start, whiteBishop2Start, null);
            ChessMove kingMove = new ChessMove(kingPos,whiteKnight2Start,null );
            rookMoves.add(rookMove);
            kingMoves.add(kingMove);
            return true;
        }
        return false;
    }

}
