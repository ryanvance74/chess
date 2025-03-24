package client;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.*;
import server.ResponseException;
import server.ServerFacade;
import service.*;
import ui.EscapeSequences;

import java.lang.StringBuilder;
import java.util.Arrays;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

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
            try {
                JsonObject obj = new Gson().fromJson(ex.getMessage(), JsonObject.class);
                return obj.get("message").getAsString();
            } catch (JsonSyntaxException e) {
                return ex.getMessage();
            }
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
        if (params.length >= 1 && (params[1].equalsIgnoreCase("white") || params[1].equalsIgnoreCase("black"))) {
            if (!this.signedIn) throw new ResponseException(400, "Not logged in yet.");
            chess.ChessGame.TeamColor color = params[1].equalsIgnoreCase("WHITE") ? WHITE : BLACK;
            UpdateGameRequest req = new UpdateGameRequest(this.currentAuthToken, color, Integer.parseInt(params[0]));
            serverFacade.joinGame(req);
            return String.format("Joined game: %s", params[0]);
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
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - a game\n");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "list");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - games\n");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - a game\n");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "observe <ID>");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - a game\n");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "logout");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - a user\n");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "quit");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - the application\n");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "help");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - with commands\n");
        } else {
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - to create an account\n");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - to play chess\n");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "quit");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - the application\n");
            sb.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "help");
            sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " - with commands\n");
        }
        return sb.toString();
    }

//    public String drawBoard(ChessGame.TeamColor orientationTeamColor, ChessGame game) {
//        StringBuilder sb = new StringBuilder();
//        ChessBoard board = game.getBoard();
//        if (orientationTeamColor == BLACK) {
//            for (int row=1; row < 9; row++) {
//                for (int col=1; col < 9; col++) {
//                    String tileColor;
//
//                    if (col % 2 == 1) {
//                        tileColor = row % 2 == 0 ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_WHITE;
//                    } else {
//                        tileColor = row % 2 == 1 ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_WHITE;
//                    }
//
//                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
//                    ChessPiece.PieceType pieceType = piece.getPieceType();
//                    ChessGame.TeamColor pieceColor = piece.getTeamColor();
//                    switch (pieceType) {
//                        case KING -> {
//                            if (pieceColor == WHITE) {
//                                sb.append(tileColor + EscapeSequences.WHITE_KING);
//                            } else {
//                                sb.append(tileColor + EscapeSequences.SET_BG_COLOR_BLACK);
//                            }
//
//                        }
//                        case
//                    }
//
//                }
//            }
//        }
//    }
}
