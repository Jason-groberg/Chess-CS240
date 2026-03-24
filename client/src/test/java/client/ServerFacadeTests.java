package client;
import chess.ChessGame;
import facade.ServerFacade;
import model.requests.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ResponseException;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    public static ServerFacade facade;
    private static RegisterRequest newUser;
    private static RegisterRequest existingUser;
    private String existingAuth;



    @BeforeAll
    public static void init() throws Exception{
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        existingUser = new RegisterRequest("jason", "password","@gmail");


    }

    @BeforeEach
    public void setup() throws Exception{
        facade.clearDatabases();
        existingAuth = facade.register(existingUser).authToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    @DisplayName("register new user")
    public void registerNewUser() throws Exception {
        RegisterRequest aUser = new RegisterRequest("nico","nico","nico");
        var result = facade.register(aUser);
        assertNotNull(result);
        assertEquals("nico", result.username());
    }

    @Test
    @DisplayName("Register Bad Request")
    public void registerBadRequest()throws Exception{
        RegisterRequest aUser = new RegisterRequest("jason","jason","@gmail");
        assertThrows(ResponseException.class, () -> facade.register(aUser),
                "Should throw Already Taken Exception");
    }

    @Test
    @DisplayName("Login Bad Request")
    public void loginBadRequest() throws Exception{
        LoginRequest aUser = new LoginRequest("jason", "passwordd");
        assertThrows(ResponseException.class, () -> facade.login(aUser),
                "Password is incorrect, unauthorized Exception");
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() throws Exception{
        LoginRequest aUser = new LoginRequest("jason", "password");
        assertDoesNotThrow(() -> facade.login(aUser));

    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess()throws Exception{
        assertDoesNotThrow(() -> facade.logout(existingAuth));
    }

    @Test
    @DisplayName("logout unauthorized")
    public void logoutUnauthorized()throws  Exception {
        assertThrows(ResponseException.class, () -> facade.logout("123457681234"));
    }

    @Test
    @DisplayName("create Game Success")
    public void createGameSuccess()throws Exception{
        CreateGameRequest newGame = new CreateGameRequest("coolGame1");
        var result = facade.createGame(newGame, existingAuth);
        assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("create game failure")
    public void createGameFailure() throws Exception{
        CreateGameRequest newGame = new CreateGameRequest("coolGame1");
        assertThrows(ResponseException.class, () -> facade.createGame(newGame,"123"),
                "should throw response error, unauthorized");
    }

    @Test
    @DisplayName("Join Game Successfully")
    public void joinGameSuccess() throws Exception {
        CreateGameRequest newGame = new CreateGameRequest("coolGame1");
        var result = facade.createGame(newGame, existingAuth);
        JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, result.gameID());
        assertDoesNotThrow( () -> facade.joinGame(joinRequest, existingAuth));

    }

    @Test
    @DisplayName("Requested color is already taken")
    public void joinGameAlreadyTaken() throws Exception{
        CreateGameRequest newGame = new CreateGameRequest("coolGame1");
        var result = facade.createGame(newGame, existingAuth);
        JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, result.gameID());
        assertDoesNotThrow( () -> facade.joinGame(joinRequest, existingAuth));
        JoinGameRequest joinRequest2 = new JoinGameRequest(ChessGame.TeamColor.WHITE, result.gameID());
        assertThrows(ResponseException.class, () -> facade.joinGame(joinRequest2, existingAuth));
    }

    @Test
    @DisplayName("List Games")
    public void listGamesSuccess() throws Exception{
        CreateGameRequest newGame = new CreateGameRequest("coolGame1");
        facade.createGame(newGame, existingAuth);
        CreateGameRequest newGame2 = new CreateGameRequest("coolGame2");
        facade.createGame(newGame2, existingAuth);
        var result = facade.listGames(existingAuth);
        assertEquals(2, result.games().size());
    }

    @Test
    @DisplayName("List Games Negative")
    public void listGamesNegative() throws Exception{
        CreateGameRequest newGame = new CreateGameRequest("coolGame1");
        facade.createGame(newGame, existingAuth);
        CreateGameRequest newGame2 = new CreateGameRequest("coolGame2");
        facade.createGame(newGame2, existingAuth);
        assertThrows(ResponseException.class, () -> facade.listGames("not a valid token"),"should throw response Exception");

    }

    @Test
    @DisplayName("Observe active Game")
    public void observeActiveGame() throws Exception{
        //white player
        CreateGameRequest newGame = new CreateGameRequest("coolGame1");
        var result = facade.createGame(newGame, existingAuth);
        facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, result.gameID()), existingAuth);

        //observer
        var game = facade.observeGame(new ObserveRequest(result.gameID()), existingAuth);
        assertNotNull(game);
        assertEquals("coolGame1", game.gameName());
        assertEquals("jason", game.whiteUsername());
        assertNull(game.blackUsername());

    }

    @Test
    @DisplayName("Observe Game Negative")
    public void observeGameNegative() throws Exception{
        CreateGameRequest newGame = new CreateGameRequest("coolGame1");
        var result = facade.createGame(newGame, existingAuth);
        facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, result.gameID()), existingAuth);

        assertThrows (ResponseException.class, () ->
                facade.observeGame(new ObserveRequest(result.gameID()), "123"));
    }
}
