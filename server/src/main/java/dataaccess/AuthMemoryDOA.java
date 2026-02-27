package dataaccess;
import model.AuthData;
import java.util.UUID;

public class AuthMemoryDOA implements AuthDOA{
    @Override
    public void clear(){
        DataBases.authDatabase.clear();
    }

    @Override
    public String createAuth(){
        return UUID.randomUUID().toString();
    }

    @Override
    public void insertAuth(AuthData newAuthData){
        DataBases.authDatabase.put(newAuthData.authToken(), newAuthData);
    }

    @Override
    public AuthData getAuth(String token){
        return DataBases.authDatabase.get(token);
    }

    @Override
    public void deleteAuth(String token){
        DataBases.authDatabase.remove(token);
    }

    @Override
    public boolean isAuthorized(String authToken){
        if(DataBases.authDatabase.containsKey(authToken)){
            return true;
        }
        return false;
    }
}
