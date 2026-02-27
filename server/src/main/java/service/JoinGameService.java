package service;

import Requests.JoinGameRequest;
import dataaccess.*;
import model.AuthData;
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
            Collection<String> players = gameDao.checkPlayers(request.gameID());
            if(players.contains(requestedColor)){
                throw new AlreadyTakenException("Error: requested color already taken");
            }

        }
        catch(DataAccessException e){
            throw new Exception();
        }
    }
}
