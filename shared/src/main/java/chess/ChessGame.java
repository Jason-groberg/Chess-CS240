package chess;
import java.util.ArrayList;
import java.util.Collection;
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
    private boolean blackHasCastled;
    private boolean whiteHasCastled;


    public ChessGame() {
        this.currentTurn = TeamColor.WHITE;
        this.currentBoard.resetBoard();
        this.isInStalemate = false;
        this.isInCheckmate = false;
        this.isInCheck = false;
        this.whiteHasCastled = false;
        this.blackHasCastled = false;
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

    public ChessBoard copy(){
        ChessBoard clone = new ChessBoard();
        for(int row = 1;row <9;row++){
            for(int col =1; col<9;col++){
                ChessPosition pos = new ChessPosition(row, col);
                clone.addPiece(pos, currentBoard.getPiece(pos));
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
        Collection<ChessMove> allMoves = piece.pieceMoves(currentBoard, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();
        for(ChessMove move : allMoves){
            ChessBoard nextBoard = copy();
            testMove(move, nextBoard);
            if(!kingInCheck(piece.getTeamColor(), nextBoard)){
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    public void testMove(ChessMove move, ChessBoard board){
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.removePiece(move.getStartPosition());
    }

    /**
     * Makes a move in a chess game
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> gameMoves = new ArrayList<>();
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
            gameMoves.add(move);
            changeTurn();
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
