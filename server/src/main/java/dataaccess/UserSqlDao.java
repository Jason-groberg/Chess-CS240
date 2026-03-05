package dataaccess;
import java.sql.Connection;
import java.sql.SQLException;

public class UserSqlDao {

    public UserSqlDao() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS userData (
            'username' VARCHAR(255) NOT NULL,
            'password' VARCHAR(255) NOT NULL,
            'email' VARCHAR(255) NOT NULL,
            PRIMARY KEY (username),
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
