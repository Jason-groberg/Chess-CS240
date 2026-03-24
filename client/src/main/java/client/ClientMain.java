package client;
import chess.*;
import ui.Repl;
import ui.EscapeSequences;
import static ui.EscapeSequences.WHITE_PAWN;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("\n♙♘♗♖♕♔ 240 Chess Client ♟♜♞♝♛♚" );
        String serverUrl = "http://localhost:8080";
        Repl repl = new Repl(serverUrl);
        repl.run();
    }



}
