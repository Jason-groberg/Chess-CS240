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

    public State getState(){
        return state;
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
                case "quit" -> "quit";
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
                    "Error: command 'observe' got unexpected fields,\n" +
                    "Expected: <ID>" + RESET_TEXT_COLOR);
        }
        assertSignedIn();

        try{
            int id = Integer.parseInt(params[0])-1;
            if(gamelist == null || id < 0 || id >= gamelist.size()){
                throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                        "Observe game failed\n" +
                        "Error: given ID is not valid.\n" + RESET_TEXT_COLOR + SET_TEXT_COLOR_BLUE +
                        "Try using 'list' to see valid game ID's" + RESET_TEXT_COLOR);
            }
            ListResult gameResult = gamelist.get(id);
            int realGameId = gameResult.gameID();
            ObserveRequest request = new ObserveRequest(realGameId);
            GameData game = server.observeGame(request, authToken);
            draw(System.out, game.game(), true);
            return "Currently watching: " + gameResult.gameName();
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Observe failed.\n" +
                    "Error: failed to observe game " + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String logout() throws ResponseException {
        try{
            server.logout(authToken);
            authToken = null;
            state = State.SIGNEDOUT;
            return "Successfully Logged out\n";

        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest, SET_TEXT_COLOR_RED +
                    "Logout failed.\n" +
                    "Error: " + e.getMessage() + RESET_TEXT_COLOR);
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
                    "List failed.\n" +
                    "Error: failed to list games " + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String joinGame(String... params) throws ResponseException{
        if(params.length < 1){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Error: command 'join' got unexpected fields\n" +
                    "Expected: <ID> [BLACK|WHITE]" + RESET_TEXT_COLOR);
        }
        assertSignedIn();
        try{
            int gameID = Integer.parseInt(params[0]) -1;
            if(gamelist ==null || gameID <0 ||gameID >= gamelist.size()){
                throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                        "Error: field <ID> got invalid ID.\n" + RESET_TEXT_COLOR +
                        SET_TEXT_COLOR_BLUE + "Use command 'list' to see valid ids" + RESET_TEXT_COLOR);
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
            return "Joined Game Successfully.";
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Join failed.\n"
                    + "Error:" + e.getMessage() + RESET_TEXT_COLOR);
        }

    }

    public String createGame(String... params) throws ResponseException{
        if(params.length!=1){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Error: command 'create' got unexpected fields.\n" +
                    "Expected: <NAME>" + RESET_TEXT_COLOR);
        }
        assertSignedIn();

        try{
            CreateGameRequest request = new CreateGameRequest(params[0]);
            server.createGame(request, authToken);
            return String.format("Created new game: %s.\nUse 'list' to see given game ID and 'join' with that ID.", request.gameName());
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Failed to create game.\n"
                    + "Error: " + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String register(String... params)throws Exception{
        if(params.length !=3){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Error: command 'register' got unexpected fields\n" +
                    "Expected: <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR);
        }
        RegisterRequest request = new RegisterRequest(params[0],params[1],params[2]);

        try{
            RegisterResult result = server.register(request);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            return String.format( WHITE_PAWN +"Welcome %s" + WHITE_PAWN + "\nStart playing with:\n%s", result.username(), help());
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,SET_TEXT_COLOR_RED +
                    "Register failed\n" + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String login(String... params) throws ResponseException{
        if(params.length != 2){
            throw new ResponseException(ResponseException.Code.BadRequest, SET_TEXT_COLOR_RED +
                    "Error: command 'login' got unexcepted fields\n" +
                    "Expected: <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR);
        }
        try{
            LoginRequest request = new LoginRequest(params[0],params[1]);
            LoginResult result = server.login(request);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            return String.format(WHITE_PAWN +"Welcome %s\nStart playing with:\n%s!" + WHITE_PAWN, result.username(), help());
        } catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest, SET_TEXT_COLOR_RED +
                    "Login failed\n" + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public String help() {
        if(state == State.SIGNEDOUT){
            return
                WHITE_PAWN + "---- COMMANDS -----" + WHITE_PAWN + "\n" +
                SET_TEXT_COLOR_BLUE + "> register <USERNAME> <PASSWORD> <EMAIL> " + RESET_TEXT_COLOR + SET_TEXT_ITALIC + "-to register a new user.\n" + RESET_TEXT_ITALIC +
                SET_TEXT_COLOR_BLUE + "> login <USERNAME> <PASSWORD> " + RESET_TEXT_COLOR + SET_TEXT_ITALIC + "-to start a game.\n" + RESET_TEXT_ITALIC +
                SET_TEXT_COLOR_BLUE + "> quit " + RESET_TEXT_COLOR + SET_TEXT_ITALIC+ "-to exit application.\n" + RESET_TEXT_ITALIC +
                SET_TEXT_COLOR_BLUE + "> help " + RESET_TEXT_COLOR + SET_TEXT_ITALIC + "-to display all commands." + RESET_TEXT_ITALIC;
        }
        // user is singed in
        else{
            return
                WHITE_PAWN + "---- COMMANDS -----" + WHITE_PAWN + "\n" +
                SET_TEXT_COLOR_BLUE + "> create <NAME> " + RESET_TEXT_COLOR + SET_TEXT_ITALIC + "-to create a new game.\n" + RESET_TEXT_ITALIC +
                SET_TEXT_COLOR_BLUE + "> join <ID> " + RESET_TEXT_COLOR + SET_TEXT_ITALIC + "-to join a game, use 'list' to see active games.\n" + RESET_TEXT_ITALIC +
                SET_TEXT_COLOR_BLUE + "> observe <ID> " + RESET_TEXT_COLOR + SET_TEXT_ITALIC + "-to observe an active game.\n" + RESET_TEXT_ITALIC +
                SET_TEXT_COLOR_BLUE + "> list " + RESET_TEXT_COLOR + SET_TEXT_ITALIC + "-to see all active games and ID's.\n" + RESET_TEXT_ITALIC +
                SET_TEXT_COLOR_BLUE + "> logout " + RESET_TEXT_COLOR + SET_TEXT_ITALIC + "-to logout of application.\n" + RESET_TEXT_ITALIC +
                SET_TEXT_COLOR_BLUE + "> quit " + RESET_TEXT_COLOR + SET_TEXT_ITALIC+ "-to exit application.\n" + RESET_TEXT_ITALIC +
                SET_TEXT_COLOR_BLUE + "> help " + RESET_TEXT_COLOR + SET_TEXT_ITALIC + "-to display all commands." + RESET_TEXT_ITALIC;
        }
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT){
            throw new ResponseException(ResponseException.Code.Unauthorized, SET_TEXT_COLOR_RED +
                    "Error: Must sign in first" + RESET_TEXT_COLOR);
        }
    }
}
