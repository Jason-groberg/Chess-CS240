package dataaccess;
import model.GameData;
import java.util.Collection;

public interface GameDOA {
    void clear() throws DataAccessException;
    void createGame(GameData newGame) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, GameData newGame) throws DataAccessException;
    boolean gameExists(int gameID) throws DataAccessException;
}
