package chess;
import java.util.Scanner;


public class testing {

    public static void main(String[] args) {

        ChessGame game = new ChessGame();
        Scanner scanner = new Scanner(System.in);
        while (!game.isInCheckmate(game.getTeamTurn()) && !game.isInStalemate(game.getTeamTurn())) {
            System.out.println(game.getBoard());
            System.out.println(game.getTeamTurn() + "'s turn. Enter a move: \b");
            String move = scanner.nextLine();
            if(move.equalsIgnoreCase("q")){
                break;
            }
        }
        scanner.close();
    }
}
