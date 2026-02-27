package dataaccess;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.Random;

public interface GameDOA {
    void clear() throws DataAccessException;
    void createGame(GameData newGame) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, GameData newGame) throws DataAccessException;
    int createGameID();
    boolean gameExists(int gameID) throws DataAccessException;
    Collection<String> checkPlayers(int gameID) throws DataAccessException;
}
