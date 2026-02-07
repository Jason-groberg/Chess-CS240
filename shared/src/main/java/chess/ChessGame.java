package chess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard currentBoard = new ChessBoard();
    private boolean isInCheck;
    private boolean isInCheckmate;
    private boolean isInStalemate;
    private TeamColor currentTurn;
    Collection<ChessMove> gameMoves;
    private Castle canCastle;

    public ChessGame() {
        this.currentTurn = TeamColor.WHITE;
        this.currentBoard.resetBoard();
        this.isInStalemate = false;
        this.isInCheckmate = false;
        this.isInCheck = false;
        this.gameMoves = new ArrayList<>();
        this.canCastle = new Castle(gameMoves, currentBoard);
    }

    public Collection<ChessMove> getGameMoves() {
        return gameMoves;
    }

    public void setGameMoves(Collection<ChessMove> gameMoves) {
        this.gameMoves = gameMoves;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    public void changeTurn(){
        if(getTeamTurn() == TeamColor.WHITE){
            this.currentTurn = TeamColor.BLACK;
        }
        else {
            this.currentTurn = TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public ChessBoard copy() {
        ChessBoard clone = new ChessBoard();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = currentBoard.getPiece(pos);
                if (piece != null) {
                    clone.addPiece(pos, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return clone;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = currentBoard.getPiece(startPosition);
        if(piece.getPieceType() == null){return List.of();}
        canCastle.setGameBoard(currentBoard);
        Collection<ChessMove> allMoves = piece.pieceMoves(currentBoard, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();
        for(ChessMove move : allMoves){
            ChessBoard nextBoard = copy();
            testMove(move, nextBoard);
            if(!kingInCheck(piece.getTeamColor(), nextBoard)){
                legalMoves.add(move);
            }
        }
        if(piece.getPieceType() == ChessPiece.PieceType.KING && (startPosition.getRow()==1 || startPosition.getRow() ==8)){
            checkCastle(piece.getTeamColor(), currentBoard, legalMoves, startPosition);
        }
        return legalMoves;
    }

    public void checkCastle(ChessGame.TeamColor color, ChessBoard board, Collection<ChessMove> legalMoves, ChessPosition kingPos){
        if(kingInCheck(color, board)){return;}
        int col = kingPos.getColumn();
        if(canCastle.canCastleKingSide(color)){
            if(col+2 <9){
            ChessPosition rightSquare = new ChessPosition(kingPos.getRow(), kingPos.getColumn()+1);
            ChessBoard clone = copy();
            testMove(new ChessMove(kingPos, rightSquare,null),clone);
            if(kingInCheck(color,clone)){return;}
            ChessPosition nextRightSquare = new ChessPosition(kingPos.getRow(), kingPos.getColumn()+2);
            testMove(new ChessMove(rightSquare, nextRightSquare, null), clone);
            if(!kingInCheck(color, clone)) {
                ChessMove kingSideCastle = new ChessMove(kingPos, nextRightSquare, null);
                legalMoves.add(kingSideCastle);
                }
            }
        }
        if(canCastle.canCastleQueenSide(color)){
            if(col-2 > 0) {
                ChessPosition leftSquare = new ChessPosition(kingPos.getRow(), kingPos.getColumn() - 1);
                ChessBoard clone = copy();
                testMove(new ChessMove(kingPos, leftSquare, null), clone);
                if (kingInCheck(color, clone)) {
                    return;
                }
                ChessPosition nextLeftSquare = new ChessPosition(kingPos.getRow(), kingPos.getColumn() - 2);
                testMove(new ChessMove(leftSquare, nextLeftSquare, null), clone);
                if (kingInCheck(color, clone)) {
                    return;
                }
                ChessMove queenSideCastle = new ChessMove(kingPos, nextLeftSquare, null);
                legalMoves.add(queenSideCastle);
            }
        }
    }

    public void testMove(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece != null) {
            if (move.getPromotionPiece() != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            } else {
                board.addPiece(move.getEndPosition(), piece);
            }
            board.removePiece(move.getStartPosition());
        }
    }

    /**
     * Makes a move in a chess game
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        canCastle.setGameBoard(this.currentBoard);
        ChessPiece piece = currentBoard.getPiece(move.getStartPosition());
        if (piece == null) {
                throw new InvalidMoveException("No piece at position: " + move.getStartPosition());
            }
        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("not your turn, current game turn is " + currentTurn);
        }
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (legalMoves.isEmpty() || !legalMoves.contains(move)) {
            throw new InvalidMoveException("Requested Move is Illegal for Piece type: " + piece.getPieceType());
        }
        if (move.getPromotionPiece() == null) {
            currentBoard.addPiece(move.getEndPosition(), piece);
        } else {
            ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            currentBoard.addPiece(move.getEndPosition(), promotionPiece);
        }
        currentBoard.removePiece(move.getStartPosition());

        if(piece.getPieceType() == ChessPiece.PieceType.KING &&
                Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn())==2) {
            makeRookMove(move);
        }
        gameMoves.add(move);
        canCastle.updateMoves(move);
        changeTurn();
    }

    public void makeRookMove(ChessMove move){
        int currRow = move.getEndPosition().getRow();
        int currCol = move.getEndPosition().getColumn();
        //king side castle
        if(currCol - move.getStartPosition().getColumn() > 0){
            currentBoard.removePiece(new ChessPosition(currRow, 8));
            if(currRow == 1){
                currentBoard.addPiece(new ChessPosition (currRow, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
            }
            else if(currRow == 8) {
                currentBoard.addPiece(new ChessPosition (currRow, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
            }
        }
        //queen side castle
        else {
            currentBoard.removePiece(new ChessPosition(currRow, 1));
            if(currRow==1) {
                currentBoard.addPiece(new ChessPosition (currRow, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
            }
            else if(currRow==8){
                currentBoard.addPiece(new ChessPosition (currRow, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return kingInCheck(teamColor, currentBoard);
    }

    public boolean kingInCheck(TeamColor kingColor, ChessBoard board){
        ChessPosition kingPos = findKing(kingColor, board);
        for(int row=1;row<9;row++){
            for(int col=1;col<9;col++) {
                ChessPosition target = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(target);
                if (piece == null || piece.getTeamColor() == kingColor) {
                    continue;
                }
                Collection<ChessMove> enemyMoves = piece.pieceMoves(board, target);
                for(ChessMove move : enemyMoves){
                    if(move.getEndPosition().equals(kingPos)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ChessPosition findKing(TeamColor color, ChessBoard board){
        for(int row=1; row <9;row++){
            for(int col=1;col <9; col++){
                ChessPosition target = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(target);
                if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color){
                    return target;
                }
            }
        }
        throw new RuntimeException("King Not Found");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!kingInCheck(teamColor, currentBoard)){
            return false;
        }
        return hasNoLegalMoves(teamColor);
    }

    public boolean hasNoLegalMoves(TeamColor teamColor){
        Collection<ChessMove> savingMoves = new ArrayList<>();
        for(int row=1;row<9;row++){
            for(int col =1;col<9;col++){
                ChessPosition target = new ChessPosition(row,col);
                ChessPiece targetPiece = currentBoard.getPiece(target);
                if(targetPiece != null && targetPiece.getTeamColor() == teamColor){
                    Collection<ChessMove> legalMoves = validMoves(target);
                    savingMoves.addAll(legalMoves);
                }
            }
        }
        return savingMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        return hasNoLegalMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return isInCheck == chessGame.isInCheck && isInCheckmate == chessGame.isInCheckmate && isInStalemate == chessGame.isInStalemate
                && Objects.equals(currentBoard, chessGame.currentBoard) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentBoard, isInCheck, isInCheckmate, isInStalemate, currentTurn);
    }
}
