package dataaccess;
import model.UserData;
import model.AuthData;

public class RegisterDoa {

    private String username;
    private String password;
    private String email;

    public RegisterDoa(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public model.UserData register(){
        if(DataBases.userDatabase.containsKey(username)){
            return DataBases.userDatabase.get(username);
        }
        else{
            insertNewUser();
            return DataBases.userDatabase.get(username);
        }
    }

    public void insertNewUser(){
        UserData newUser = new UserData(username, password, email);
        DataBases.userDatabase.put(username, newUser);
        String authToken = new Authorizer().generateToken();
        AuthData newAuth = new AuthData(username, authToken);
        DataBases.authDatabase.put(username, newAuth);
    }
}
