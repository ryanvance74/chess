package server.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.exceptions.DataAccessException;
import gsonextras.RuntimeTypeAdapterFactory;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import requests.UpdateGameRequest;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage.ServerMessageType;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.crypto.Data;
import java.io.IOException;


@WebSocket
public class WebSocketHandler {
    private final Gson commandGson;
    private final ConcurrentHashMap<Integer, ConnectionManager> gameConnectionManagers =
            new ConcurrentHashMap<>();
    private GameService gameService;
    private UserService userService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        RuntimeTypeAdapterFactory<UserGameCommand> commandTypeAdapterFactory
                = RuntimeTypeAdapterFactory.of(UserGameCommand.class, "commandType");
        commandTypeAdapterFactory.registerSubtype(MoveCommand.class, "Move");
        this.commandGson = new GsonBuilder()
                .registerTypeAdapterFactory(commandTypeAdapterFactory)
                .create();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = commandGson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(session, command);
            case MAKE_GAME_MANAGER -> createGameConnectionManager(command.getGameID());
            case MAKE_MOVE -> makeMove(session, command);
            case LEAVE ->
        }
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        String userName;
        GameData gameData;
        try {
            userName = userService.getUserNameFromAuth(command.getAuthToken());
            gameData = gameService.getGameFromId(command.getAuthToken(), command.getGameID());
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
        String message = "";
        if (gameData.whiteUsername().equals(userName)) {
            message = String.format("%s has entered the game as white.", userName);
        } else if (gameData.blackUsername().equals(userName)) {
            message = String.format("%s has entered the game as black.", userName);
        } else {
            message = String.format("%s has entered the game as an observer.", userName);
        }
        gameConnectionManagers.get(gameData.gameID()).add(userName, session);

        var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
        this.gameConnectionManagers.get(gameData.gameID()).broadcast(userName, notification);
    }

    public void createGameConnectionManager(int gameId) {
        gameConnectionManagers.put(gameId, new ConnectionManager());
    }

    public void makeMove(Session session, UserGameCommand command) {
        gameService.updateGameAfterJoin();

    }


//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }

//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}
