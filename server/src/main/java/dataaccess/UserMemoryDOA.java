package dataaccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserMemoryDOA implements UserDOA {
    public static Map<String, UserData> userDatabase = new HashMap<>();
    @Override
    public void clear(){
        userDatabase.clear();
    }

    @Override
    public UserData getUser(String username) {
        return userDatabase.get(username);
    }

    @Override
    public void insertUser(UserData newUser) {
        userDatabase.put(newUser.username(), newUser);
    }

    @Override
    public boolean containsUser(String username){
        if(userDatabase.containsKey(username)){
            return true;
        }
        return false;
    }
}
