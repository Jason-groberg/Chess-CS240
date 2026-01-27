package chess;
import java.util.Collection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class PieceMoves {
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

    public static boolean InBounds(int row, int col) {
        return row < 9 && row > 0
                && col < 9 && col > 0;
    }

    public static Collection<ChessMove> FindMoves(ChessBoard board, ChessPosition position, EnumSet<moves> legal_moves) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);
        for (moves move : legal_moves) {
            int curr_col = position.getColumn();
            int curr_row = position.getRow();
            //increment in the testing direction each loop, until piece captures, is blocked, or hits the edge.
            while (InBounds(curr_row + move.getDY(), curr_col + move.getDX())) {
                ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                ChessPiece square = board.getPiece(next);
                if (square == null) {
                    legalMoves.add(new ChessMove(position, next, null));
                    if (piece.getPieceType() == ChessPiece.PieceType.KING || piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        break;
                    }
                    curr_row += move.getDY();
                    curr_col += move.getDX();
                } else {
                    if (square.getTeamColor() != piece.getTeamColor()) {
                        legalMoves.add(new ChessMove(position, next, null));
                        break;
                    }
                    break; //catch same color block
                }
            }
        }
        return legalMoves;
    }

    public Collection<ChessMove> pawn_promotion(ChessPosition startposition, ChessPosition promotion_square) {
        ArrayList<ChessMove> promotion_moves = new ArrayList<>();
        EnumSet<ChessPiece.PieceType> types = EnumSet.of(ChessPiece.PieceType.ROOK, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP);
        for (ChessPiece.PieceType type : types) {
            promotion_moves.add(new ChessMove(startposition, promotion_square, type));
        }
        return promotion_moves;
    }

    // returns a list of all legal moves given piece and position
    public Collection<ChessMove> GetLegalMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            EnumSet<moves> king_moves = EnumSet.of(moves.UP, moves.LEFT, moves.RIGHT, moves.DOWN, moves.UP_LEFT,
                    moves.UP_RIGHT, moves.DOWN_LEFT, moves.DOWN_RIGHT);
            return FindMoves(board, position, king_moves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            EnumSet<moves> queen_moves = EnumSet.of(moves.UP, moves.DOWN, moves.LEFT, moves.RIGHT, moves.DOWN_LEFT,
                    moves.UP_RIGHT, moves.DOWN_RIGHT, moves.UP_LEFT);
            return FindMoves(board, position, queen_moves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            EnumSet<moves> rook_moves = EnumSet.of(moves.DOWN, moves.UP, moves.RIGHT, moves.LEFT);
            return FindMoves(board, position, rook_moves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            EnumSet<moves> bishop_moves = EnumSet.of(moves.DOWN_LEFT, moves.DOWN_RIGHT, moves.UP_LEFT, moves.UP_RIGHT);
            return FindMoves(board, position, bishop_moves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            EnumSet<moves> knight_moves = EnumSet.of(moves.D_D_L, moves.D_D_R, moves.U_U_R, moves.U_U_L, moves.L_L_D, moves.L_L_U, moves.R_R_D, moves.R_R_U);
            return FindMoves(board, position, knight_moves);

        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            Collection<ChessMove> pawn_moves = new ArrayList<>();
            boolean isWhite = (piece.getTeamColor() == ChessGame.TeamColor.WHITE);
            Collection<moves> white_pawn = EnumSet.of(moves.UP_RIGHT, moves.UP_LEFT, moves.UP);
            Collection<moves> black_pawn = EnumSet.of(moves.DOWN_RIGHT, moves.DOWN_LEFT, moves.DOWN);
            if (isWhite) {
                for (moves move : white_pawn) {
                    int curr_row = position.getRow();
                    int curr_col = position.getColumn();
                    if (InBounds(curr_row + move.getDY(), curr_col + move.getDX())) {
                        ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                        ChessPiece target = board.getPiece(next);
                        if (target == null && move.getDX() == 0) {
                            //single move foward
                            if (curr_row < 7) {
                                pawn_moves.add(new ChessMove(position, next, null));
                                if (curr_row == 2) {
                                    ChessPosition double_next = new ChessPosition(curr_row + 2, curr_col + move.getDX());
                                    ChessPiece target_2 = board.getPiece(double_next);
                                    if (target_2 == null) {
                                        pawn_moves.add(new ChessMove(position, double_next, null));
                                    }
                                }
                            } else {
                                pawn_moves.addAll(pawn_promotion(position, next));
                            }
                        }
                        //pawn captures
                        else if (target != null && target.getTeamColor() != piece.getTeamColor() && move.getDX() != 0) {
                            if (curr_row < 7) {
                                pawn_moves.add(new ChessMove(position, next, null));
                            } else {
                                pawn_moves.addAll(pawn_promotion(position, next));
                            }
                        }
                    }
                }
            }
            //pawn is black
            else {
                for (moves move : black_pawn) {
                    int curr_row = position.getRow();
                    int curr_col = position.getColumn();
                    if (InBounds(curr_row + move.getDY(), curr_col + move.getDX())) {
                        ChessPosition next = new ChessPosition(curr_row + move.getDY(), curr_col + move.getDX());
                        ChessPiece target = board.getPiece(next);
                        if (target == null && move.getDX() == 0) {
                            //single move foward
                            if (curr_row > 2) {
                                pawn_moves.add(new ChessMove(position, next, null));
                                if (curr_row == 7) {
                                    ChessPosition double_next = new ChessPosition(curr_row - 2, curr_col + move.getDX());
                                    ChessPiece target_2 = board.getPiece(double_next);
                                    if (target_2 == null) {
                                        pawn_moves.add(new ChessMove(position, double_next, null));
                                    }
                                }
                            } else {
                                pawn_moves.addAll(pawn_promotion(position, next));
                            }
                        }
                        //pawn captures
                        else if (target != null && target.getTeamColor() != piece.getTeamColor() && move.getDX() != 0) {
                            if (curr_row > 2) {
                                pawn_moves.add(new ChessMove(position, next, null));
                            } else {
                                pawn_moves.addAll(pawn_promotion(position, next));
                            }
                        }
                    }
                }
            }
            return pawn_moves;
        }
        return List.of();
    }
}
