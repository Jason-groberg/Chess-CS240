package service;
import results.ListResult;
import results.ListofListResult;
import dataaccess.*;
import model.GameData;
import java.util.ArrayList;
import java.util.Collection;

public class ListService {
    private final AuthDOA authDao;
    private final GameDOA gameDao;

    public ListService(){
        this.authDao = new AuthMemoryDOA();
        this.gameDao = new GameMemoryDAO();
    }

    public ListofListResult listGames(String authToken) throws Exception {
        try {
            if(!authDao.isAuthorized(authToken)){
                throw new UnauthorizedException("Error: unauthorized");
            }
            Collection<GameData> gameList = gameDao.listGames();
            Collection<ListResult> results = new ArrayList<>();
            gameList.forEach(game -> {
                ListResult result = new ListResult(
                        game.gameID(),
                        game.whiteUsername(),
                        game.blackUsername(),
                        game.gameName()
                );
                results.add(result);
            });
            return new ListofListResult(results);
        }
        catch (DataAccessException e){
            throw new Exception();
        }
    }
}
