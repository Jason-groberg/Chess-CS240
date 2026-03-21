package model.results;

import chess.ChessGame;

public record ListResult(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
