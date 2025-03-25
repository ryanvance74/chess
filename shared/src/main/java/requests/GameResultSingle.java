package requests;

import chess.ChessGame;

public record GameResultSingle(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
