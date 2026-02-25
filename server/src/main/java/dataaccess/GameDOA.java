package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDOA {
    void clear() throws DataAccessException;
    GameData createGame(GameData newGame) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
}
