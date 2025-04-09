package websocket.messages;

import java.util.Objects;

public class ServerErrorMessage extends ServerMessage {
    String errorMessage;
    public ServerErrorMessage(ServerMessage.ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getMessage() {return this.errorMessage;}
}


