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

    private ChessBoard current_board = new ChessBoard();
    private boolean isInCheck;
    private boolean isInCheckmate;
    private boolean isInStalemate;
    private TeamColor current_turn;

    public ChessGame() {
        this.current_turn = TeamColor.WHITE;
        this.isInStalemate = false;
        this.isInCheckmate = false;
        this.isInCheck = false;
        this.current_board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {return current_turn;
    }


    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.current_turn = team;
    }

    public void change_turn(){
        if(getTeamTurn() == TeamColor.WHITE){
            this.current_turn = TeamColor.BLACK;
        }
        else {
            this.current_turn = TeamColor.WHITE;
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
        for(int row = 0;row <9;row++){
            for(int col =0; col<9;col++){
                ChessPosition pos = new ChessPosition(row, col);
                clone.addPiece(pos, current_board.getPiece(pos));
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
        ChessPiece piece = current_board.getPiece(startPosition);
        Collection<ChessMove> all_moves = piece.pieceMoves(current_board, startPosition);
        Collection<ChessMove> legal_moves = new ArrayList<>();
        for(ChessMove move : all_moves){
            ChessBoard next_board = copy();
            testMove(move, next_board);
            if(!isInCheck(piece.getTeamColor())){
                ChessMove legal_move = new ChessMove(startPosition, move.getEndPosition(), null);
                legal_moves.add(legal_move);

            }
        }
        return List.of();
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
        try {
            ChessPiece piece = current_board.getPiece(move.getStartPosition());
            current_board.addPiece(move.getEndPosition(), piece);
            current_board.removePiece(move.getStartPosition());
        }
        catch (InvalidMoveException ){

            throw new InvalidMoveException("Requested move is not legal");
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {


        return isInCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheckmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return isInStalemate;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.current_board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return current_board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return isInCheck == chessGame.isInCheck && isInCheckmate == chessGame.isInCheckmate
                && isInStalemate == chessGame.isInStalemate && Objects.equals(current_board, chessGame.current_board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(current_board, isInCheck, isInCheckmate, isInStalemate);
    }
}
