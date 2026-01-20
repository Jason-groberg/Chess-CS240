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

        public int getDY() {
            return dy;
        }
    }

    public static boolean OutOfBounds(ChessPosition pos) {
        return pos.getColumn() <= 8 && pos.getColumn() >= 0
                && pos.getRow() <= 8 && pos.getRow() >= 0;
    }


    public static Collection<ChessMove> GetLegalMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            List<ChessMove> KingMoves = new ArrayList<>();
            EnumSet<moves> king_moves = EnumSet.of(moves.UP, moves.LEFT, moves.RIGHT, moves.DOWN, moves.UP_LEFT,
                    moves.UP_RIGHT, moves.DOWN_LEFT, moves.DOWN_RIGHT);

            for (moves move : king_moves) {
                int curr_x = position.getColumn();
                int curr_y = position.getRow();
                ChessPosition next = new ChessPosition(curr_x + move.dx, curr_y + move.dy);
                if (OutOfBounds(next)) {
                    ChessPiece square = board.getPiece(next);
                    //space is empty
                    if (square == null) {
                        ChessMove valid_move = new ChessMove(position, next, square.getPieceType());
                        KingMoves.add(valid_move);
                    }
                    //space is not empty, and piece captures
                    else {
                        if (square.getTeamColor() != piece.getTeamColor()) {
                            ChessMove valid_move = new ChessMove(position, next, square.getPieceType());
                            KingMoves.add(valid_move);
                        }
                    }
                }
                return KingMoves;
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return List.of();

        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return List.of();

        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return List.of();

        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return List.of();

        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return List.of();

        }
        return List.of();
    }
}



//    public Collection<ChessMove> bishop_move() {
//
//    }
//
//    public Collection<ChessMove> pawn_moves() {
//
//    }
//
//    public Collection<ChessMove> rook_moves() {
//
//    }
//
//    public Collection<ChessMove> Knight_moves() {
//
//    }
//
//    public Collection<ChessMove> Queen_moves() {
//
//    }
//
//    public Collection<ChessMove> King_moves() {
//
//    }
//}
