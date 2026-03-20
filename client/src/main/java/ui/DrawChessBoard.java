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
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    //Padded Characters
    private static final String EMPTY = EscapeSequences.EMPTY;

    //White Pieces
    private static final String WHITE_KING = EscapeSequences.WHITE_KING;
    private static final String WHITE_QUEEN = EscapeSequences.WHITE_QUEEN;
    private static final String WHITE_BISHOP = EscapeSequences.WHITE_BISHOP;
    private static final String WHITE_KNIGHT = EscapeSequences.WHITE_KNIGHT;
    private static final String WHITE_ROOK = EscapeSequences.WHITE_ROOK;
    private static final String WHITE_PAWN = EscapeSequences.WHITE_PAWN;
    private static final String[] whiteFiles = new String[] {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};

    //Black Pieces
    private static final String BLACK_KING = EscapeSequences.BLACK_KING;
    private static final String BLACK_QUEEN = EscapeSequences.BLACK_QUEEN;
    private static final String BLACK_BISHOP = EscapeSequences.BLACK_BISHOP;
    private static final String BLACK_KNIGHT = EscapeSequences.BLACK_KNIGHT;
    private static final String BLACK_ROOK = EscapeSequences.BLACK_ROOK;
    private static final String BLACK_PAWN = EscapeSequences.BLACK_PAWN;
    private static final String[] blackFiles = new String[]{" h ", " g ", " f ", " e ", " d ", " c ", " b ", " a "};
    public static ChessBoard normalBoard = new ChessBoard();

    public static void main(String[] args){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        normalBoard.resetBoard();
        drawChessBoard(out, normalBoard, true);

    }

    private static void drawHeaders(PrintStream out, boolean isWhite){
        setHeader(out);
        out.print("   ");
        String[] files;
        if(isWhite){files = whiteFiles;}
        else{files = blackFiles;}

        for (String file : files){
            out.print(file);
        }

        out.print("   ");
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
                    ChessPiece piece = board.getPiece(new ChessPosition(rank, col));
                    out.print(findPiece(piece));
                }
                else{
                    out.print(EMPTY);
                }

            }

            drawChessRankNumber(out, rank, squareRow);
            out.println();
        }

    }


    private static void drawChessRankNumber(PrintStream out, int rank, int squareRow){
        setHeader(out);
        if(squareRow == 1){
            out.print(EMPTY + rank + EMPTY);
        }
        else{
            out.print(EMPTY);
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
