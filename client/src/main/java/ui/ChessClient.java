package ui;
import chess.ChessGame;
import facade.ServerFacade;
import model.GameData;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.requests.LoginRequest;
import model.requests.RegisterRequest;
import model.results.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static ui.DrawChessBoard.draw;

public class ChessClient {
    public enum State{
        SIGNEDIN,
        SIGNEDOUT
    }
    private State state = State.SIGNEDOUT;
    private final String serverUrl;
    private final ServerFacade server;
    private String authToken = null;
    private List<ListResult> gamelist = null;

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

    public String observeGame(String... params) throws ResponseException{
        if(params.length != 1){
            throw new ResponseException("Expected: <ID>");
        }
        assertSignedIn();

        try{
            int id = Integer.parseInt(params[0])-1;
            if(gamelist == null || id < 0 || id >= gamelist.size()){
                throw new ResponseException("Error: game id is not valid, use list to see valid game ids");
            }

            ListResult gameResult = gamelist.get(id);
            JoinGameRequest request = new JoinGameRequest(null, gameResult.gameID());

            draw(System.out, gameResult.game(), true);
            return "Currently watching: " + gameResult.gameName();
        }catch(Exception e){
            throw new ResponseException("Error: failed to observe game " + e.getMessage());
        }
    }

    public String logout(String... params) throws ResponseException {
        try{
            server.logout(authToken);
            authToken = null;
            state = State.SIGNEDOUT;
            return "Successfully Logged out";

        }catch(Exception e){
            throw new ResponseException("Error: Logout failed: ");
        }
    }

    public String listGames() throws ResponseException{
        assertSignedIn();

        try{
            ListofListResult result = server.listGames(authToken);
            gamelist = new ArrayList<>(result.games());
            StringBuilder sb = new StringBuilder();
            sb.append("------ Current Active Games ------\n");
            if(gamelist.isEmpty()){
                sb.append("No active games, use the create command to start\n");
            }else{
                for(int i=0; i < gamelist.size();i++){
                    var game = gamelist.get(i);
                    sb.append("Game ID: " + (i+1) + ", Game Name: " + (game.gameName()));
                    String whiteUsername = "<none>";
                    String blackUsername ="<none>";
                    if(game.whiteUsername() !=null){
                        whiteUsername = game.whiteUsername();
                    }
                    if(game.blackUsername()!=null){
                        blackUsername = game.blackUsername();
                    }
                    sb.append("WhitePlayer: " + whiteUsername + "|BlackPlayer: " + blackUsername + "\n");
                }
            }
            return sb.toString();
        }catch (Exception e){
            throw new ResponseException("Error: failed to list games" + e.getMessage());
        }
    }

    public String joinGame(String... params) throws ResponseException{
        if(params.length < 1){
            throw new ResponseException("Expected: <ID> [BLACK|WHITE]");
        }
        assertSignedIn();
        try{
            int gameID = Integer.parseInt(params[0]) -1;
            if(gamelist ==null || gameID <0 ||gameID >= gamelist.size()){
                throw new ResponseException("Error: invalid game id. Use list to see valid ids");
            }
            ListResult gameResult = gamelist.get(gameID);
            int realGameId = gameResult.gameID();

            ChessGame.TeamColor playerColor =null;

            if(params.length > 1){
                String color = params[1];
                if(color.equalsIgnoreCase("WHITE")){
                    playerColor=ChessGame.TeamColor.WHITE;
                }
                else if(color.equalsIgnoreCase("BLACK")){
                    playerColor = ChessGame.TeamColor.BLACK;
                }
            }

            JoinGameRequest request = new JoinGameRequest(playerColor, realGameId);
            server.joinGame(request,authToken);

            boolean isWhite = (playerColor == ChessGame.TeamColor.WHITE);
            draw(System.out, gameResult.game(), isWhite);
            return String.format("Joined game ");
        }catch(Exception e){
            throw new ResponseException("Join falied: Expected <ID> [BLACK|WHITE]\nTry list to see joinable games");
        }

    }

    public String createGame(String... params) throws ResponseException{
        if(params.length!=1){
            throw new ResponseException("Expected: <NAME>");
        }
        assertSignedIn();

        try{
            CreateGameRequest request = new CreateGameRequest(params[0]);
            CreateGameResult result = server.createGame(request, authToken);
            return String.format("Create new game: %s", request.gameName());
        }catch(Exception e){
            throw new ResponseException("Error: failed to create game" + e.getMessage());
        }
    }

    public String register(String... params)throws Exception{
        if(params.length !=3){
            throw new ResponseException("Error: Expected <USERNAME> <PASSWORD> <EMAIL>");
        }
        RegisterRequest request = new RegisterRequest(params[0],params[1],params[2]);

        try{
            RegisterResult result = server.register(request);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            return String.format("Welcome %s\nStart playing with:\n%s", result.username(), help());
        }catch(Exception e){
            throw new ResponseException("Error: Register failed " + e.getMessage());
        }
    }

    public String login(String... params) throws ResponseException{
        if(params.length != 2){
            throw new ResponseException("Error: Expected <USERNAME> <PASSWORD>");
        }


        try{
            LoginRequest request = new LoginRequest(params[0],params[1]);
            LoginResult result = server.login(request);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            return String.format("Welcome %s\nStart playing with:\n%s", result.username(), help());
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
