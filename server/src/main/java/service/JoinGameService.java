package service;

import Requests.JoinGameRequest;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class JoinGameService {
    private final GameDOA gameDao;
    private final AuthDOA authDao;

    public JoinGameService(){
        this.gameDao = new GameMemoryDAO();
        this.authDao = new AuthMemoryDOA();
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
            String requestedColor = request.playerColor();
            GameData requestedGame = gameDao.getGame(request.gameID());
            Collection<String> players = gameDao.checkPlayers(request.gameID());
            if(players.contains(requestedColor)){
                throw new AlreadyTakenException("Error: requested color already taken");
            }
            if(requestedColor.equalsIgnoreCase("black")){
                GameData blackGame = new GameData(requestedGame.GameID(), requestedGame.whiteUsername(),
                        username, requestedGame.gameName(),requestedGame.game());
                gameDao.updateGame(request.gameID(), blackGame);
            }
            else{
                GameData whiteGame = new GameData(requestedGame.GameID(), username,
                        requestedGame.blackUsername(), requestedGame.gameName(),requestedGame.game());
                gameDao.updateGame(request.gameID(), whiteGame);
            }

        }
        catch(DataAccessException e){
            throw new Exception();
        }
    }
}
