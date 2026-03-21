package ui;
import facade.ServerFacade;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Arrays;
import java.util.Collection;

public class ChessClient {
    public enum State{
        SIGNEDIN,
        SIGNEDOUT
    }
    private State state = State.SIGNEDOUT;
    private final String serverUrl;
    private final ServerFacade server;
    private String authToken = null;
    private Collection<GameData> gamelist =null;

    public ChessClient(String url){
        server = new ServerFacade(url);
        serverUrl = url;
    }

    public String eval(String input){
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (state) {
                case SIGNEDOUT -> preLoginEval(cmd, params);
                case SIGNEDIN -> postLoginEval(cmd, params);
            };
        }catch(Exception e){
            return e.getMessage();
        }
    }

    public String preLoginEval(String cmd, String[] params){
        try {
            return switch(cmd) {
                case "login" -> login(params);
                case "quit" -> "quit";
                case "register" -> register(params);
                default -> help();
            };
        }catch (Exception e) {
            return e.getMessage();
        }
    }

    public String postLoginEval(String cmd, String[] params){
        try {
            return switch(cmd){
                case "logout" -> logout(params);
                case "create game" -> createGame(params);
                case "list games" -> listGames();
                case "play game" -> joinGame(params);
                case "observe game" -> observeGame(params);
                default -> help();
            };
        }catch(Exception e){
            return e.getMessage();
        }
    }

    public String register(String[] params)throws Exception{
        if(params.length !=3){
            throw new ResponseException("Error: Expected <USERNAME> <PASSWORD> <EMAIL>");
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];

        try{
            AuthData auth = server.register(username, password,email);
            authToken = auth.authToken();
            state = State.SIGNEDIN;
            return String.format("Welcome %s\nStart playing with:\n%s", username, help());
        }catch(Exception e){
            throw new ResponseException("Error: Register failed" + e.getMessage());
        }
    }

    public String login(String... params) throws ResponseException{
        if(params.length != 2){
            throw new ResponseException("Error: Expected <USERNAME> <PASSWORD>");
        }
        String username = params[0];
        String password = params[1];
        try{
            AuthData auth = server.login(username, password);
            authToken = auth.authToken();
            state = State.SIGNEDIN;
            return String.format("Welcome %s\nStart playing with:\n%s", username, help());
        } catch(Exception e){
            throw new ResponseException("Login failed");
        }
    }

    public String help() {
        if(state == State.SIGNEDOUT){
            return """
                    > register <USERNAME> <PASSWORD> <EMAIL> - to register a new user
                    > login <USERNAME> <PASSWORD> - to start a game
                    > quit - exit application
                    > help - display all commands
                    """;
        }
        // user is singed in
        else{
            return """
                    > logout - to log out of application
                    > create <NAME> - to create a new game to play
                    > join <ID> [WHITE|BLACK] - to join game at given ID 
                    > list - to see all games 
                    > observe <ID> - to watch game @ given ID
                    > quit - to exit game 
                    > help - to view all commands  
                    """;
        }
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT){
            throw new ResponseException("Error: Must sign in first");
        }
    }
}
