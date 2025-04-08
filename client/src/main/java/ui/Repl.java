package ui;
import client.GameClient;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class Repl {
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
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + notification.toString());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_GREEN + ">>> ");
    }
}
