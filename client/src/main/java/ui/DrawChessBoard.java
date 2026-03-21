package ui;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class DrawChessBoard extends EscapeSequences {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;

    //Padded Characters
    private static final String EMPTY = EscapeSequences.EMPTY;

    //White Pieces
    private static final String WHITE_KING = EscapeSequences.WHITE_KING;
    private static final String WHITE_QUEEN = EscapeSequences.WHITE_QUEEN;
    private static final String WHITE_BISHOP = EscapeSequences.WHITE_BISHOP;
    private static final String WHITE_KNIGHT = EscapeSequences.WHITE_KNIGHT;
    private static final String WHITE_ROOK = EscapeSequences.WHITE_ROOK;
    private static final String WHITE_PAWN = EscapeSequences.WHITE_PAWN;
    private static final String[] whiteFiles = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};

    //Black Pieces
    private static final String BLACK_KING = EscapeSequences.BLACK_KING;
    private static final String BLACK_QUEEN = EscapeSequences.BLACK_QUEEN;
    private static final String BLACK_BISHOP = EscapeSequences.BLACK_BISHOP;
    private static final String BLACK_KNIGHT = EscapeSequences.BLACK_KNIGHT;
    private static final String BLACK_ROOK = EscapeSequences.BLACK_ROOK;
    private static final String BLACK_PAWN = EscapeSequences.BLACK_PAWN;
    private static final String[] blackFiles = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
    public static ChessBoard board;

    private static boolean isWhite;

    public static void draw(PrintStream out, ChessGame game, boolean isWhiteSide){
        out.print(ERASE_SCREEN);
        board = game.getBoard();
        isWhite = isWhiteSide;
        drawChessBoard(out, board, isWhite);
        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void drawHeaders(PrintStream out, boolean isWhite){
        setHeader(out);
        String[] files;
        if(isWhite){files = whiteFiles;}
        else{files = blackFiles;}
        out.print(EMPTY + " " + EMPTY);
        for (String file : files){
            out.print(EMPTY +  file + "   ");
        }
        out.print(EMPTY + " " + EMPTY);
        out.println();
    }

    private static void drawChessBoard(PrintStream out, ChessBoard board, boolean isWhite) {
        drawHeaders(out, isWhite);

        if(isWhite) {
            for(int rank = BOARD_SIZE_IN_SQUARES; rank >=1; rank--){
                drawChessRank(out, rank, board, isWhite);
            }
        }
        else{
            for(int rank = 1; rank <= BOARD_SIZE_IN_SQUARES; rank++){
                drawChessRank(out, rank, board, isWhite);
            }
        }
        drawHeaders(out, isWhite);
        out.println();
    }


    private static void drawChessRank(PrintStream out, int rank, ChessBoard board, boolean isWhite) {
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; squareRow++) {

            drawChessRankNumber(out, rank, squareRow);

            for (int boardCol = 1; boardCol <= BOARD_SIZE_IN_SQUARES; boardCol++) {
                int col;
                if(isWhite){col = boardCol;}
                else{col = 9 - boardCol;}
                setSquareColor(out, rank, col);

                if(squareRow ==1){
                    out.print("  ");
                    ChessPiece piece = board.getPiece(new ChessPosition(rank, col));
                    out.print(findPiece(piece));
                    out.print("  ");
                }
                else{
                    out.print("  ");
                    out.print(EMPTY);
                    out.print("  ");
                }
            }

            drawChessRankNumber(out, rank, squareRow);
            out.print(RESET_TEXT_COLOR);
            out.println();
        }
    }

    private static void drawChessRankNumber(PrintStream out, int rank, int squareRow){
        setHeader(out);
        if(squareRow == 1){
            out.print(EMPTY + rank + EMPTY);
        }
        else{
            out.print(EMPTY + " " + EMPTY);
        }
    }


    private static void setSquareColor(PrintStream out, int rank, int col){
        //square is white
        if((rank + col) % 2 == 1){
            out.print(SET_BG_COLOR_WHITE);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(SET_TEXT_BOLD);

        }
        else{
            out.print(SET_BG_COLOR_DARK_GREEN);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(SET_TEXT_BOLD);
        }
    }

    private static void setHeader(PrintStream out){
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_TEXT_ITALIC);
        out.print(SET_TEXT_BOLD);
    }

    private static void setRed(PrintStream out){
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static String findPiece(ChessPiece piece){
        if(piece == null){
            return EMPTY;
        }
        String pieceChar = EMPTY;
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();

        if(pieceColor == ChessGame.TeamColor.WHITE){

            switch(type){
                case PAWN: pieceChar = WHITE_PAWN; break;
                case BISHOP: pieceChar = WHITE_BISHOP; break;
                case KNIGHT: pieceChar = WHITE_KNIGHT; break;
                case ROOK: pieceChar = WHITE_ROOK; break;
                case KING: pieceChar = WHITE_KING; break;
                case QUEEN: pieceChar = WHITE_QUEEN; break;
            }
        }
        else{
            switch(type){
                case PAWN: pieceChar = BLACK_PAWN; break;
                case BISHOP: pieceChar = BLACK_BISHOP; break;
                case KNIGHT: pieceChar = BLACK_KNIGHT; break;
                case ROOK: pieceChar = BLACK_ROOK; break;
                case KING: pieceChar = BLACK_KING; break;
                case QUEEN: pieceChar = BLACK_QUEEN; break;
            }
        }
        return pieceChar;
    }
}
