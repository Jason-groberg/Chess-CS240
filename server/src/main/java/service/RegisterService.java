package service;
import Requests.RegisterRequest;
import dataaccess.RegisterDoa;

public class RegisterService {
    public void registerUser(){
         RegisterDoa doa = new RegisterDoa();
         doa.register()
    }


}
