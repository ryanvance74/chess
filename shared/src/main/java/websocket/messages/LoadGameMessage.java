package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage {
    ChessGame game;
    public LoadGameMessage(ServerMessage.ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() {return this.game;}

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
                getGame().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getGame());
    }
}

