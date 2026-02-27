package service;
import Requests.AuthRequest;
import Results.ListResult;
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

    public Collection<ListResult> listGames(AuthRequest request) throws Exception {
        try {
            if(!authDao.isAuthorized(request.authToken())){
                throw new UnauthorizedException("Error: unauthorized");
            }
            Collection<GameData> gameList = gameDao.listGames();
            Collection<ListResult> results = new ArrayList<>();
            gameList.forEach(game -> {
                ListResult result = new ListResult(
                        game.GameID(),
                        game.whiteUsername(),
                        game.blackUsername(),
                        game.gameName()
                );
                results.add(result);
            });
            return results;
        }
        catch (DataAccessException e){
            throw new Exception();
        }
    }
}
