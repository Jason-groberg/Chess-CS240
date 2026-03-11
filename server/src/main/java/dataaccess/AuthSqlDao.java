package dataaccess;
import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AuthSqlDao extends SqlDaoManager implements AuthDOA {

    public AuthSqlDao() throws DataAccessException{
        configureDatabase(createStatements);
    }

    @Override
    public void clear() throws DataAccessException{
        String statement = "TRUNCATE authData";
        executeUpdate(statement);
    }

    @Override
    public void insertAuth(AuthData newAuth)throws DataAccessException{
        String statement = "INSERT INTO authData (authToken, username) VALUES (?,?)";
        executeUpdate(statement, newAuth.authToken(), newAuth.userName());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        try(Connection conn = DatabaseManager.getConnection()){
            String statement = "SELECT authToken, username FROM authData WHERE authToken=?";
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1,authToken);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return readAuth(rs);
                    }
                }
            }
        }catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken)throws DataAccessException{
        String statement = "DELETE FROM authData WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public boolean isAuthorized(String authToken){
        try{
            return getAuth(authToken) !=null;
        } catch (DataAccessException e) {
            return false;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS authData (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            )
            """
    };

    private AuthData readAuth(ResultSet rs)throws SQLException{
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);
    }
}
