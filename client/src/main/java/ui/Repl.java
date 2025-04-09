package ui;
import chess.ChessGame;
import client.GameClient;
import client.websocket.NotificationHandler;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final GameClient client;

    public Repl(String serverUrl) {
        this.client = new GameClient(serverUrl, this);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg + "***FAILED TO EVALUATE***");
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage notification) {
        System.out.println("received notification from server");
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            // TODO hard coded perspective as white for now.
            System.out.println(client.drawBoard(((LoadGameMessage) notification).getGame(), ChessGame.TeamColor.WHITE, null ));
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + notification.toString());
        }

        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_GREEN + ">>> ");
    }
}
