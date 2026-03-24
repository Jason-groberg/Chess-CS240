package ui;
import chess.ChessGame;
import facade.ServerFacade;
import model.GameData;
import model.requests.*;
import model.results.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static ui.DrawChessBoard.draw;
import static ui.EscapeSequences.*;


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
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
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
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "clear" -> clearDatabases();
                default -> help();
            };
        }catch(Exception e){
            return e.getMessage();
        }
    }

    public String clearDatabases() throws Exception{
        try{
            server.clearDatabases();
            return "All Databases Cleared";
        }catch(Exception e ){
            throw new ResponseException(ResponseException.Code.ServerError, SET_TEXT_COLOR_RED +
                    "Error: something went wrong clearing database" + RESET_TEXT_COLOR);
        }
    }

    public String observeGame(String... params) throws Exception{
        if(params.length != 1){
            throw new ResponseException(ResponseException.Code.BadRequest, SET_TEXT_COLOR_RED +
                    "Expected: <ID>" + RESET_TEXT_COLOR);
        }
        assertSignedIn();

        try{
            int id = Integer.parseInt(params[0])-1;
            if(gamelist == null || id < 0 || id >= gamelist.size()){
                throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                        "Error: game id is not valid, use list to see valid game ids" + RESET_TEXT_COLOR);
            }
            ListResult gameResult = gamelist.get(id);
            int realGameId = gameResult.gameID();
            ObserveRequest request = new ObserveRequest(realGameId);
            GameData game = server.observeGame(request, authToken);
            draw(System.out, game.game(), true);
            return "Currently watching: " + gameResult.gameName();
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Error: failed to observe game " + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String logout() throws ResponseException {
        try{
            server.logout(authToken);
            authToken = null;
            state = State.SIGNEDOUT;
            return "Successfully Logged out";

        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Error: Logout failed: " + RESET_TEXT_COLOR);

        }
    }

    public String listGames() throws ResponseException{
        assertSignedIn();

        try{
            ListofListResult result = server.listGames(authToken);
            gamelist = new ArrayList<>(result.games());
            StringBuilder sb = new StringBuilder();
            sb.append(SET_TEXT_BOLD + SET_TEXT_ITALIC +
                    WHITE_KING + "------ Current Active Games ------" + WHITE_KING +"\n" +
                    RESET_TEXT_ITALIC + RESET_TEXT_BOLD_FAINT);
            if(gamelist.isEmpty()){
                sb.append("No active games, use the create command to start\n");
            }else{
                for(int i=0; i < gamelist.size();i++){
                    var game = gamelist.get(i);
                    sb.append( WHITE_PAWN + SET_TEXT_BOLD + "Game ID: " + (i+1) + RESET_TEXT_ITALIC +
                            SET_TEXT_ITALIC + ", Game Name: " + (game.gameName()) + ",");
                    String whiteUsername = "<none>";
                    String blackUsername ="<none>";
                    if(game.whiteUsername() !=null){
                        whiteUsername = game.whiteUsername();
                    }
                    if(game.blackUsername()!=null){
                        blackUsername = game.blackUsername();
                    }
                    sb.append("\n    Players: White: " + whiteUsername + ", Black: " + blackUsername + "\n");
                }
            }
            return sb.toString();
        }catch (Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Error: failed to list games " + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String joinGame(String... params) throws ResponseException{
        if(params.length < 1){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Expected: <ID> [BLACK|WHITE]" + RESET_TEXT_COLOR);
        }
        assertSignedIn();
        try{
            int gameID = Integer.parseInt(params[0]) -1;
            if(gamelist ==null || gameID <0 ||gameID >= gamelist.size()){
                throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                        "Error: invalid game id. Use list to see valid ids" + RESET_TEXT_COLOR);
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
            return "Joined Game Successfully";
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Join failed: Expected <ID> [BLACK|WHITE]\nTry list to see joinable games" + RESET_TEXT_COLOR);
        }

    }

    public String createGame(String... params) throws ResponseException{
        if(params.length!=1){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Expected: <NAME>" + RESET_TEXT_COLOR);
        }
        assertSignedIn();

        try{
            CreateGameRequest request = new CreateGameRequest(params[0]);
            server.createGame(request, authToken);
            return String.format("Created new game: %s.\nUse 'list' to see given game ID and 'join' with that ID.", request.gameName());
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Error: failed to create game" + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String register(String... params)throws Exception{
        if(params.length !=3){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Error: Expected <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR);
        }
        RegisterRequest request = new RegisterRequest(params[0],params[1],params[2]);

        try{
            RegisterResult result = server.register(request);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            return String.format("Welcome %s\nStart playing with:\n%s", result.username(), help());
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Error: Register failed " + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String login(String... params) throws ResponseException{
        if(params.length != 2){
            throw new ResponseException(ResponseException.Code.BadRequest, SET_TEXT_COLOR_RED +
                    "Error: Expected <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR);
        }
        try{
            LoginRequest request = new LoginRequest(params[0],params[1]);
            LoginResult result = server.login(request);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            return String.format("Welcome %s\nStart playing with:\n%s", result.username(), help());
        } catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest, SET_TEXT_COLOR_RED +
                    "Login failed" + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String help() {
        if(state == State.SIGNEDOUT){
            return """
                    > ---- COMMANDS -----
                    > register <USERNAME> <PASSWORD> <EMAIL> - to register a new user
                    > login <USERNAME> <PASSWORD> - to start a game
                    > quit - exit application
                    > help - display all commands
                    """;
        }
        // user is singed in
        else{
            return """
                    > ---- COMMANDS -----
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
            throw new ResponseException(ResponseException.Code.Unauthorized, SET_TEXT_COLOR_RED +
                    "Error: Must sign in first" + RESET_TEXT_COLOR);
        }
    }
}
