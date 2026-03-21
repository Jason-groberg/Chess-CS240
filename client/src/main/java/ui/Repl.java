package ui;
import java.util.Scanner;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;

public class Repl {

    public final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        var result = "";

        while(!result.equals("quit")){
            try{

                String line = scanner.nextLine();
                result = client.eval(line);
                System.out.print(result);
            }catch(Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void printPrompt(){
        System.out.println("\n" + ">>>" + GREEN);
    }
}
