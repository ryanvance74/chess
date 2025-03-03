package service;

public record UpdateGameRequest(String authToken, String username, String playerColor, Integer gameId) {
}
