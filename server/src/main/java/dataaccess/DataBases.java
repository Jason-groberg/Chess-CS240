package dataaccess;
import java.util.Map;
import java.util.HashMap;
import model.UserData;
import model.AuthData;
import model.GameData;

public class DataBases {
    //userDatabase string key is the username
    private Map<String, UserData> userDatabase = new HashMap<>();

    //GameData key is the Game ID
    private Map<Integer, GameData> gameDataBase = new HashMap<>();

    //AuthData username as key
    private Map<String, AuthData> authDatabase = new HashMap<>();

    public DataBases(Map<String, UserData> userDatabase, Map<Integer, GameData> gameDataBase, Map<String, AuthData> authDatabase) {
        this.userDatabase = userDatabase;
        this.gameDataBase = gameDataBase;
        this.authDatabase = authDatabase;
    }

    public void clearDatabases(){
        userDatabase.clear();
        gameDataBase.clear();
        authDatabase.clear();
    }

}
