package requests;

import chess.ChessGame;

public record UpdateGameRequest(String authToken, ChessGame.TeamColor playerColor, int gameID) {
}
