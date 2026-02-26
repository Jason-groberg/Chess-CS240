package dataaccess;

public class UserNotFoundExecption extends RuntimeException {
    public UserNotFoundExecption(String message) {
        super(message);
    }
}
