package service;

import dataaccess.UnauthorizedException;
import dataaccess.UserDOA;
import dataaccess.UserMemoryDOA;
import dataaccess.UserNotFoundExecption;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {

    private LoginService service;
    private UserMemoryDOA userDao;

    @BeforeEach
    public void setup(){
        userDao = new UserMemoryDOA();
        userDao.clear();
        userDao.insertUser(new UserData("jason", "password", "groberg0@byu.edu"));
        service = new LoginService();
    }

    @Test
    @Order(1)
    @DisplayName("Valid Login is Successful")
    void loginUser() throws Exception {
        LoginRequest request = new LoginRequest("jason", "password");
        LoginResult result = service.loginUser(request);
        assertNotNull(result.authToken(), "No auth Token Returned");
        assertEquals("jason", result.username(), "Username does not match existing user");
    }

    @Test
    @Order(2)
    @DisplayName("BadLoginRequest password incorrect")
    void loginUserWrongPassword(){
        LoginRequest request = new LoginRequest("jason", "incorrect");
        assertThrows(UnauthorizedException.class, () -> service.loginUser(request), "Service should throw UnauthorizedException");
    }

    @Test
    @Order(3)
    @DisplayName("Incorrect UserName")
    void loginUserWrongUsername(){
        LoginRequest request = new LoginRequest("notJason", "password");
        assertThrows(UserNotFoundExecption.class, () -> service.loginUser(request), "Service Should throw Unauthorized Exception");
    }
}