package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthMemoryDOA implements AuthDOA{

    public static Map<String, AuthData> authDatabase = new HashMap<>();

    @Override
    public void clear(){authDatabase.clear();}

    @Override
    public String createAuth(){
        return UUID.randomUUID().toString();
    }

    @Override
    public void insertAuth(AuthData newAuthData){
        authDatabase.put(newAuthData.authToken(), newAuthData);
    }

    @Override
    public AuthData getAuth(String token){
        return authDatabase.get(token);
    }

    @Override
    public void deleteAuth(String token){
        authDatabase.remove(token);
    }

    @Override
    public boolean isAuthorized(String authToken){
        if(authDatabase.containsKey(authToken)){
            return true;
        }
        return false;
    }
}
