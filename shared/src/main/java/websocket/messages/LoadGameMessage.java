package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage {
    ChessGame game;
    ChessGame.TeamColor playerPerspective;
    public LoadGameMessage(ServerMessage.ServerMessageType type, ChessGame game, ChessGame.TeamColor playerPerspective) {
        super(type);
        this.game = game;
        this.playerPerspective = playerPerspective;
    }

    public ChessGame getGame() {return this.game;}
    public ChessGame.TeamColor getPlayerPerspective() {return this.playerPerspective;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        LoadGameMessage that = (LoadGameMessage) o;
        return getServerMessageType() == that.getServerMessageType() &&
                getGame().equals(that.getGame()) &&
                getPlayerPerspective().equals(that.getPlayerPerspective());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getGame(), getPlayerPerspective());
    }
}

