package dataaccess;
import java.sql.Connection;
import java.sql.SQLException;

public class GameSqlDao {

    public GameSqlDao() throws DataAccessException{
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS gameData (
            'gameID' INT NOT NULL,
            'whiteUsername' VARCHAR(255),
            'blackUsername' VARCHAR(255),
            'gameName' VARCHAR(255) NOT NULL,
            'chessGame' VARCHAR(255) NOT NULL,
            PRIMARY KEY('gameID'),
            INDEX(type),
            INDEX(name)
            )"""
    };

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

