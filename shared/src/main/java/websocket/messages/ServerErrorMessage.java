package websocket.messages;

import java.util.Objects;

public class ServerErrorMessage extends ServerMessage {
    String errorMessage;
    public ServerErrorMessage(ServerMessage.ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getMessage() {return this.errorMessage;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        NotificationMessage that = (NotificationMessage) o;
        return getServerMessageType() == that.getServerMessageType() &&
                getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getMessage());
    }
}


