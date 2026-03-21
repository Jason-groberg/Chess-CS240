package facade;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.requests.LoginRequest;
import model.requests.RegisterRequest;
import model.results.CreateGameResult;
import model.results.ListofListResult;
import model.results.LoginResult;
import model.results.RegisterResult;
import ui.ResponseException;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest register) throws ResponseException{
        var request = buildRequest("POST", "/user", register, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException{
        var request = buildRequest("POST","/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(String authToken) throws ResponseException{
        var request = buildRequest("DELETE","/session",null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateGameResult createGame(CreateGameRequest createRequest,String authToken) throws ResponseException{
        var request = buildRequest("POST","/game",createRequest, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest joinRequest, String authToken) throws ResponseException{
        var request = buildRequest("PUT", "/game", joinRequest, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);

    }

    public ListofListResult listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET","/game",null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListofListResult.class);
    }


    private HttpRequest buildRequest(String method, String path, Object body, String authToken){
        var request = HttpRequest.newBuilder().uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if(body != null){
            request.setHeader("Content-Type", "application/json");
        }
        if(authToken!=null){
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request){
        if(request!=null){
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        }
        else{
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException{
        var status = response.statusCode();
        if(!isSuccessful(status)){
            var body = response.body();
            if(body!=null){
                throw ResponseException.fromJson(body, status);
            }
            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "Status: " + status);
        }
        if(responseClass != null){
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException{
        try{
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    private boolean isSuccessful(int status){
        return status/100==2;
    }
}
