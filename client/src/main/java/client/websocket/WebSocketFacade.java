package client.websocket;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import org.eclipse.jetty.server.Response;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import websocket.commands.UserGameCommand.CommandType;
import facade.ResponseException;
import requests.UpdateGameRequest;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                notificationHandler.notify(notification);
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

//    public void join(UpdateGameRequest req) throws ResponseException {
//        try {
//            var action = new UserGameCommand(CommandType.CONNECT, req.authToken(), req.gameID());
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

//    public void leave(UpdateGameRequest req) throws ResponseException {
//        try {
//            var action = new UserGameCommand(CommandType.LEAVE, req.authToken(), req.gameID());
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

//    public void makeMove(MoveCommand moveCommand) throws ResponseException {
//        try {
//            this.session.getBasicRemote().sendText(new Gson().toJson(moveCommand));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//    public void resign(UpdateGameRequest req) throws ResponseException {
//        try {
//            var action = new UserGameCommand(CommandType.RESIGN, req.authToken(), req.gameID());
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }



}
