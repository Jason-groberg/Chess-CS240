package chess;
import java.util.Collection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class LegalMoves {

    public enum moves {
        //white POV
        RIGHT(1, 0), LEFT(-1, 0), DOWN(0, -1), UP(0, 1),
        UP_RIGHT(1, 1), UP_LEFT(-1, 1), DOWN_LEFT(-1, -1), DOWN_RIGHT(1, -1),
        //Knight Moves
        U_U_R(1, 2), U_U_L(-1, 2), D_D_L(-1, -2), D_D_R(1, -2);

        private final int dx, dy;

        moves(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public int getDX() {
            return dx;
        }

        public int getDY() {return dy;}
    }

    public static boolean OutOfBounds(ChessPosition pos) {
        return pos.getColumn() <= 8 && pos.getColumn() >= 0
                && pos.getRow() <= 8 && pos.getRow() >= 0;
    }
    // returns a list of all legal moves given piece and position
    public static Collection<ChessMove> GetLegalMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            List<ChessMove> KingMoves = new ArrayList<>();
            EnumSet<moves> king_moves = EnumSet.of(moves.UP, moves.LEFT, moves.RIGHT, moves.DOWN, moves.UP_LEFT,
                    moves.UP_RIGHT, moves.DOWN_LEFT, moves.DOWN_RIGHT);
            for (moves move : king_moves) {
                int curr_x = position.getColumn();
                int curr_y = position.getRow();
                ChessPosition next = new ChessPosition(curr_y + move.getDY(),curr_x + move.getDX());
                if (OutOfBounds(next)) {
                    ChessPiece square = board.getPiece(next);
                    //space is empty
                    if (square == null) {
                        ChessMove valid_move = new ChessMove(position, next, null);
                        KingMoves.add(valid_move);
                    }
                    //space is not empty, and piece captures
                    else {
                        if (square.getTeamColor() != piece.getTeamColor()) {
                            ChessMove valid_move = new ChessMove(position, next, null);
                            KingMoves.add(valid_move);
                        }
                    }
                }
            }
            return KingMoves;
        }

        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            List<ChessMove> QueenMoves = new ArrayList<>();
            EnumSet<moves> queen_moves = EnumSet.of(moves.UP, moves.DOWN, moves.LEFT, moves.RIGHT, moves.DOWN_LEFT,
            moves.UP_RIGHT, moves.DOWN_RIGHT, moves.UP_LEFT);
            for(moves move : queen_moves)
            {
                return QueenMoves;
            }

        return QueenMoves;
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            List<ChessMove> RookMoves = new ArrayList<>();
            EnumSet<moves> rook_moves = EnumSet.of(moves.DOWN, moves.UP, moves.RIGHT, moves.LEFT);
            return List.of();

        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            List<ChessMove> BishopMoves = new ArrayList<>();
            EnumSet<moves> bishop_moves = EnumSet.of(moves.DOWN_LEFT, moves.DOWN_RIGHT, moves.UP_LEFT, moves.UP_RIGHT);

            return List.of();

        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            List<ChessMove> KnightMoves = new ArrayList<>();
            EnumSet<moves> knight_moves = EnumSet.of(moves.D_D_L, moves.D_D_R, moves.U_U_R, moves.U_U_L);



        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            List<ChessMove> PawnMoves = new ArrayList<>();
            EnumSet<moves> pawn_moves = EnumSet.of(moves.UP, moves.UP_LEFT, moves.UP_RIGHT);
            return List.of();

        }
        return List.of();
    }
}