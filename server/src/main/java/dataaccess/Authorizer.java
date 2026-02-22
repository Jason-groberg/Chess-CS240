package dataaccess;
import java.util.UUID;

public class Authorizer {
    public static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
