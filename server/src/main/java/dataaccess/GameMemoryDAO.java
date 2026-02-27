package dataaccess;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class GameMemoryDAO implements GameDOA{
    @Override
    public void clear(){
        DataBases.gameDataBase.clear();
    }

    @Override
    public void createGame(GameData newGame){
        DataBases.gameDataBase.put(newGame.GameID(), newGame);
    }
    @Override
    public GameData getGame(int GameID){
        return DataBases.gameDataBase.get(GameID);
    }
    @Override
    public Collection<GameData> listGames(){
        return DataBases.gameDataBase.values();
    }
    @Override
    public void updateGame(int gameID, GameData newGame){
        DataBases.gameDataBase.remove(gameID);
        DataBases.gameDataBase.put(gameID,newGame);
    }
    @Override
    public int createGameID(){
        Random random = new Random();
        return random.nextInt(1000,100000);
    }
    @Override
    public boolean gameExists(int gameID){
        GameData game = getGame(gameID);
        if(game.GameID() == gameID){
            return true;
        }
        return false;
    }

    @Override
    public Collection<String> checkPlayers(int gameID){
        Collection<String> players = new ArrayList<>();
        GameData gameData = getGame(gameID);
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if(white != null){
            players.add("white");
        }
        else{
            players.add(null);
        }
        if(black != null){
            players.add("black");
        }
        else{
            players.add(null);
        }
        return players;
    }
}
