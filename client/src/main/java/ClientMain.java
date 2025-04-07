import client.GameClient;
import ui.GameRepl;
import ui.PreGameRepl;

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
                nextRepl = new PreGameRepl(client).run();
            } else {
                nextRepl = new GameRepl(client).run();
            }
        } while (nextRepl.equals("preGame") || nextRepl.equals("inGame"));


    }

}