package ui;
import client.GameClient;

public class PostLoginRepl implements PreGameRepl{
        private final GameClient client;
    public PostLoginRepl(String serverUrl, GameClient client) {
        this.client = client;
    }
    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        System.out.print(client.help());
    }

    public void communicate() {

    }
}
