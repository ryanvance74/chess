package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DatabaseDAOCommunicator;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
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
import chess.ChessGame.TeamColor;



@WebSocket
public class WebSocketHandler {
//    private final Gson commandGson;
    private final ConcurrentHashMap<Integer, ConnectionManager> gameConnectionManagers =
            new ConcurrentHashMap<>();
    private GameService gameService;
    private UserService userService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
        error.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("received message DEBUG MESSAGE");
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            System.out.println(message);
            command = new Gson().fromJson(message, MoveCommand.class);
        }
        switch (command.getCommandType()) {
            case CONNECT -> connect(session, command);
            case MAKE_GAME_MANAGER -> createGameConnectionManager(command.getGameID());
            case MAKE_MOVE -> makeMove(session, (MoveCommand) command);
            case LEAVE -> leaveGame(session, command);
            case RESIGN -> resignGame(session, command);
        }
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        AuthData authData;
        GameData gameData;
        String userName;
        try {
            authData = userService.getAuthDataNameFromAuth(command.getAuthToken());
            if (authData == null) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerErrorMessage(ServerMessageType.ERROR, "Error: you have not logged in yet.")));
                return;
            }
            userName = authData.username();
            gameData = gameService.getGameFromId(command.getAuthToken(), command.getGameID());
            if (gameData == null) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerErrorMessage(ServerMessageType.ERROR, "Error: that is an invalid game ID.")));
                return;
            }
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
        if (gameConnectionManagers.get(gameData.gameID()) == null) {
            System.out.println("adding new connection manager for game: " + gameData.gameID());
            gameConnectionManagers.put(gameData.gameID(), new ConnectionManager());
        }
        gameConnectionManagers.get(gameData.gameID()).add(userName, session);

        var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
        System.out.println("getting in connect: " + gameData.gameID());
        this.gameConnectionManagers.get(gameData.gameID()).broadcast(userName, notification);
        System.out.println("made it past get connection manager");
        var loadGameNotification = new LoadGameMessage(ServerMessageType.LOAD_GAME, gameData.game());
        this.gameConnectionManagers.get(gameData.gameID()).singleSend(userName, loadGameNotification);
        System.out.println("sent load game message");
    }

    private void createGameConnectionManager(int gameId) {
        System.out.println("putting: " + gameId);
        gameConnectionManagers.put(gameId, new ConnectionManager());
    }

    private void makeMove(Session session, MoveCommand command) throws IOException {
        ChessMove move = command.getMove();
        GameData gameData;
        String userName = "";
        ChessGame game;
//        this.session.getBasicRemote().sendText(new Gson().toJson(command));
        try {


            AuthData authData = userService.getAuthDataNameFromAuth(command.getAuthToken());
            if (authData == null) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerErrorMessage(ServerMessageType.ERROR, "Error: you have not logged in yet.")));
                return;
            }
            userName = authData.username();
            gameData = gameService.getGameFromId(command.getAuthToken(), command.getGameID());
            game = gameData.game();
            if (game.isInCheckmate(TeamColor.WHITE) || game.isInCheckmate(TeamColor.BLACK)) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerErrorMessage(ServerMessageType.ERROR, "Error: the game has ended. You cannot make another move.")));
                return;
            }
            if (game.getTeamTurn() == TeamColor.WHITE && !gameData.whiteUsername().equals(userName)) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerErrorMessage(ServerMessageType.ERROR, "Error: it is not your turn!")));
                return;
            } else if (game.getTeamTurn() == TeamColor.BLACK && !gameData.blackUsername().equals(userName)) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerErrorMessage(ServerMessageType.ERROR, "Error: it is not your turn!")));
                return;
            }

            if (game.getBoard().getPiece(move.getStartPosition()).getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (!gameData.whiteUsername().equals(userName)) {
                    session.getRemote().sendString(new Gson().toJson(
                            new ServerErrorMessage(ServerMessageType.ERROR, "Error: that is not your piece!")));
                    return;
                }
            } else {
                if (!gameData.blackUsername().equals(userName)) {
                    session.getRemote().sendString(new Gson().toJson(
                            new ServerErrorMessage(ServerMessageType.ERROR, "Error: that is not your piece!")));
                    return;
                }
            }
            game = gameService.updateGameState(command.getAuthToken(), command.getGameID(), move);

            String cLetterInitial = String.valueOf((char) ('a' + move.getStartPosition().getColumn() - 1));
            String rNumberInitial = (String.valueOf(move.getStartPosition().getRow()));
            String cLetterFinal = String.valueOf((char) ('a' + move.getEndPosition().getColumn() - 1));
            String rNumberFinal = (String.valueOf(move.getEndPosition().getRow()));

            String message = String.format("%s%s moves to %s%s", cLetterInitial, rNumberInitial,
                    cLetterFinal, rNumberFinal);
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
            this.gameConnectionManagers.get(command.getGameID()).broadcast(userName, notification);
            var loadGameNotification = new LoadGameMessage(ServerMessageType.LOAD_GAME, game);
            this.gameConnectionManagers.get(command.getGameID()).broadcast("", loadGameNotification);

            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                String whiteUser = gameData.whiteUsername();
                notification = new NotificationMessage(ServerMessageType.NOTIFICATION, whiteUser + "has been checkmated!");
                this.gameConnectionManagers.get(command.getGameID()).broadcast("", notification);
            } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                String blackUser = gameData.blackUsername();
                notification = new NotificationMessage(ServerMessageType.NOTIFICATION, blackUser + "has been checkmated!");
                this.gameConnectionManagers.get(command.getGameID()).broadcast("", notification);
            } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                String whiteUser = gameData.whiteUsername();
                notification = new NotificationMessage(ServerMessageType.NOTIFICATION, whiteUser + "is in check");
                this.gameConnectionManagers.get(command.getGameID()).broadcast("", notification);
            } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                String blackUser = gameData.blackUsername();
                notification = new NotificationMessage(ServerMessageType.NOTIFICATION, blackUser + "is in check");
                this.gameConnectionManagers.get(command.getGameID()).broadcast("", notification);
            }
        } catch (Exception e) {
            System.out.println("caught an exception");
            session.getRemote().sendString(new Gson().toJson(
                    new ServerErrorMessage(ServerMessageType.ERROR, "Error: that is an invalid move.")));
        }
    }

    private void leaveGame(Session session, UserGameCommand command) throws IOException {
        String userName="";
        try {
            userName = userService.getAuthDataNameFromAuth(command.getAuthToken()).username();
            gameService.removeUserFromGame(command.getAuthToken(), command.getGameID());
            String message = String.format("%s has left the game.", userName);
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
            this.gameConnectionManagers.get(command.getGameID()).broadcast(userName, notification);
            this.gameConnectionManagers.get(command.getGameID()).remove(userName);
        } catch (Exception e) {
            this.gameConnectionManagers.get(command.getGameID()).singleSend(userName,
                    new ServerErrorMessage(ServerMessageType.ERROR, e.getMessage()));
        }
    }

    private void resignGame(Session session, UserGameCommand command) throws IOException {
        String userName="";
        try {
            // TODO make all username checks safe

            userName = userService.getAuthDataNameFromAuth(command.getAuthToken()).username();
            GameData gameData = gameService.getGameFromId(command.getAuthToken(), command.getGameID());
            if (!userName.equals(gameData.whiteUsername()) && !userName.equals(gameData.blackUsername())) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerErrorMessage(ServerMessageType.ERROR, "You are an observer. You cannot resign from the game!")));
                return;
            } else if (gameData.game().getHasResigned()) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerErrorMessage(ServerMessageType.ERROR, "The game has already ended. You cannot resign.")));
                return;
            }
            gameService.setGameHasResigned(command.getAuthToken(), command.getGameID());
            String message = String.format("%s has resigned. The game game is over", userName);
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
            this.gameConnectionManagers.get(command.getGameID()).broadcast("", notification);
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }
}
