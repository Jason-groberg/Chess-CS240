package dataaccess;
import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class AuthSqlDao implements AuthDOA {

    public AuthSqlDao() throws DataAccessException{
        configureDatabase();
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
    public boolean isAuthorized(String authToken)throws DataAccessException{
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement,RETURN_GENERATED_KEYS)){
                for(int i =0; i<params.length;i++){
                    Object param = params[i];
                    if(param instanceof String p){ ps.setString(i+1, p);}
                    else if(param==null) {ps.setNull(i+1, java.sql.Types.VARCHAR);}
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
