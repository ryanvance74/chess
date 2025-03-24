package client;
import chess.ChessGame;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import server.ResponseException;
import server.ServerFacade;
import service.*;
import ui.EscapeSequences;

import java.lang.StringBuilder;
import java.util.Arrays;

public class GameClient {
    private boolean signedIn;
    private String currentAuthToken;
    private final ServerFacade serverFacade;

    public GameClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.signedIn = false;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "help" -> help();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> "no functionality yet";
                case "logout" -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
            this.signedIn = true;
            RegisterRequest req = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = serverFacade.register(req);
            this.currentAuthToken = result.authToken();
            return String.format("Registered and logged in as %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <NAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
            this.signedIn = true;
            LoginRequest req = new LoginRequest(params[0], params[1]);
            LoginResult result = serverFacade.login(req);
            this.currentAuthToken = result.authToken();
            return String.format("Logged in as %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <NAME> <PASSWORD>");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            if (!this.signedIn) throw new ResponseException(400, "Not logged in yet.");
            CreateGameRequest req = new CreateGameRequest(this.currentAuthToken, params[0]);
            serverFacade.createGame(req);
            return String.format("Created game: %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames() throws ResponseException {
        if (!this.signedIn) throw new ResponseException(400, "Not logged in yet.");
        ListGamesResult result = serverFacade.listGames(this.currentAuthToken);
        return result.toString();
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length >= 1 && (params[1].equals("WHITE") || params[1].equals("BLACK"))) {
            if (!this.signedIn) throw new ResponseException(400, "Not logged in yet.");
            chess.ChessGame.TeamColor color = params[1].equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            UpdateGameRequest req = new UpdateGameRequest(this.currentAuthToken, color, Integer.parseInt(params[0]));
            serverFacade.joinGame(req);
            return String.format("Joined game: %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }

    public String logout() throws ResponseException {
        serverFacade.logout(new LogoutRequest(this.currentAuthToken));
        return "Successfully logged out.";
    }

    public String help() {
        StringBuilder sb = new StringBuilder();
        if (this.signedIn) {
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "create <NAME>");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - a game");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "list");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - games");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - a game");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "observe <ID>");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - a game");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "logout");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - a user");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "quit");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - the application");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "help");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - with commands");
        } else {
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - to create an account");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - to play chess");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "quit");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - the application");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "help");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " - with commands");
        }
        return sb.toString();
    }
}
