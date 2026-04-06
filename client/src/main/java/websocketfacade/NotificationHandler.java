package websocketfacade;

import chess.ChessGame;

public interface NotificationHandler {
    void notify(String message);
    void updateGame(ChessGame game);
    void printError(String errorMessage);

}
