package dataaccess;

import model.UserData;


public interface UserDOA {
    void clear() throws DataAccessException;
    void insertUser(UserData newUser) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean containsUser(String username);

}
