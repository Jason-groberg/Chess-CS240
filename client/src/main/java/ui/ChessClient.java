package ui;
import facade.ServerFacade;
import model.GameData;

import java.util.Arrays;
import java.util.Collection;

public class ChessClient {
    public enum State{
        SIGNEDIN,
        SIGNEDOUT
    }
    private State state = State.SIGNEDOUT;
    private final String serverUrl;
    private final ServerFacade server;
    private String authToken = null;
    private Collection<GameData> gamelist =null;

    public ChessClient(String url){
        server = new ServerFacade(url);
        serverUrl = url;
    }

    public String eval(String input){
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (state) {
                case SIGNEDOUT -> preLoginEval(cmd, params);
                case SIGNEDIN -> postLoginEval(cmd, params);
            };
        }catch(Exception e){
            return e.getMessage();
        }
    }

    public String postLoginEval(String cmd, String[] params){

    }

    public String preLoginEval(String cmd, String[] params){

    }

    public String help() {
        if(state == State.SIGNEDOUT){

        }
    }



}
