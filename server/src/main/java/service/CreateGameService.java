package service;
import requests.CreateGameRequest;
import results.CreateGameResult;
import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import java.util.Random;

public class CreateGameService {
    private final GameDOA gameDao;
    private final AuthDOA authDao;

    public CreateGameService() throws DataAccessException{
        this.gameDao = new GameSqlDao();
        this.authDao = new AuthSqlDao();
    }

    public int createGameID(){
        Random random = new Random();
        return random.nextInt(1000,9999);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws Exception{
        try{
            if(!authDao.isAuthorized(authToken)){
                throw new UnauthorizedException("Error: unauthorized");
            }
            int id = createGameID();
            GameData newGame = new GameData(id, null, null,
                    request.gameName(), new ChessGame());
            gameDao.createGame(newGame);
            return new CreateGameResult(id);

        }
        catch(DataAccessException e){
            throw new Exception();
        }
    }

}
