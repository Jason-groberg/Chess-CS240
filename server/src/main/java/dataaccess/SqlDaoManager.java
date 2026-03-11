package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public abstract class SqlDaoManager {

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement,RETURN_GENERATED_KEYS)){
                for(int i =0; i<params.length;i++){
                    Object param = params[i];
                    if(param instanceof Integer p){ ps.setInt(i+1, p);}
                    else if(param instanceof String p){ ps.setString(i+1, p);}
                    else if(param instanceof ChessGame p){ ps.setString(i+1, new Gson().toJson(p));}
                    else if(param==null){ ps.setNull(i+1, java.sql.Types.VARCHAR);}
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){return rs.getInt(1);}
            }
            return 0;
        } catch(SQLException e){
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    protected void configureDatabase(String[] createStatements) throws DataAccessException{
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
