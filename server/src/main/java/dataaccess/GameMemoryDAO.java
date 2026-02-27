package dataaccess;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameMemoryDAO implements GameDOA{
    public static Map<Integer, GameData> gameDataBase = new HashMap<>();

    @Override
    public void clear(){
        gameDataBase.clear();
    }

    @Override
    public void createGame(GameData newGame){
        gameDataBase.put(newGame.GameID(), newGame);
    }
    @Override
    public GameData getGame(int GameID){
        return gameDataBase.get(GameID);
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
    public int createGameID(){
        Random random = new Random();
        return random.nextInt(1000,100000);
    }
    @Override
    public boolean gameExists(int gameID){
        if(gameDataBase.containsKey(gameID)){
            return true;
        }
        return false;
    }
}
