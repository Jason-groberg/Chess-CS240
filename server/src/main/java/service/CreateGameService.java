package service;
import Requests.CreateGameRequest;
import Results.CreateGameResult;
import chess.ChessGame;
import dataaccess.*;
import model.GameData;

public class CreateGameService {
    private final GameDOA gameDao;
    private final AuthDOA authDao;

    public CreateGameService(){
        this.gameDao = new GameMemoryDAO();
        this.authDao = new AuthMemoryDOA();
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws Exception{
        try{
            if(!authDao.isAuthorized(authToken)){
                throw new UnauthorizedException("Error: unauthorized");
            }
            int Id = gameDao.createGameID();
            GameData newGame = new GameData(Id, null, null,
                    request.gameName(), new ChessGame());
            gameDao.createGame(newGame);
            return new CreateGameResult(Id);

        }
        catch(DataAccessException e){
            throw new Exception();
        }
    }

}
