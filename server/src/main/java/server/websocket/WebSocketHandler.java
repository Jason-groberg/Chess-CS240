package server.websocket;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import java.io.IOException;
import java.util.Collection;

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
            AuthData authData = authDao.getAuth(authToken);
            if(authData == null){
                sendError("Error: unauthorized", ctx.session);
                return;
            }

            String username = authData.userName();
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
                notification = String.format("Notification: %s joined game as white", username);
            }
            else if(username.equals(game.blackUsername())){
                notification = String.format("Notification: %s joined game as black", username);
            }
            else{
                notification = String.format("Notification: %s joined game as an observer", username);
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
                return;
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
                return;
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

    private void makeMove(String chessMoveCommand, String username, Session session) throws IOException{
        try{

            MoveCommand command = new Gson().fromJson(chessMoveCommand, MoveCommand.class);
            int gameID = command.getGameID();
            if(!gameDao.gameExists(gameID)){
                sendError("Error: could not find game with given id", session);
                return;
            }

            GameData currGameData = gameDao.getGame(gameID);
            ChessGame currGame = currGameData.game();
            ChessMove nextMove = command.getMove();

            Collection<ChessMove> legalMoves = currGame.validMoves(nextMove.getStartPosition());
            if(!legalMoves.contains(nextMove)) {
                sendError("Error requested move is illegal.\n" +
                        "Use 'highlight' <piecePosition> to see all legal moves", session);
                return;
            }
            if(currGame.gameOver()){
                sendError("Error: game is over",session);
                return;
            }
            ChessGame.TeamColor playerColor = null;
            String enemyPlayer = "his enemy";
            if(currGameData.blackUsername().equals(username)){
                playerColor = ChessGame.TeamColor.BLACK;
                enemyPlayer = currGameData.whiteUsername();
            }
            if(currGameData.whiteUsername().equals(username)){
                playerColor = ChessGame.TeamColor.WHITE;
                enemyPlayer = currGameData.blackUsername();
            }
            if(playerColor == null){
                sendError("Error: observers can not make moves", session);
                return;
            }
            if(playerColor != currGame.getTeamTurn()){
                sendError("Error: not your turn", session);
                return;
            }
            if(playerColor  != currGame.getBoard().getPiece(nextMove.getStartPosition()).getTeamColor()){
                sendError("Error: not your piece.",session);
                return;
            }

            currGame.makeMove(nextMove);
            gameDao.updateGame(gameID, currGameData);

            String notification;
            String start = parseMove(nextMove.getStartPosition());
            String end = parseMove(nextMove.getEndPosition());
            connections.broadcast(gameID, null, new LoadGameMessage(currGame));
            notification = String.format("Notification: %s has made his move %s-%s.", username,start,end);

            //Check/checkmate/stalemate notifications
            if(currGame.isInCheckmate(currGame.getTeamTurn())) {
                connections.broadcast(gameID, username, new NotificationMessage(notification));
                notification = String.format("Checkmate! %s has won, sorry %s. :(", username, enemyPlayer);
                connections.broadcast(gameID, null, new NotificationMessage(notification));
            }
            else if(currGame.isInCheck(currGame.getTeamTurn())){
                connections.broadcast(gameID, username, new NotificationMessage(notification));
                notification = String.format("%s has placed %s in check.", username, enemyPlayer);
                connections.broadcast(gameID, null, new NotificationMessage(notification));
            }
            else if(currGame.isInStalemate(currGame.getTeamTurn())){
                connections.broadcast(gameID, username, new NotificationMessage(notification));
                notification = String.format("Woah that's stalemate good job %s I hope you weren't winning," +
                        " now %s can't move and NO ONE WINS NOW ;)", username, enemyPlayer);
                connections.broadcast(gameID, null, new NotificationMessage(notification));
            }else{
                connections.broadcast(gameID, username, new NotificationMessage(notification));
            }

        }
        catch(Exception e){
            sendError("Error: " + e.getMessage(), session);
        }
    }
    private String parseMove(ChessPosition pos){
        char col = (char) ('a' + pos.getColumn() -1);
        int row = pos.getRow();
        return String.valueOf(col) + row;
    }
}