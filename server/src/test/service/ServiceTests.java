package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.*;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTests {
    private AuthMemoryDOA authDao;
    private GameDOA gameDao;
    private UserMemoryDOA userDao;

    @BeforeEach
    public void setup() {
        userDao = new UserMemoryDOA();
        gameDao = new GameMemoryDAO();
        authDao = new AuthMemoryDOA();
        userDao.clear();
        authDao.clear();
        gameDao.clear();
        userDao.insertUser(new UserData("jason", "password", "groberg0@byu.edu"));

    }

    @Test
    @Order(1)
    @DisplayName("Valid Login is Successful")
    void loginUser() throws Exception {
        LoginService service = new LoginService();
        LoginRequest request = new LoginRequest("jason", "password");
        LoginResult result = service.loginUser(request);
        assertNotNull(result.authToken(), "No auth Token Returned");
        assertEquals("jason", result.username(), "Username does not match existing user");
    }

    @Test
    @Order(2)
    @DisplayName("BadLoginRequest password incorrect")
    void loginUserWrongPassword(){
        LoginService service = new LoginService();
        LoginRequest request = new LoginRequest("jason", "incorrect");
        assertThrows(UnauthorizedException.class, () -> service.loginUser(request), "Service should throw UnauthorizedException");
    }

    @Test
    @Order(3)
    @DisplayName("Incorrect UserName")
    void loginUserWrongUsername(){
        LoginService service = new LoginService();
        LoginRequest request = new LoginRequest("notJason", "password");
        assertThrows(UserNotFoundExecption.class, () -> service.loginUser(request), "Service Should throw Unauthorized Exception");
    }

    @Test
    @Order(4)
    @DisplayName("Register Success")
    void registerUserSuccess() throws Exception {
        RegisterService service = new RegisterService();
        RegisterRequest request = new RegisterRequest("newUser","newPassword","newEmail");
        RegisterResult existingUser = service.registerUser(request);
        assertEquals(request.username(), existingUser.username());

    }

    @Test
    @Order(5)
    @DisplayName("Register Existing User")
    void registerExistingUser() {
        RegisterService service = new RegisterService();
        RegisterRequest request = new RegisterRequest("jason", "anotherPassword","anotherEmail");
        assertThrows(AlreadyTakenException.class, () -> service.registerUser(request), "Should throw Already Taken Exception");
    }

    @Test
    @Order(6)
    @DisplayName("Logout Success")
    void logoutSuccess()throws Exception{
        LogoutService service = new LogoutService();
        RegisterService regService = new RegisterService();
        RegisterResult regResult = regService.registerUser(new RegisterRequest("User", "number","someEmail"));
        String authToken = regResult.authToken();
        service.logoutUser(authToken);
        assertFalse(authDao.isAuthorized(authToken));
    }

    @Test
    @Order(7)
    @DisplayName("Logout Unauthorized")
    void unauthorizedLogout() throws Exception{
        RegisterService regService = new RegisterService();
        regService.registerUser(new RegisterRequest("User", "number","someEmail"));
        String randomAuth = authDao.createAuth();
        LogoutService service = new LogoutService();
        assertThrows(UnauthorizedException.class, () -> service.logoutUser(randomAuth), "Should throw Unauthorized Exception");
    }

    @Test
    @Order(8)
    @DisplayName("List Games Success")
    void listGamesSuccess()throws Exception{
        gameDao.createGame(new GameData(1234, "whitePlayer", "blackPlayer", "coolGame", new ChessGame()));
        ListService service = new ListService();
        String randomAuth = authDao.createAuth();
        authDao.insertAuth(new AuthData(randomAuth, "user"));
        ListofListResult gameList = service.listGames(randomAuth);
        assertFalse(gameList.games().isEmpty());
    }

    @Test
    @Order(9)
    @DisplayName("List Game is Unauthorized")
    void unauthorizedList()throws Exception{
        gameDao.createGame(new GameData(1234, "whitePlayer", "blackPlayer", "coolGame", new ChessGame()));
        ListService service = new ListService();
        String randomAuth = authDao.createAuth();
        assertThrows(UnauthorizedException.class, () -> service.listGames(randomAuth));
    }

    @Test
    @Order(10)
    @DisplayName("Create New Game")
    void createNewGameSuccess()throws Exception{
        String randomAuth = authDao.createAuth();
        authDao.insertAuth(new AuthData(randomAuth, "user"));
        CreateGameService service = new CreateGameService();
        CreateGameResult result = service.createGame(new CreateGameRequest("newGame"), randomAuth);
        assertTrue(gameDao.gameExists(result.gameID()));
    }

    @Test
    @Order(11)
    @DisplayName("Created Game is Unauthorized")
    void createGameUnauthorized(){
        String auth = authDao.createAuth();
        authDao.insertAuth(new AuthData(auth, "user"));
        CreateGameService service = new CreateGameService();
        assertThrows(UnauthorizedException.class, () -> service.createGame(new CreateGameRequest("coolCame1"), null));
    }

    @Test
    @Order(12)
    @DisplayName("Join Game Success")
    void joinGameSuccess()throws Exception{
        RegisterService registerService = new RegisterService();
        RegisterResult result = registerService.registerUser(new RegisterRequest("whitePlayer", "6","eSnail"));
        String authToken = result.authToken();

        gameDao.createGame(new GameData(1234, null, "blackPlayer", "coolGame", new ChessGame()));
        JoinGameService service = new JoinGameService();
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1234);
        service.joinGame(authToken, request);
        GameData joinedGame = gameDao.getGame(1234);
        assertEquals(joinedGame.whiteUsername(), "whitePlayer");
    }

    @Test
    @Order(13)
    @DisplayName("Requested Color is Taken")
    void requestedColorIsTaken()throws Exception{
        RegisterService registerService = new RegisterService();
        RegisterResult result = registerService.registerUser(new RegisterRequest("whitePlayer", "6","eSnail"));
        String authToken = result.authToken();

        gameDao.createGame(new GameData(1234, null, "blackPlayer", "coolGame", new ChessGame()));
        JoinGameService service = new JoinGameService();
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1234);
        assertThrows(AlreadyTakenException.class, () -> service.joinGame(authToken, request), "Should throw Already Taken Exception");

    }
}