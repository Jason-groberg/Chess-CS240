package chess;

import java.util.Collection;

public class Castle {
    private Collection<ChessMove> gameMoves;
    private boolean whiteKingMoved;
    private boolean whiteRook1Moved;
    private boolean whiteRook2Moved;
    private boolean blackKingMoved;
    private boolean blackRook1Moved;
    private boolean blackRook2Moved;
    private ChessBoard gameBoard;

    public Castle(Collection<ChessMove> gameMoves, ChessBoard gameBoard){
        this.gameMoves = gameMoves;
        this.gameBoard = gameBoard;
        resetBools();

    }

    private void resetBools() {
        this.whiteRook1Moved = false;
        this.whiteRook2Moved = false;
        this.whiteKingMoved = false;
        this.blackKingMoved = false;
        this.blackRook1Moved = false;
        this.blackRook2Moved = false;
    }

    public boolean canCastleKingSide(ChessGame.TeamColor teamColor){
        if(teamColor == ChessGame.TeamColor.WHITE){
            if(!whiteKingMoved&&!whiteRook2Moved){
                return gameBoard.getPiece(new ChessPosition(1,6))==null && gameBoard.getPiece(new ChessPosition(1,7))==null;
            }
        }
        else {
            if(!blackKingMoved&&!blackRook2Moved){
                return gameBoard.getPiece(new ChessPosition(8,6))==null && gameBoard.getPiece(new ChessPosition(8,7))==null;
            }
        }
        return false;
    }

    public boolean canCastleQueenSide(ChessGame.TeamColor teamColor){
        if(teamColor == ChessGame.TeamColor.WHITE){
            if(!whiteKingMoved&&!whiteRook1Moved){
                return gameBoard.getPiece(new ChessPosition(1,4))==null && gameBoard.getPiece(new ChessPosition(1,3))==null
                        && gameBoard.getPiece(new ChessPosition(1,2))==null;
            }
        }
        else {
            if(!blackKingMoved&&!blackRook1Moved){
                return gameBoard.getPiece(new ChessPosition(8,4))==null && gameBoard.getPiece(new ChessPosition(8,3))==null
                        && gameBoard.getPiece(new ChessPosition(8,2))==null;
            }
        }
        return false;
    }

    public void updateMoves() {
        for (ChessMove move : gameMoves) {
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            checkMoved(start);
            checkCaptured(end);
        }
    }

    private void checkMoved(ChessPosition position) {
        if (position.equals(new ChessPosition(1, 5))) {
            whiteKingMoved = true;
        }
        if (position.equals(new ChessPosition(1, 8))) {
            whiteRook2Moved = true;
        }
        if (position.equals(new ChessPosition(1, 1))) {
            whiteRook1Moved = true;
        }
        if (position.equals(new ChessPosition(8, 5))) {
            blackKingMoved = true;
        }
        if (position.equals(new ChessPosition(8, 8))) {
            blackRook2Moved = true;
        }
        if (position.equals(new ChessPosition(8, 1))){
            blackRook1Moved = true;
            }
    }

    private void checkCaptured(ChessPosition pos) {
        if (pos.equals(new ChessPosition(1, 8))) {
            whiteRook2Moved = true;
        }
        if (pos.equals(new ChessPosition(1, 1))){
            whiteRook1Moved = true;}
        if (pos.equals(new ChessPosition(8, 8))) {
            blackRook2Moved = true;}
        if (pos.equals(new ChessPosition(8, 1))){
            blackRook1Moved = true;}
    }

    public ChessBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(ChessBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public Collection<ChessMove> getGameMoves() {
        return gameMoves;
    }

    public void setGameMoves(Collection<ChessMove> gameMoves) {
        this.gameMoves = gameMoves;
    }
}
