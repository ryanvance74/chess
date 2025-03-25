package requests;

import chess.ChessGame;

public record ListGameResultSingle(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
