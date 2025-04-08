import client.GameClient;
import ui.BadRepl;
import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        GameClient client = new GameClient(serverUrl);

        String nextRepl = "preGame";
        do {
            if (nextRepl.equals("preGame")) {
                nextRepl = new Repl(client).run();
            } else {
                nextRepl = new BadRepl(client).run();
            }
        } while (nextRepl.equals("preGame") || nextRepl.equals("inGame"));


    }

}