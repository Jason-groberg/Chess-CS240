package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthSqlDaoTest {

    private AuthSqlDao authDao;

    @BeforeEach
    public void setup() throws DataAccessException{
        authDao = new AuthSqlDao();
        authDao.clear();

    }

    @Test
    @DisplayName("Clear positive")
    void clear()throws DataAccessException {
        authDao.insertAuth(new AuthData("192387491273497129347912374912743917238934721389759234874923749587234985723495734", "j"));
        authDao.clear();
        assertNull(authDao.getAuth("192387491273497129347912374912743917238934721389759234874923749587234985723495734"));

    }

    @Test
    @DisplayName("Positive insert Auth")
    void insertAuth() throws DataAccessException {
        AuthData test = new AuthData("123-123-123-123", "jason");
        authDao.insertAuth(test);
        AuthData get = authDao.getAuth("123-123-123-123");
        assertEquals(get.authToken(), test.authToken());
        assertEquals(get.userName(), test.userName());

    }

    @Test
    @DisplayName("Negative insert auth")
    void insertAuthNegative()throws DataAccessException{
        AuthData bad = new AuthData("tokensss", null);
        assertThrows(DataAccessException.class, () -> authDao.insertAuth(bad), "shouldn't accept authData with Null values");


    }

    @Test
    @DisplayName("getAuthPositive")
    void getAuth()throws DataAccessException {
        AuthData test = new AuthData("123-123-123-123", "jason");
        authDao.insertAuth(test);
        AuthData authData = authDao.getAuth("123-123-123-123");
        assertNotNull(authData);
        assertEquals(authData.authToken(), test.authToken());

    }

    @Test
    @DisplayName("get Auth Negative")
    void getAuthNegative() throws DataAccessException{
        AuthData get = authDao.getAuth("fakeToken");
        assertNull(get);
    }

    @Test
    @DisplayName("Delete Auth Positive")
    void deleteAuth() throws DataAccessException{
        AuthData test = new AuthData("123-123-123-123", "jason");
        authDao.insertAuth(test);
        authDao.deleteAuth("123-123-123-123");
        assertNull(authDao.getAuth("123-123-123-123"));
    }

    @Test
    @DisplayName("Delete Auth Negative")
    void deleteAuthNegative() throws DataAccessException{
        assertDoesNotThrow( () -> authDao.deleteAuth("notAtoken"));

    }

    @Test
    @DisplayName("Is Authorized Positive")
    void isAuthorized() throws DataAccessException{
        AuthData test = new AuthData("123-123-123-123", "jason");
        authDao.insertAuth(test);
        assertTrue(authDao.isAuthorized("123-123-123-123"));
    }

    @Test
    @DisplayName("Is Authorized Negative")
    void isNotAuthorized() throws DataAccessException{
        assertFalse(authDao.isAuthorized("doesn't Exists"));
    }
}