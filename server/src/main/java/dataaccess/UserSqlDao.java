package dataaccess;
import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSqlDao extends SqlDaoManager implements UserDOA{

    public UserSqlDao() throws DataAccessException {
        configureDatabase(createStatements);
    }
    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE userData";
        executeUpdate(statement);

    }
    @Override
    public void insertUser(UserData newUser) throws DataAccessException{
        String statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, newUser.username(), newUser.password(), newUser.email());
    }

    @Override
    public boolean containsUser(String username){
        try {
            return getUser(username) != null;
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        try(Connection conn = DatabaseManager.getConnection()){
            String statement ="SELECT username, password, email FROM userData WHERE username=?";
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return readUser(rs);
                    }
                }
            }
        } catch(Exception e){
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS userData (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )"""
    };

    private UserData readUser(ResultSet rs) throws SQLException{
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }

}
