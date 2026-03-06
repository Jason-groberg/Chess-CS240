package dataaccess;

import model.AuthData;


public interface AuthDOA {
    void clear() throws DataAccessException;
    void insertAuth(AuthData newAuthData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    boolean isAuthorized(String authToken) throws DataAccessException;

}
