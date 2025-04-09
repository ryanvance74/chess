package websocket.messages;

import java.util.Objects;

public class NotificationMessage extends ServerMessage {
    String message;
    public NotificationMessage(ServerMessage.ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public String getMessage() {return this.message;}

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
