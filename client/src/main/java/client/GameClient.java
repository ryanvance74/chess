package client;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.*;
import server.ResponseException;
import ServerFacade;
import service.*;
import ui.EscapeSequences;

import java.lang.StringBuilder;
import java.util.Arrays;
import java.util.HashMap;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class GameClient {
    private boolean signedIn;
    private String currentAuthToken;
    private final ServerFacade serverFacade;
    private final HashMap<Integer, ListGameResultSingle> gameCodeMap;

    private final HashMap<Integer, String> COL_LABEL_MAP;
    public GameClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.signedIn = false;
        this.gameCodeMap = new HashMap<>();
        this.COL_LABEL_MAP = new HashMap<>();
        this.COL_LABEL_MAP.put(1,"a");
        this.COL_LABEL_MAP.put(2,"b");
        this.COL_LABEL_MAP.put(3,"c");
        this.COL_LABEL_MAP.put(4,"d");
        this.COL_LABEL_MAP.put(5,"e");
        this.COL_LABEL_MAP.put(6,"f");
        this.COL_LABEL_MAP.put(7,"g");
        this.COL_LABEL_MAP.put(8,"h");
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
                case "observe" -> observeGame(params);
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
        StringBuilder sb = new StringBuilder();
        int gameCounter = 1;
        this.gameCodeMap.clear();
        sb.append("ID | NAME | WHITE | BLACK\n");
        for (ListGameResultSingle game : result.games()) {
            this.gameCodeMap.put(gameCounter, game);
            sb.append(gameCounter);
            sb.append(" ");
            sb.append(game.gameName());
            sb.append(" ");
            sb.append(game.whiteUsername());
            sb.append(" ");
            sb.append(game.blackUsername());
            sb.append("\n");
            gameCounter++;
        }

        return sb.toString();
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length >= 1 && (params[1].equalsIgnoreCase("white") || params[1].equalsIgnoreCase("black"))) {
            if (!this.signedIn) throw new ResponseException(400, "Not logged in yet.");
            chess.ChessGame.TeamColor color = params[1].equalsIgnoreCase("WHITE") ? WHITE : BLACK;
            int gameId = this.gameCodeMap.get(Integer.parseInt(params[0])).gameID();
            UpdateGameRequest req = new UpdateGameRequest(this.currentAuthToken, color, gameId);
            serverFacade.joinGame(req);
            return String.format("Joined game: %s", params[0]);
        }
        throw new ResponseException(400, "Error. Expected: <ID> [WHITE|BLACK]");
    }

    public String logout() throws ResponseException {
        serverFacade.logout(new LogoutRequest(this.currentAuthToken));
        return "Successfully logged out.";
    }

    public String observeGame(String... params) throws ResponseException {
        ListGameResultSingle gameObj = this.gameCodeMap.get(Integer.parseInt(params[0]));
        return drawBoard(gameObj.game(), WHITE);
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

    public String drawBoard(ChessGame game, ChessGame.TeamColor orientationTeamColor) {
        StringBuilder sb = new StringBuilder();
        ChessBoard board = game.getBoard();
        drawHeader(sb);
        if (orientationTeamColor == BLACK) {
            for (int row=1; row < 9; row++) {
                sb.append(SET_TEXT_BOLD + this.COL_LABEL_MAP.get(row));
                for (int col=1; col < 9; col++) {
                    drawBoardHelper(sb, board, row, col);
                }
                sb.append("\n");
            }

        } else {
            for (int row=8; row > 0; row--) {
                sb.append(SET_TEXT_BOLD + this.COL_LABEL_MAP.get(row) + " \u2009");
                for (int col=1; col < 9; col++) {
                    drawBoardHelper(sb, board, row, col);
                }
                sb.append(RESET_BG_COLOR);
                sb.append(" \u2009" + SET_TEXT_BOLD + this.COL_LABEL_MAP.get(row));
                sb.append("\n");
            }

        }
        drawHeader(sb);
        return sb.toString();
    }

    private void drawBoardHelper(StringBuilder sb, ChessBoard board, int row, int col) {
        String tileColor;
        if (col % 2 == 1) {
            tileColor = row % 2 == 1 ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_WHITE;
        } else {
            tileColor = row % 2 == 0 ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_WHITE;
        }

        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            sb.append(tileColor + EMPTY);
            return;
        }

        ChessPiece.PieceType pieceType = piece.getPieceType();
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        sb.append(tileColor);
        switch (pieceType) {
            case KING -> {
                if (pieceColor == WHITE) {
                    sb.append(EscapeSequences.WHITE_KING);
                } else {
                    sb.append(EscapeSequences.BLACK_KING);
                }
            }
            case QUEEN -> {
                if (pieceColor == WHITE) {
                    sb.append(EscapeSequences.WHITE_QUEEN);
                } else {
                    sb.append(EscapeSequences.BLACK_QUEEN);
                }
            }
            case BISHOP -> {
                if (pieceColor == WHITE) {
                    sb.append(EscapeSequences.WHITE_BISHOP);
                } else {
                    sb.append(EscapeSequences.BLACK_BISHOP);
                }
            }
            case KNIGHT -> {
                if (pieceColor == WHITE) {
                    sb.append(EscapeSequences.WHITE_KNIGHT);
                } else {
                    sb.append(EscapeSequences.BLACK_KNIGHT);
                }
            }
            case ROOK -> {
                if (pieceColor == WHITE) {
                    sb.append(EscapeSequences.WHITE_ROOK);
                } else {
                    sb.append(EscapeSequences.BLACK_ROOK);
                }
            }
            case PAWN -> {
                if (pieceColor == WHITE) {
                    sb.append(EscapeSequences.WHITE_PAWN);
                } else {
                    sb.append(EscapeSequences.BLACK_PAWN);
                }
            }
        }
    }

    private void drawHeader(StringBuilder sb) {
        sb.append(" \u2009");
        sb.append(" \u2003" + SET_TEXT_BOLD + "1");
        sb.append(" \u2003" + "2");
        sb.append(" \u2003" + "3");
        sb.append(" \u2003" + "4");
        sb.append(" \u2003" + "5");
        sb.append(" \u2003" + "6");
        sb.append(" \u2003" + "7");
        sb.append(" \u2003" + "8\n");
    }
}
