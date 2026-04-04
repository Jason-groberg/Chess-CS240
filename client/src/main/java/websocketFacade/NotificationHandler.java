package websocketFacade;

import chess.ChessGame;

public interface NotificationHandler {
    void notify(String notification);
    void updateGame(ChessGame game);
    void printError(String errorMessage);

}
