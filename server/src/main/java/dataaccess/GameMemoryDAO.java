package dataaccess;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class GameMemoryDAO implements GameDOA{
    public static Map<Integer, GameData> gameDataBase = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        gameDataBase.clear();
    }

    @Override
    public void createGame(GameData newGame){
        gameDataBase.put(newGame.gameID(), newGame);
    }
    @Override
    public GameData getGame(int gameID){
        return gameDataBase.get(gameID);
    }
    @Override
    public Collection<GameData> listGames(){
        return gameDataBase.values();
    }
    @Override
    public void updateGame(int gameID, GameData newGame){
        gameDataBase.remove(gameID);
        gameDataBase.put(gameID,newGame);
    }
    @Override
    public boolean gameExists(int gameID){
        return gameDataBase.containsKey(gameID);
    }
}
