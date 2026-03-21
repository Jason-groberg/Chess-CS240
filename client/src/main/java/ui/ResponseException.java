package ui;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        BadRequest,
        Unauthorized,
        Forbidden,
        NotFound,
        ServerError
    }
    private final Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public static ResponseException fromJson(String json, int status){
        try{
            Map map = new Gson().fromJson(json, Map.class);
            String message = map.get("message").toString();
            return new ResponseException(fromHttpStatusCode(status),message);
        }catch(Exception e){
            return new ResponseException(fromHttpStatusCode(status), "Error" + json);

        }
    }

    public static Code fromHttpStatusCode(int status){
        return switch(status){
            case 400 -> Code.BadRequest;
            case 401 -> Code.Unauthorized;
            case 403 -> Code.Forbidden;
            case 404 -> Code.NotFound;
            default -> Code.ServerError;
        };
    }
    public Code getCode(){
        return code;
    }
}
