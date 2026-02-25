package dataaccess;
import model.UserData;
import model.AuthData;

public class UserMemoryDOA implements UserDOA {
    @Override
    public void clear(){
        DataBases.userDatabase.clear();
    }

    @Override
    public UserData getUser(String username) {
        return DataBases.userDatabase.get(username);
    }

    @Override
    public void insertUser(UserData newUser) {
        DataBases.userDatabase.put(newUser.username(), newUser);
    }
}
