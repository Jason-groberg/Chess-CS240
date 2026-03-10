package service;
import requests.JoinGameRequest;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

public class JoinGameService {
    private final GameDOA gameDao;
    private final AuthDOA authDao;

    public JoinGameService() throws DataAccessException{
        this.gameDao = new GameSqlDao();
        this.authDao = new AuthSqlDao();
    }

    public void joinGame(String authToken, JoinGameRequest request) throws Exception{
        try{
            if(!authDao.isAuthorized(authToken)){
                throw new UnauthorizedException("Error: unauthorized");
            }
            AuthData authData = authDao.getAuth(authToken);
            String username = authData.userName();
            if(!gameDao.gameExists(request.gameID())){
                throw new GameNotFoundException("Error: no game found with given ID");
            }
            ChessGame.TeamColor playerColor = request.playerColor();
            GameData requestedGame = gameDao.getGame(request.gameID());

            if(playerColor.equals(ChessGame.TeamColor.BLACK) && requestedGame.blackUsername()==null){
                GameData blackGame = new GameData(requestedGame.gameID(), requestedGame.whiteUsername(),
                        username, requestedGame.gameName(),requestedGame.game());
                gameDao.updateGame(request.gameID(), blackGame);
            }
            else if(playerColor.equals(ChessGame.TeamColor.WHITE) && requestedGame.whiteUsername()==null){
                GameData whiteGame = new GameData(requestedGame.gameID(), username,
                        requestedGame.blackUsername(), requestedGame.gameName(),requestedGame.game());
                gameDao.updateGame(request.gameID(), whiteGame);
            }
            else{
                throw new AlreadyTakenException("Error: requested color is already taken");
            }
        }
        catch(DataAccessException e){
            throw new Exception();
        }
    }
}
