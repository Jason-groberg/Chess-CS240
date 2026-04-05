package server.websocket;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections;
    private final AuthDOA authDao;
    private final GameDOA gameDao;

    public WebSocketHandler() {
        try {
            connections = new ConnectionManager();
            authDao = new AuthSqlDao();
            gameDao = new GameSqlDao();
        }catch(DataAccessException e){
            throw new RuntimeException("Error: Could not initialize or connect to Database");
        }
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            String authToken = command.getAuthToken();
            String username = authDao.getAuth(authToken).userName();
            if(username == null){
                throw new UnauthorizedException("Error : Unauthorized");
            }

            Session session = ctx.session;

            switch (command.getCommandType()) {
                case CONNECT -> connect(command, username, session);
                case MAKE_MOVE -> makeMove(ctx.message(), username, session);
                case LEAVE -> leaveGame(command, username, session);
                case RESIGN -> resign(command, username, session);
            }

        }catch(DataAccessException e){
            sendError("Error: unauthorized", ctx.session);
        }
        catch (IOException ex) {
            sendError("Error: " + ex.getMessage(), ctx.session);
        }
        catch(Exception e){
            sendError("Error: " + e.getMessage(), ctx.session);
        }
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void sendError(String message, Session session) throws IOException{
        if(message==null || !message.toUpperCase().contains("ERROR")){
            message = "unexpected error";
        }
        ErrorMessage error = new ErrorMessage(message);
        String errorMessage = new Gson().toJson(error);
        session.getRemote().sendString(errorMessage);
    }

    private void connect(UserGameCommand command, String username, Session session) throws IOException {
        try{
            int gameID = command.getGameID();
            GameData game = gameDao.getGame(gameID);
            if(game==null){
                sendError("Error: no game found with given ID:  " + gameID + ".", session);
                return;
            }
            connections.add(gameID, username, session);

            LoadGameMessage loadGame = new LoadGameMessage(game.game());
            String loadGameString = new Gson().toJson(loadGame);
            session.getRemote().sendString(loadGameString);

            String notification;
            if(username.equals(game.whiteUsername())){
                notification = String.format("%s joined game as white", username);
            }
            else if(username.equals(game.blackUsername())){
                notification = String.format("%s joined game as black", username);
            }
            else{
                notification = String.format("%s joined game as an observer", username);
            }

            NotificationMessage notify = new NotificationMessage(notification);
            connections.broadcast(gameID, username, notify);

        }catch(Exception e ){
            sendError("Error: " + e.getMessage(), session);
        }
    }

    private void resign(UserGameCommand command, String username, Session session) throws IOException{
        try {
            int gameID = command.getGameID();
            GameData currGame = gameDao.getGame(gameID);
            if(currGame==null){
                sendError("Error: no game found with given ID:  " + gameID + ".", session);
                return;
            }

            if(!username.equals(currGame.whiteUsername()) && !username.equals(currGame.blackUsername())) {
                sendError("Error: only active players can resign,", session);
                return;
            }
            if(currGame.game().gameOver()){
                sendError("Error: game is already over", session);
            }

            currGame.game().setResigned(true);
            gameDao.updateGame(gameID, currGame);

            NotificationMessage notify = new NotificationMessage(username + " has resigned.");
            connections.broadcast(gameID, null, notify);
        } catch(Exception e){
            sendError("Error: " + e.getMessage(), session);
        }
    }

    private void leaveGame(UserGameCommand command, String username, Session session) throws IOException {
        try{
            int gameID = command.getGameID();
            if(!gameDao.gameExists(gameID)){
                sendError("Error: could not find game with given id", session);
            }
            GameData currGame = gameDao.getGame(gameID);
            GameData updatedGame;
            String notification;
            if(username.equals(currGame.blackUsername())){
                updatedGame = new GameData(currGame.gameID(), currGame.whiteUsername(),
                        null, currGame.gameName(), currGame.game());
                notification = String.format("%s has left the game, black side is open to join", username);
                gameDao.updateGame(gameID, updatedGame);
            }
            else if(username.equals(currGame.whiteUsername())){
                updatedGame = new GameData(currGame.gameID(), null,
                        currGame.blackUsername(), currGame.gameName(), currGame.game());
                notification = String.format("%s has left the game, white side is open to join", username);
                gameDao.updateGame(gameID, updatedGame);
            }else{
                notification = String.format("Observer %s has stopped watching", username);
            }
            NotificationMessage notify = new NotificationMessage(notification);
            connections.broadcast(gameID, username, notify);
            connections.remove(gameID, username);

        }catch(Exception e ){
            sendError("Error: "+ e.getMessage(), session);
        }
    }

    private void makeMove(String chessMoveCommand, String username, Session session){
        try{
            MoveCommand command = new Gson().fromJson(chessMoveCommand, MoveCommand.class);

            int gameID = command.getGameID();
            if(!gameDao.gameExists(gameID)){
                sendError("Error: could not find game with given id", session);
            }
            GameData currGameData = gameDao.getGame(gameID);
            ChessGame currGame = currGameData.game();
            ChessMove nextMove = command.getMove();
            ChessGame.TeamColor playerColor = currGame.getTeamTurn();

            if(command.getMove() )
        }
    }
}