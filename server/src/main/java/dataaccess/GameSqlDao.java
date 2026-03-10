package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class GameSqlDao implements GameDOA {

    public GameSqlDao() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE gameData";
        executeUpdate(statement);
    }

    @Override
    public void createGame(GameData newGame) throws DataAccessException {
        String statement = "INSERT INTO gameData (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?,?,?,?,?)";
        executeUpdate(statement, newGame.gameID(), newGame.whiteUsername(),
                newGame.blackUsername(), newGame.gameName(), newGame.game());

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        try(Connection conn = DatabaseManager.getConnection()){
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM gameData WHERE gameID=?";
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1,gameID);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return readGame(rs);
                    }
                }
            }
        }catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }


    @Override
    public Collection<GameData> listGames() throws DataAccessException{
        Collection<GameData> games = new ArrayList<>();
        try(Connection conn = DatabaseManager.getConnection()){
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM gameData";
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    games.add(readGame(rs));
                }
            }
        }catch(SQLException e ){
                throw new DataAccessException(e.getMessage());
        }
        return games;
    }

    @Override
    public void updateGame(int gameID, GameData newGame) throws DataAccessException{
        String statement = "UPDATE gameData SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?";
        executeUpdate(statement, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), newGame.game());
    }

    @Override
    public boolean gameExists(int gameID) throws DataAccessException{
        try{
            return getGame(gameID) !=null;
        }catch(DataAccessException e) {
            return false;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS gameData (
            gameID INT NOT NULL,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255) NOT NULL,
            chessGame TEXT NOT NULL,
            PRIMARY KEY(gameID)
            )"""
    };

    private GameData readGame(ResultSet rs) throws SQLException{
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String json = rs.getString("chessGame");
        ChessGame chessGame = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement,RETURN_GENERATED_KEYS)){
                for(int i =0; i<params.length;i++){
                    Object param = params[i];
                    if(param instanceof Integer p) ps.setInt(i+1, p);
                    else if(param instanceof String p) ps.setString(i+1, p);
                    else if(param instanceof ChessGame p) ps.setString(i+1, p.toString());
                    else if(param==null) ps.setNull(i+1, java.sql.Types.VARCHAR);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){return rs.getInt(1);}
            }
            return 0;
        } catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(Connection conn = DatabaseManager.getConnection()){
            for (String statement : createStatements){
                try(var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch(SQLException e){
            throw new DataAccessException("Unable to configure Database");
        }
    }
}

