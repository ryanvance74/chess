package server.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.exceptions.DataAccessException;
import gsonextras.RuntimeTypeAdapterFactory;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import javax.xml.crypto.Data;
import java.io.IOException;


@WebSocket
public class WebSocketHandler {
    private final Gson commandGson;
    private final ConnectionManager connections = new ConnectionManager();
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
            case MAKE_MOVE ->
            case LEAVE ->
        }
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        String userName;
        GameData gameData;
        try {
            userName = userService.getUserNameFromAuth(command.getAuthToken());
            gameData = gameService.
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }

        connections.add(userName, session);

        var message = String.format("%s has entered the game.", userName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(userName, notification);
    }

    private void exit(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
