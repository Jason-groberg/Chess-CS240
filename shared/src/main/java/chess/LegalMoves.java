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
        U_U_R(1, 2), U_U_L(-1, 2), D_D_L(-1, -2), D_D_R(1, -2), L_L_U(2, 1),
        L_L_D(2, -1), R_R_U(-2, 1), R_R_D(-2, -1);

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

    public static boolean OutOfBounds(int row, int col) {
        return row - 1 < 8 && row - 1 >= 0
                && col - 1 < 8 && col - 1 >= 0;
    }

    // returns a list of all legal moves given piece and position
    public static Collection<ChessMove> GetLegalMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            List<ChessMove> KingMoves = new ArrayList<>();
            EnumSet<moves> king_moves = EnumSet.of(moves.UP, moves.LEFT, moves.RIGHT, moves.DOWN, moves.UP_LEFT,
                    moves.UP_RIGHT, moves.DOWN_LEFT, moves.DOWN_RIGHT);
            for (moves move : king_moves) {
                int curr_col = position.getColumn();
                int curr_row = position.getRow();
                if (OutOfBounds(curr_col + move.getDX(), curr_row + move.getDY())) {
                    ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                    ChessPiece square = board.getPiece(next);
                    //space is empty and piece can move
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
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            List<ChessMove> QueenMoves = new ArrayList<>();
            EnumSet<moves> queen_moves = EnumSet.of(moves.UP, moves.DOWN, moves.LEFT, moves.RIGHT, moves.DOWN_LEFT,
                    moves.UP_RIGHT, moves.DOWN_RIGHT, moves.UP_LEFT);
            for (moves move : queen_moves) {
                int curr_col = position.getColumn();
                int curr_row = position.getRow();
                //increment in the testing direction each loop, until piece captures, is blocked, or hits the edge.
                while (OutOfBounds(curr_row + move.getDY(), curr_col + move.getDX())) {
                    ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                    ChessPiece square = board.getPiece(next);
                    if (square == null) {
                        ChessMove valid_move = new ChessMove(position, next, null);
                        QueenMoves.add(valid_move);
                        curr_row += move.getDY();
                        curr_col += move.getDX();
                    } else {
                        if (square.getTeamColor() != piece.getTeamColor()) {
                            ChessMove valid_move = new ChessMove(position, next, null);
                            QueenMoves.add(valid_move);
                            break;
                        }
                        break; //catch same color block
                    }
                }
            }
            return QueenMoves;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            List<ChessMove> RookMoves = new ArrayList<>();
            EnumSet<moves> rook_moves = EnumSet.of(moves.DOWN, moves.UP, moves.RIGHT, moves.LEFT);
            for (moves move : rook_moves) {
                int curr_col = position.getColumn();
                int curr_row = position.getRow();
                //increment in the testing direction each loop, until piece captures, is blocked, or hits the edge.
                while (OutOfBounds(curr_row + move.getDY(), curr_col + move.getDX())) {
                    ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                    ChessPiece square = board.getPiece(next);
                    if (square == null) {
                        ChessMove valid_move = new ChessMove(position, next, null);
                        RookMoves.add(valid_move);
                        curr_row += move.getDY();
                        curr_col += move.getDX();
                    } else {
                        if (square.getTeamColor() != piece.getTeamColor()) {
                            ChessMove valid_move = new ChessMove(position, next, null);
                            RookMoves.add(valid_move);
                            break;
                        }
                        break; //catch same color block
                    }
                }
            }
            return RookMoves;
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            List<ChessMove> BishopMoves = new ArrayList<>();
            EnumSet<moves> bishop_moves = EnumSet.of(moves.DOWN_LEFT, moves.DOWN_RIGHT, moves.UP_LEFT, moves.UP_RIGHT);
            for (moves move : bishop_moves) {
                int curr_col = position.getColumn();
                int curr_row = position.getRow();
                //increment in the testing direction each loop, until piece captures, is blocked, or hits the edge.
                while (OutOfBounds(curr_row + move.getDY(), curr_col + move.getDX())) {
                    ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                    ChessPiece square = board.getPiece(next);
                    if (square == null) {
                        ChessMove valid_move = new ChessMove(position, next, null);
                        BishopMoves.add(valid_move);
                        curr_row += move.getDY();
                        curr_col += move.getDX();
                    } else {
                        if (square.getTeamColor() != piece.getTeamColor()) {
                            ChessMove valid_move = new ChessMove(position, next, null);
                            BishopMoves.add(valid_move);
                            break;
                        }
                        break; //catch same color block
                    }
                }
            }
            return BishopMoves;

        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            List<ChessMove> KnightMoves = new ArrayList<>();
            EnumSet<moves> knight_moves = EnumSet.of(moves.D_D_L, moves.D_D_R, moves.U_U_R, moves.U_U_L, moves.L_L_D, moves.L_L_U, moves.R_R_D, moves.R_R_U);
            for (moves move : knight_moves) {
                int curr_col = position.getColumn();
                int curr_row = position.getRow();
                if (OutOfBounds(curr_col + move.getDX(), curr_row + move.getDY())) {
                    ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                    ChessPiece square = board.getPiece(next);
                    //space is empty
                    if (square == null) {
                        ChessMove valid_move = new ChessMove(position, next, null);
                        KnightMoves.add(valid_move);
                    }
                    //space is not empty, and piece captures
                    else {
                        if (square.getTeamColor() != piece.getTeamColor()) {
                            ChessMove valid_move = new ChessMove(position, next, null);
                            KnightMoves.add(valid_move);
                        }
                    }
                }
            }
            return KnightMoves;
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            List<ChessMove> PawnMoves = new ArrayList<>();
            boolean isWhite = (piece.getTeamColor() == ChessGame.TeamColor.WHITE);
            EnumSet<moves> pawn_moves_white = EnumSet.of( moves.UP_LEFT, moves.UP_RIGHT);
            EnumSet<moves> pawn_moves_black = EnumSet.of(moves.DOWN_LEFT, moves.DOWN_RIGHT);

            //white pawn moves
            if (isWhite) {
                int curr_col = position.getColumn();
                int curr_row = position.getRow();
                //first test to see if white pawn can move one space
                if (OutOfBounds(curr_row + 1, curr_col + 0)) {
                    ChessPosition next = new ChessPosition(curr_row + 1, curr_col + 0);
                    ChessPiece square = board.getPiece(next);
                    if (square == null) {
                        if (curr_row < 7) {
                            ChessMove valid_move = new ChessMove(position, next, null);
                            PawnMoves.add(valid_move);
                            //test to see if double move is open
                            if (curr_row == 2) {
                                ChessPosition double_next = new ChessPosition(curr_row + 2, curr_col);
                                ChessPiece double_square = board.getPiece(double_next);
                                if (double_square == null) {
                                    ChessMove valid_double_move = new ChessMove(position, double_next, null);
                                    PawnMoves.add(valid_double_move);
                                }
                            }
                        }
                        // pawn is on row 7 and can promote
                        else {
                            ChessPosition promotion_square = new ChessPosition(curr_row + 1, curr_col);
                            ChessMove promotion_move = new ChessMove(position, promotion_square, ChessPiece.PieceType.QUEEN);
                            ChessMove promotion_move1 = new ChessMove(position, promotion_square, ChessPiece.PieceType.ROOK);
                            ChessMove promotion_move2 = new ChessMove(position, promotion_square, ChessPiece.PieceType.KNIGHT);
                            ChessMove promotion_move3 = new ChessMove(position, promotion_square, ChessPiece.PieceType.BISHOP);
                            PawnMoves.add(promotion_move);
                            PawnMoves.add(promotion_move1);
                            PawnMoves.add(promotion_move2);
                            PawnMoves.add(promotion_move3);
                        }
                    }
                }
                //pawn captures
                for (moves move : pawn_moves_white) {
                    if (OutOfBounds(curr_col + move.getDX(), curr_row + move.getDY())) {
                        ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                        ChessPiece square = board.getPiece(next);
                        if (square != null) {
                            if(curr_row <7) {
                                if (square.getTeamColor() != piece.getTeamColor()) {
                                    ChessMove valid_move = new ChessMove(position, next, null);
                                    PawnMoves.add(valid_move);
                                }
                            }
                            else {
                                if (square.getTeamColor() != piece.getTeamColor()) {
                                    ChessPosition promotion_square = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                                    ChessMove promotion_move = new ChessMove(position, promotion_square, ChessPiece.PieceType.QUEEN);
                                    ChessMove promotion_move1 = new ChessMove(position, promotion_square, ChessPiece.PieceType.ROOK);
                                    ChessMove promotion_move2 = new ChessMove(position, promotion_square, ChessPiece.PieceType.KNIGHT);
                                    ChessMove promotion_move3 = new ChessMove(position, promotion_square, ChessPiece.PieceType.BISHOP);
                                    PawnMoves.add(promotion_move);
                                    PawnMoves.add(promotion_move1);
                                    PawnMoves.add(promotion_move2);
                                    PawnMoves.add(promotion_move3);
                                }
                            }
                        }
                        //pawn is on row 7
                    }
                }
            }
            //pawn is black
            else {
                int curr_col = position.getColumn();
                int curr_row = position.getRow();
                //first test to see if white pawn can move one space
                if (OutOfBounds(curr_row - 1, curr_col + 0)) {
                    ChessPosition next = new ChessPosition(curr_row - 1, curr_col + 0);
                    ChessPiece square = board.getPiece(next);
                    if (square == null) {
                        if (curr_row > 2) {
                            ChessMove valid_move = new ChessMove(position, next, null);
                            PawnMoves.add(valid_move);
                            //test to see if double move is open
                            if (curr_row == 7) {
                                ChessPosition double_next = new ChessPosition(curr_row - 2, curr_col);
                                ChessPiece double_square = board.getPiece(double_next);
                                if (double_square == null) {
                                    ChessMove valid_double_move = new ChessMove(position, double_next, null);
                                    PawnMoves.add(valid_double_move);
                                }
                            }
                        }
                        // black pawn is on row 1 and can promote
                        else {
                            ChessPosition promotion_square = new ChessPosition(curr_row - 1, curr_col);
                            ChessMove promotion_move = new ChessMove(position, promotion_square, ChessPiece.PieceType.QUEEN);
                            ChessMove promotion_move1 = new ChessMove(position, promotion_square, ChessPiece.PieceType.ROOK);
                            ChessMove promotion_move2 = new ChessMove(position, promotion_square, ChessPiece.PieceType.KNIGHT);
                            ChessMove promotion_move3 = new ChessMove(position, promotion_square, ChessPiece.PieceType.BISHOP);
                            PawnMoves.add(promotion_move);
                            PawnMoves.add(promotion_move1);
                            PawnMoves.add(promotion_move2);
                            PawnMoves.add(promotion_move3);
                        }
                    }
                }
                //pawn captures
                for (moves move : pawn_moves_black) {
                    if (OutOfBounds(curr_col + move.getDX(), curr_row + move.getDY())) {
                        ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                        ChessPiece square = board.getPiece(next);
                        if (square != null) {
                            if(curr_row > 2) {
                                if (square.getTeamColor() != piece.getTeamColor()) {
                                    ChessMove valid_move = new ChessMove(position, next, null);
                                    PawnMoves.add(valid_move);
                                }
                            }
                            else {
                                if (square.getTeamColor() != piece.getTeamColor()) {
                                    ChessPosition promotion_square = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                                    ChessMove promotion_move = new ChessMove(position, promotion_square, ChessPiece.PieceType.QUEEN);
                                    ChessMove promotion_move1 = new ChessMove(position, promotion_square, ChessPiece.PieceType.ROOK);
                                    ChessMove promotion_move2 = new ChessMove(position, promotion_square, ChessPiece.PieceType.KNIGHT);
                                    ChessMove promotion_move3 = new ChessMove(position, promotion_square, ChessPiece.PieceType.BISHOP);
                                    PawnMoves.add(promotion_move);
                                    PawnMoves.add(promotion_move1);
                                    PawnMoves.add(promotion_move2);
                                    PawnMoves.add(promotion_move3);
                                }
                            }
                        }
                    }
                }
            }
            return PawnMoves;
        }
        return List.of();
    }
}