package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

public class MoveCommand extends UserGameCommand {
    private final ChessMove chessMove;
    public MoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove chessMove) {
        super(commandType, authToken, gameID);
        this.chessMove = chessMove;
    }

    public ChessMove getMove() {
        return this.chessMove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MoveCommand)) {
            return false;
        }
        MoveCommand that = (MoveCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID()) &&
                Objects.equals(getMove(), that.getMove());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID(), getMove());
    }

}
