package service;
import Requests.RegisterRequest;
import dataaccess.RegisterDoa;

public class RegisterService {
    public void registerUser(RegisterRequest request){
         RegisterDoa doa = new RegisterDoa(request.username(), request.password(), request.email());
         doa.register();
    }


}
