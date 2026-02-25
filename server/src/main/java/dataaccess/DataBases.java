package dataaccess;
import java.util.Map;
import java.util.HashMap;
import model.UserData;
import model.AuthData;
import model.GameData;

public class DataBases {
    //userDatabase string key is the username
    public static Map<String, UserData> userDatabase = new HashMap<>();

    //GameData key is the Game ID
    public static Map<Integer, GameData> gameDataBase = new HashMap<>();

    //AuthData username as key
    public static Map<String, AuthData> authDatabase = new HashMap<>();

}
