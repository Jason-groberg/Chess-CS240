package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSqlDaoTest {

    private UserSqlDao userDao;

    @BeforeEach
    public void setup()throws DataAccessException {
        userDao = new UserSqlDao();
        userDao.clear();
    }


    @Test
    void clear() throws DataAccessException{
        UserData test = new UserData("jason", "groberg", "groberg0@byu.edu");
        userDao.insertUser(test);
        userDao.clear();
        assertNull(userDao.getUser("jason"));
    }

    @Test
    @DisplayName("Insert User Success")
    void insertUser() throws DataAccessException {
        UserData test = new UserData("jason", "groberg", "groberg0@byu.edu");
        userDao.insertUser(test);
        assertEquals(test, userDao.getUser("jason"));
    }

    @Test
    @DisplayName("Negative User Login")
    void insertUserNegative() throws DataAccessException{
        UserData test = new UserData("jason", "groberg", "groberg0@byu.edu");
        userDao.insertUser(test);
        assertThrows(DataAccessException.class, () -> userDao.insertUser(test), "Should throw DataAccessError");
    }

    @Test
    @DisplayName("Contains user Negative")
    void containsUserNegative() {
        assertFalse(userDao.containsUser("notInDatabase"), "should return false, userName is not in Database");
    }

    @Test
    @DisplayName("Contains User Negative")
    void containsUser() throws DataAccessException {
        userDao.insertUser(new UserData("jason","test","asdlgkja;dg"));
        assertTrue(userDao.containsUser("jason"));
    }

    @Test
    @DisplayName("Positive Get user")
    void getUser() throws DataAccessException{
        UserData test = new UserData("jason", "groberg", "groberg0@byu.edu");
        userDao.insertUser(test);
        UserData user = userDao.getUser("jason");
        assertNotNull(user);
        assertEquals("jason", user.username());
        assertEquals("groberg", user.password());
        assertEquals("groberg0@byu.edu", user.email());
    }

    @Test
    @DisplayName("Negative get user")
    void getUserNegative() throws DataAccessException{
        UserData notAUser = userDao.getUser("somebody");
        assertNull(notAUser);
    }
}