package ui;
import java.util.Scanner;

public class Repl {

    public final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to my CS 240 Chess Application. Try typing help to start playing");
        Scanner scanner = new Scanner(System.in);
        var result = "";

        while(!result.equals("quit")){
            printPrompt();
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
        System.out.println("\n" + ">>>");
    }
}
