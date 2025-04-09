package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DatabaseDAOCommunicator;
import dataaccess.exceptions.DataAccessException;
import gsonextras.RuntimeTypeAdapterFactory;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import requests.UpdateGameRequest;
import server.Server;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import javax.websocket.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerErrorMessage;
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
            case MAKE_MOVE -> makeMove(session, (MoveCommand) command);
            case LEAVE -> leaveGame(command);
            case RESIGN -> resignGame(command);
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

    private void createGameConnectionManager(int gameId) {
        gameConnectionManagers.put(gameId, new ConnectionManager());
    }

    private void makeMove(Session session, MoveCommand command) throws IOException {
        ChessMove move = command.getMove();
        String userName = "";
        ChessGame game;
//        this.session.getBasicRemote().sendText(new Gson().toJson(command));
        try {
            userName = userService.getUserNameFromAuth(command.getAuthToken());
            game = gameService.updateGameState(command.getAuthToken(), command.getGameID(), move);

            String cLetterInitial = String.valueOf((char) ('a' + move.getStartPosition().getColumn() - 1));
            String rNumberInitial = (String.valueOf(move.getStartPosition().getRow()));
            String cLetterFinal = String.valueOf((char) ('a' + move.getEndPosition().getColumn() - 1));
            String rNumberFinal = (String.valueOf(move.getEndPosition().getRow()));

            String message = String.format("%s%s moves to %s%s", cLetterInitial, rNumberInitial,
                    cLetterFinal, rNumberFinal);
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
            this.gameConnectionManagers.get(command.getGameID()).broadcast("", notification);
            var loadGameNotification = new LoadGameMessage(ServerMessageType.LOAD_GAME, game);
            this.gameConnectionManagers.get(command.getGameID()).broadcast("", loadGameNotification);
        } catch (Exception e) {
                this.gameConnectionManagers.get(command.getGameID()).singleSend(userName,
                        new ServerErrorMessage(ServerMessageType.ERROR, e.getMessage()));
        }
    }

    private void leaveGame(UserGameCommand command) throws IOException {
        String userName="";
        try {
            userName = userService.getUserNameFromAuth(command.getAuthToken());
            gameService.removeUserFromGame(command.getAuthToken(), command.getGameID());
            String message = String.format("%s has left the game.", userName);
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
            this.gameConnectionManagers.get(command.getGameID()).broadcast("", notification);
        } catch (Exception e) {
            this.gameConnectionManagers.get(command.getGameID()).singleSend(userName,
                    new ServerErrorMessage(ServerMessageType.ERROR, e.getMessage()));
        }
    }

    private void resignGame(UserGameCommand command) throws IOException {
        String userName="";
        try {
            userName = userService.getUserNameFromAuth(command.getAuthToken());
            gameService.setGameHasResigned(command.getAuthToken(), command.getGameID());
            String message = String.format("%s has resigned. The game game is over", userName);
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
            this.gameConnectionManagers.get(command.getGameID()).broadcast("", notification);
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }
}
