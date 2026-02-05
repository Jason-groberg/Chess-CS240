package chess;
import java.util.Collection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class PieceMoves {
    public enum Moves {
        //white POV
        RIGHT(1, 0), LEFT(-1, 0), DOWN(0, -1), UP(0, 1),
        UP_RIGHT(1, 1), UP_LEFT(-1, 1), DOWN_LEFT(-1, -1), DOWN_RIGHT(1, -1),
        //Knight Moves
        U_U_R(1, 2), U_U_L(-1, 2), D_D_L(-1, -2), D_D_R(1, -2),
        L_L_U(2, 1), L_L_D(2, -1), R_R_U(-2, 1), R_R_D(-2, -1);

        private final int dx, dy;

        Moves(int dx, int dy) {
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

    public static boolean inBounds(int row, int col) {
        return row < 9 && row > 0
                && col < 9 && col > 0;
    }

    public static Collection<ChessMove> findMoves(ChessBoard board, ChessPosition position, EnumSet<Moves> movesEnumSet) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);
        for (Moves move : movesEnumSet) {
            int currCol = position.getColumn();
            int currRow = position.getRow();
            //increment in the testing direction each loop, until piece captures, is blocked, or hits the edge.
            while (inBounds(currRow + move.getDY(), currCol + move.getDX())) {
                ChessPosition next = new ChessPosition(currRow + move.getDY(), currCol + move.getDX());
                ChessPiece square = board.getPiece(next);
                if (square == null) {
                    legalMoves.add(new ChessMove(position, next, null));
                    if (piece.getPieceType() == ChessPiece.PieceType.KING || piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        break;
                    }
                    currRow += move.getDY();
                    currCol += move.getDX();
                }
                else {
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

    public void pawnPromotion(ChessPosition startPosition, ChessPosition promotionSquare, Collection<ChessMove> pawnMoves) {
        EnumSet<ChessPiece.PieceType> types = EnumSet.of(ChessPiece.PieceType.ROOK, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP);
        for (ChessPiece.PieceType type : types) {
            pawnMoves.add(new ChessMove(startPosition, promotionSquare, type));
        }
    }

    public void checkDouble(ChessPosition start, ChessBoard board, Collection<ChessMove> pawnMoves, int num) {
        ChessPosition doubleMove = new ChessPosition(start.getRow() + num +num, start.getColumn());
        if(board.getPiece(doubleMove)==null){
            pawnMoves.add(new ChessMove (start, doubleMove, null));
        }
    }

    public Collection<ChessMove> pawnMoves(ChessPosition position, ChessBoard board, EnumSet<Moves> movesEnumSet) {
        Collection<ChessMove> pawnMoves = new ArrayList<>();
        ChessPiece pawn = board.getPiece(position);
        ChessGame.TeamColor pawnColor = pawn.getTeamColor();
        for (Moves move : movesEnumSet) {
            int currRow = position.getRow();
            int currCol = position.getColumn();
            if (inBounds(currRow + move.getDY(), currCol + move.getDX())) {
                ChessPosition next = new ChessPosition(currRow + move.getDY(), currCol + move.getDX());
                ChessPiece target = board.getPiece(next);
                if (target == null && move.getDX() == 0 && pawnColor == ChessGame.TeamColor.WHITE && currRow< 7) {
                    //single move forward
                    pawnMoves.add(new ChessMove(position, next, null));
                    if (currRow == 2) {
                        checkDouble(position, board, pawnMoves, move.getDY());
                    }
                }
                else if(target == null && move.getDX()==0 && pawnColor == ChessGame.TeamColor.WHITE) {
                    pawnPromotion(position, next, pawnMoves);
                }
                if (target == null && move.getDX() == 0 && pawnColor == ChessGame.TeamColor.BLACK && currRow > 2) {
                    //single move forward
                    pawnMoves.add(new ChessMove(position, next, null));
                    if (currRow == 7) {
                        checkDouble(position, board, pawnMoves, move.getDY());
                    }
                }
                else if(target == null && move.getDX()==0 && pawnColor == ChessGame.TeamColor.BLACK) {
                    pawnPromotion(position, next, pawnMoves);
                }
                //pawn captures
                else if (target != null && target.getTeamColor() != pawnColor && move.getDX() != 0) {
                    if (currRow < 7 && pawnColor == ChessGame.TeamColor.WHITE) {
                        pawnMoves.add(new ChessMove(position, next, null));
                    }
                    else if (currRow > 2 && pawnColor == ChessGame.TeamColor.BLACK) {
                        pawnMoves.add(new ChessMove(position, next, null));
                    }
                    else {
                        pawnPromotion(position, next, pawnMoves);
                    }
                }
            }
        }
        return pawnMoves;
    }

    // returns a list of all legal moves given piece and position
    public Collection<ChessMove> getLegalMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            EnumSet<Moves> kingMoves = EnumSet.of(Moves.UP, Moves.LEFT, Moves.RIGHT, Moves.DOWN, Moves.UP_LEFT,
                    Moves.UP_RIGHT, Moves.DOWN_LEFT, Moves.DOWN_RIGHT);
            return findMoves(board, position, kingMoves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            EnumSet<Moves> queenMoves = EnumSet.of(Moves.UP, Moves.DOWN, Moves.LEFT, Moves.RIGHT, Moves.DOWN_LEFT,
                    Moves.UP_RIGHT, Moves.DOWN_RIGHT, Moves.UP_LEFT);
            return findMoves(board, position, queenMoves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            EnumSet<Moves> rookMoves = EnumSet.of(Moves.DOWN, Moves.UP, Moves.RIGHT, Moves.LEFT);
            return findMoves(board, position, rookMoves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            EnumSet<Moves> bishopMoves = EnumSet.of(Moves.DOWN_LEFT, Moves.DOWN_RIGHT, Moves.UP_LEFT, Moves.UP_RIGHT);
            return findMoves(board, position, bishopMoves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            EnumSet<Moves> knightMoves = EnumSet.of(Moves.D_D_L, Moves.D_D_R, Moves.U_U_R, Moves.U_U_L, Moves.L_L_D,
                    Moves.L_L_U, Moves.R_R_D, Moves.R_R_U);
            return findMoves(board, position, knightMoves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            EnumSet<Moves> whitePawnMoves = EnumSet.of(Moves.UP_RIGHT, Moves.UP_LEFT, Moves.UP);
            return pawnMoves(position, board, whitePawnMoves);
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            EnumSet<Moves> blackPawnMoves = EnumSet.of(Moves.DOWN_RIGHT, Moves.DOWN_LEFT, Moves.DOWN);
            return pawnMoves(position, board, blackPawnMoves);
        }
        return List.of();
    }
}
