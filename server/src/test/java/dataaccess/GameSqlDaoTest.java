package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameSqlDaoTest {
    private GameSqlDao gameDao;

    @BeforeEach
    public void setup()throws DataAccessException{
        gameDao = new GameSqlDao();
        gameDao.clear();
    }

    @Test
    void clear()throws DataAccessException {
        GameData test = new GameData(1234, "white", "black", "chessGame", new ChessGame());
        gameDao.createGame(test);
        gameDao.clear();
        assertNull(gameDao.getGame(1234));

    }

    @Test
    @DisplayName("create Game positive")
    void createGame()throws DataAccessException {
        GameData test = new GameData(1234, "white", "black", "chessGame", new ChessGame());
        assertDoesNotThrow( () -> gameDao.createGame(test));
        GameData sameGame = gameDao.getGame(1234);
        assertEquals("chessGame", sameGame.gameName());
        assertEquals(new ChessGame(), sameGame.game());
    }

    @Test
    @DisplayName("createGame negative")
    void createGameNegative() throws DataAccessException{
        GameData badGame = new GameData(0, null, null, "someGame", new ChessGame());
        gameDao.createGame(badGame);
        assertThrows(DataAccessException.class, () -> gameDao.createGame(badGame), "Game with null id (0) can not be created");
    }

    @Test
    @DisplayName("get Game Positive")
    void getGame() throws DataAccessException {
        GameData test = new GameData(1234, "white", "black", "chessGame", new ChessGame());
        gameDao.createGame(test);
        GameData sameGame = gameDao.getGame(1234);
        assertNotNull(sameGame);
        assertEquals("white", sameGame.whiteUsername());
        assertNotNull(sameGame.game());
    }

    @Test
    @DisplayName("Get Game Negative")
    void getGameNegative()throws DataAccessException{
        assertNull(gameDao.getGame(123456), "game does not exist");

    }

    @Test
    @DisplayName("list game Positive")
    void listGames() throws DataAccessException {
        GameData test = new GameData(1234, "white", "black", "chessGame", new ChessGame());
        gameDao.createGame(test);
        GameData test1 = new GameData(1235, "white", "black", "chessGame", new ChessGame());
        gameDao.createGame(test1);
        GameData test2 = new GameData(1236, "white", "black", "chessGame", new ChessGame());
        gameDao.createGame(test2);
        Collection<GameData> gameList = gameDao.listGames();
        assertEquals(3, gameList.size());
    }

    @Test
    @DisplayName("List game Negative")
    void listGameNegative() throws DataAccessException{
        Collection<GameData> gameList = gameDao.listGames();
        assertTrue(gameList.isEmpty());
    }

    @Test
    @DisplayName("update Game Positive")
    void updateGame() throws DataAccessException {
        gameDao.createGame(new GameData(1234, null, null, "Joinable Game", new ChessGame()));
        GameData updatedGame = new GameData(1234, "jason", null, "Joinable Game", new ChessGame());
        gameDao.updateGame(1234, updatedGame);
        GameData sameGame = gameDao.getGame(1234);
        assertEquals("jason", sameGame.whiteUsername());
    }

    @Test
    @DisplayName("Update Game negative")
    void updateGameNegative(){
        assertDoesNotThrow(()-> gameDao.updateGame(1111, new GameData(1111,null,"someone", "NotAGame", new ChessGame() )));

    }

    @Test
    @DisplayName("game Exists Positive")
    void gameExists() throws DataAccessException{
        GameData test = new GameData(1234, "white", "black", "chessGame", new ChessGame());
        gameDao.createGame(test);
        assertTrue(gameDao.gameExists(1234));
    }

    @Test
    @DisplayName("Game Exists Negative")
    void gameExistsNegative() throws DataAccessException{
        assertFalse(gameDao.gameExists(7777));
    }
}