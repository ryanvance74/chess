package client;
import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import facade.*;
import org.eclipse.jetty.server.Authentication;
import requests.*;
import spark.Response;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class GameClient {
    private boolean signedIn;
    private boolean inGame;
    private String currentAuthToken;
    private boolean mightResign;
    private final ServerFacade serverFacade;
    private final String serverUrl;
    private WebSocketFacade ws;
    private final HashMap<Integer, GameResultSingle> gameCodeMap;
    private final HashMap<String, ChessPiece.PieceType> pieceTypeHashMap;
    private final NotificationHandler notificationHandler;
    private ChessGame.TeamColor color;

    public GameClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.signedIn = false;
        this.inGame = false;
        this.gameCodeMap = new HashMap<>();
        this.notificationHandler = notificationHandler;
        this.serverUrl = serverUrl;
        this.mightResign = false;
        this.ws = new WebSocketFacade(serverUrl, notificationHandler);

        this.pieceTypeHashMap = new HashMap<>();
        pieceTypeHashMap.put("QUEEN", ChessPiece.PieceType.QUEEN);
        pieceTypeHashMap.put("ROOK", ChessPiece.PieceType.ROOK);
        pieceTypeHashMap.put("KNIGHT", ChessPiece.PieceType.KNIGHT);
        pieceTypeHashMap.put("BISHOP", ChessPiece.PieceType.BISHOP);
    }

    public String eval(String input) {
        String cmd = "";
        try {
            var tokens = input.toLowerCase().split(" ");
            cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "leave" -> leave(params);
                case "move" -> move(params);
                case "resign" -> resign(params);
                case "highlight" -> highlightLegalMoves(params);
                default -> help();
            };
        } catch (NullPointerException e) {
            if (cmd.equals("join")) {
                return """
                    Either you game number was invalid or you did not supply all of the necessary fields.
                    Use the help command to see which fields are required for this command.""";
            } else {
                return """
                    You did not put in all the necessary information.
                    Use the help command to see which fields are required for this command.""";
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            if (cmd.equals("register") || cmd.equals("login")) {
                return "Your register or login request did not have all of the required fields. Run help for more information.";
            } else {
                return """
                        Your join, observe, or create request either did not have
                         all of the required fields, or you tried to find a game with an invalid number.
                         Please run help for more information.""";
            }
        } catch (Exception e) {
            if (cmd.equals("login")) {
                return """
                Something about your request was incorrect.
                You likely are trying to login as someone who has not yet registered.""";
            }
            return "Something about your request was incorrect. Please run the help command for more information.";
        }
//        } (ResponseException ex) {
//            try {
//                JsonObject obj = new Gson().fromJson(ex.getMessage(), JsonObject.class);
//                return obj.get("message").getAsString();
//            } catch (JsonSyntaxException e) {
//                return ex.getMessage();
//            }
//        }
    }

    public String leave(String... params) throws ResponseException {
        if (!inGame) {
            return "You are not currently in a game.";
        }
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return "You must specify which game you want to leave.";
        }
        if (!this.gameCodeMap.containsKey(gameNumber)) {
            return "Invalid game number";
        }
        int gameId = this.gameCodeMap.get(gameNumber).gameID();
        UserGameCommand command = new UserGameCommand(CommandType.LEAVE, this.currentAuthToken, gameId);
        ws.sendCommand(command);
        this.inGame = false;
        return String.format("Left game #%s", gameNumber);
    }

    public String move(String... params) throws ResponseException {
        if (!inGame || !signedIn) {
            return "You cannot make a move when you are not in a game or not signed in.";
        }
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return "You must specify which game you want to leave.";
        }
        if (!this.gameCodeMap.containsKey(gameNumber)) {
            return "Invalid game number";
        }
        int gameId = this.gameCodeMap.get(gameNumber).gameID();

        ChessMove move = parseMove(Arrays.copyOfRange(params, 1, params.length));
        if (move == null) {
            return "Expected <GAME> <START POSITION> <END POSITION> <promotion-piece (if applicable)>";
        }

        MoveCommand moveCommand = new MoveCommand(CommandType.MAKE_MOVE, this.currentAuthToken, gameId, move);
        ws.sendCommand(moveCommand);
        return "Made the move.";
    }

    public String resign(String... params) throws ResponseException {
        if (mightResign) {
            if (params[0].equals("confirm")) {
                int gameNumber;
                try {
                    gameNumber = Integer.parseInt(params[2]);
                } catch (NumberFormatException e) {
                    return "You must specify the game from which you want to resign.";
                }
                if (!this.gameCodeMap.containsKey(gameNumber)) {
                    return "Invalid game number";
                }
                int gameId = this.gameCodeMap.get(gameNumber).gameID();

                UserGameCommand command = new UserGameCommand(CommandType.RESIGN, this.currentAuthToken, gameId);
                ws.sendCommand(command);
                return "Successfully resigned.";
            } else {
                mightResign = false;
                return "No confirmation. Will not resign.";
            }

        } else {
            mightResign = true;
            return "Are you sure that you want to resign? If so, enter 'resign <game> confirm'";
        }

    }

    public String highlightLegalMoves(String... params) throws ResponseException {
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return "You must specify the game you want to highlight.";
        }
        if (!params[1].matches("[a-h][1-8]")) {
            return "You must specify the piece that you want to highlight.";
        }

        ChessGame game = this.gameCodeMap.get(gameNumber).game();
        ChessBoard board = game.getBoard();
        ChessPosition position = new ChessPosition(Character.toLowerCase(params[0].charAt(0))-'a'+1, params[0].charAt(1));
        ChessPiece piece = board.getPiece(position);
        Collection<ChessMove> moves = piece.pieceMoves(board, position);

        int[][] endPositions = new int[8][8];
        endPositions[position.getRow()][position.getColumn()] = 2;
        for (ChessMove move : moves) {
            ChessPosition endPosition = move.getEndPosition();
            endPositions[endPosition.getRow()][endPosition.getColumn()] = 1;
        }
        return drawBoard(game, this.color, endPositions);
    }

    private ChessMove parseMove(String... params) throws MoveParsingException {
        if (!params[0].matches("[a-h][1-8]") || !params[1].matches("[a-h][1-8]")) {
            return null;
        }
        ChessPosition startPosition = new ChessPosition(params[0].charAt(1),Character.toLowerCase(params[0].charAt(0))-'a'+1);
        ChessPosition endPosition = new ChessPosition(params[1].charAt(1),Character.toLowerCase(params[1].charAt(0))-'a'+1);
        if (params.length == 2) {
            return new ChessMove(startPosition, endPosition, null);
        } else {
            if (this.pieceTypeHashMap.containsKey(params[3])) {
                return new ChessMove(startPosition, endPosition, this.pieceTypeHashMap.get(params[3]));
            } else {
                return null;
            }
        }
    }

    public String register(String... params) throws ResponseException {
        if (inGame) {
            return "You cannot register when in a game.";
        }
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
        if (inGame) {
            return "You cannot login when in a game.";
        }
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
            if (!this.signedIn) {throw new ResponseException(400, "Not logged in yet.");}
            CreateGameRequest req = new CreateGameRequest(this.currentAuthToken, params[0]);
            CreateGameResult result = serverFacade.createGame(req);
            UserGameCommand command = new UserGameCommand(CommandType.MAKE_GAME_MANAGER, this.currentAuthToken, result.gameID());
            ws.sendCommand(command);
            return String.format("Created game: %s.", params[0]);
        }

        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames() throws ResponseException {
        if (!this.signedIn) {throw new ResponseException(400, "Not logged in yet.");}
        ListGamesResult result = serverFacade.listGames(this.currentAuthToken);
        StringBuilder sb = new StringBuilder();
        int gameCounter = 1;
        this.gameCodeMap.clear();
        sb.append("ID | NAME | WHITE | BLACK\n");
        for (GameResultSingle game : result.games()) {
            this.gameCodeMap.put(gameCounter, game);
            sb.append(gameCounter);
            sb.append(" ");
            sb.append(game.gameName());
            sb.append(" ");
            if (game.whiteUsername() == null) {
                sb.append("available");
            } else {
                sb.append(game.whiteUsername());
            }
            sb.append(" ");
            if (game.blackUsername() == null) {
                sb.append("available");
            } else {
                sb.append(game.blackUsername());
            }
            sb.append("\n");
            gameCounter++;
        }

        return sb.toString();
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length >= 1 && (params[1].equalsIgnoreCase("white") || params[1].equalsIgnoreCase("black"))) {
            if (!this.signedIn) {throw new ResponseException(400, "Not logged in yet.");}
            ChessGame.TeamColor color = params[1].equalsIgnoreCase("WHITE") ? WHITE : BLACK;
            int gameId = this.gameCodeMap.get(Integer.parseInt(params[0])).gameID();
            UpdateGameRequest req = new UpdateGameRequest(this.currentAuthToken, color, gameId);

            ws = new WebSocketFacade(serverUrl, notificationHandler);
            serverFacade.joinGame(req);
            ws.sendCommand(new UserGameCommand(CommandType.CONNECT, this.currentAuthToken, gameId));
            this.inGame = true;
            this.color = color;
            return String.format("Joined game: %s\n", params[0]); //+ drawBoard(new ChessGame(), color, null);
        }
        throw new ResponseException(400, "Error. Expected: <ID> [WHITE|BLACK]");
    }

    public String logout() throws ResponseException {
        if (inGame) {
            return "You cannot logout when in a game.";
        }
        this.signedIn = false;
        serverFacade.logout(new LogoutRequest(this.currentAuthToken));
        return "Successfully logged out.";
    }

    public String observeGame(String... params) throws ResponseException {
        GameResultSingle gameObj = this.gameCodeMap.get(Integer.parseInt(params[0]));
        return drawBoard(gameObj.game(), WHITE, null);
    }
    public String help() {
        StringBuilder sb = new StringBuilder();
        if (this.signedIn && this.inGame) {
            sb.append(SET_TEXT_COLOR_BLUE + "redraw");
            sb.append(SET_TEXT_COLOR_WHITE + " - the board\n");
            sb.append(SET_TEXT_COLOR_BLUE + "leave");
            sb.append(SET_TEXT_COLOR_WHITE + " - the game\n");
            sb.append(SET_TEXT_COLOR_BLUE + "move");
            sb.append(SET_TEXT_COLOR_WHITE + " - make a move in the chess game.\n");
            sb.append(SET_TEXT_COLOR_WHITE + " - valid moves specify the game as well as two positions on the board such as a7 or c2.\n");
            sb.append(SET_TEXT_COLOR_BLUE + "resign");
            sb.append(SET_TEXT_COLOR_WHITE + " - forfeit the game\n");
            sb.append(SET_TEXT_COLOR_BLUE + "highlight");
            sb.append(SET_TEXT_COLOR_WHITE + " - legal moves that a certain piece can make in the game\n");
            sb.append(SET_TEXT_COLOR_BLUE + "help");
            sb.append(SET_TEXT_COLOR_WHITE + " - with commands\n");
        } else if (this.signedIn) {
            sb.append(SET_TEXT_COLOR_BLUE + "create <NAME>");
            sb.append(SET_TEXT_COLOR_WHITE + " - a game\n");
            sb.append(SET_TEXT_COLOR_BLUE + "list");
            sb.append(SET_TEXT_COLOR_WHITE + " - games\n");
            sb.append(SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]");
            sb.append(SET_TEXT_COLOR_WHITE + " - a game\n");
            sb.append(SET_TEXT_COLOR_BLUE + "observe <ID>");
            sb.append(SET_TEXT_COLOR_WHITE + " - a game\n");
            sb.append(SET_TEXT_COLOR_BLUE + "logout");
            sb.append(SET_TEXT_COLOR_WHITE + " - a user\n");
            sb.append(SET_TEXT_COLOR_BLUE + "quit");
            sb.append(SET_TEXT_COLOR_WHITE + " - the application\n");
            sb.append(SET_TEXT_COLOR_BLUE + "help");
            sb.append(SET_TEXT_COLOR_WHITE + " - with commands\n");
        } else {
            sb.append(SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>");
            sb.append(SET_TEXT_COLOR_WHITE + " - to create an account\n");
            sb.append(SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>");
            sb.append(SET_TEXT_COLOR_WHITE + " - to play chess\n");
            sb.append(SET_TEXT_COLOR_BLUE + "quit");
            sb.append(SET_TEXT_COLOR_WHITE + " - the application\n");
            sb.append(SET_TEXT_COLOR_BLUE + "help");
            sb.append(SET_TEXT_COLOR_WHITE + " - with commands\n");
        }
        return sb.toString();
    }

    public String drawBoard(ChessGame game, ChessGame.TeamColor orientationTeamColor, int[][] highlightArr) {
        StringBuilder sb = new StringBuilder();
        ChessBoard board = game.getBoard();
        drawHeader(sb, orientationTeamColor);
        if (orientationTeamColor == BLACK) {
            for (int row=1; row < 9; row++) {
                sb.append(SET_TEXT_BOLD + row  + " \u2009");
                for (int col=8; col > 0; col--) {
                    drawBoardHelper(sb, board, row, col, BLACK, highlightArr);
                }
                sb.append(RESET_BG_COLOR);
                sb.append(" \u2009" + SET_TEXT_BOLD + row);
                sb.append("\n");
            }

        } else {
            for (int row=8; row > 0; row--) {
                sb.append(SET_TEXT_BOLD + row + " \u2009");
                for (int col=1; col < 9; col++) {
                    drawBoardHelper(sb, board, row, col, WHITE, highlightArr);
                }
                sb.append(RESET_BG_COLOR);
                sb.append(" \u2009" + SET_TEXT_BOLD + row);
                sb.append("\n");
            }

        }
        drawHeader(sb, orientationTeamColor);
        return sb.toString();
    }

    private void drawBoardHelper(StringBuilder sb, ChessBoard board, int row, int col, ChessGame.TeamColor color, int[][] highlightArr) {
        String tileColor;
        if (highlightArr != null && highlightArr[row][col] != 0) {
            if (highlightArr[row][col] == 1) {
                tileColor = SET_BG_COLOR_GREEN;
            } else {
                tileColor = SET_BG_COLOR_YELLOW;
            }
        } else {
            if (color == WHITE) {
                if (col % 2 == 1) {
                    tileColor = row % 2 == 1 ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_WHITE;
                } else {
                    tileColor = row % 2 == 0 ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_WHITE;
                }
            } else {
                if (col % 2 == 1) {
                    tileColor = row % 2 == 1 ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_WHITE;
                } else {
                    tileColor = row % 2 == 0 ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_WHITE;
                }
            }
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
                    sb.append(WHITE_KING);
                } else {
                    sb.append(BLACK_KING);
                }
            }
            case QUEEN -> {
                if (pieceColor == WHITE) {
                    sb.append(WHITE_QUEEN);
                } else {
                    sb.append(BLACK_QUEEN);
                }
            }
            case BISHOP -> {
                if (pieceColor == WHITE) {
                    sb.append(WHITE_BISHOP);
                } else {
                    sb.append(BLACK_BISHOP);
                }
            }
            case KNIGHT -> {
                if (pieceColor == WHITE) {
                    sb.append(WHITE_KNIGHT);
                } else {
                    sb.append(BLACK_KNIGHT);
                }
            }
            case ROOK -> {
                if (pieceColor == WHITE) {
                    sb.append(WHITE_ROOK);
                } else {
                    sb.append(BLACK_ROOK);
                }
            }
            case PAWN -> {
                if (pieceColor == WHITE) {
                    sb.append(WHITE_PAWN);
                } else {
                    sb.append(BLACK_PAWN);
                }
            }
        }
    }

    private void drawHeader(StringBuilder sb, ChessGame.TeamColor color) {
        if (color == WHITE) {
            sb.append(" \u2009");
            sb.append(" \u2003" + SET_TEXT_BOLD + "a");
            sb.append(" \u2003" + "b");
            sb.append(" \u2003" + "c");
            sb.append(" \u2003" + "d");
            sb.append(" \u2003" + "e");
            sb.append(" \u2003" + "f");
            sb.append(" \u2003" + "g");
            sb.append(" \u2003" + "h\n");
        } else {
            sb.append(" \u2009");
            sb.append(" \u2003" + SET_TEXT_BOLD + "h");
            sb.append(" \u2003" + "g");
            sb.append(" \u2003" + "f");
            sb.append(" \u2003" + "e");
            sb.append(" \u2003" + "d");
            sb.append(" \u2003" + "c");
            sb.append(" \u2003" + "b");
            sb.append(" \u2003" + "a\n");
        }

    }
}
