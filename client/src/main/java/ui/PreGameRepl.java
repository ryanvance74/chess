package ui;
import client.GameClient;

import java.util.Scanner;

public class PreGameRepl {
    private final GameClient client;

    public PreGameRepl(GameClient client) {this.client = client;}

    public String run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        ClientResult result = new ClientResult("", false);
        while (!result.resultText().equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
                if (result.switchRepl()) {
                    return "inGame";
                }
            } catch (Throwable e) {

                var msg = e.toString();
                System.out.print(msg + "***FAILED TO EVALUATE***");
            }
        }
        System.out.println();
        return "";
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_GREEN + ">>> ");
    }
}
