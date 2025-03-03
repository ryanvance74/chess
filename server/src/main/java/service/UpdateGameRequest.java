package service;

public record UpdateGameRequest(String authToken, String playerColor, int gameID) {
}
