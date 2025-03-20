package ui;

public class PostLoginRepl implements PreGameRepl{
        private final GameClient client;
    public PostLoginRepl(String serverUrl) {
        this.client = new GameClient(serverUrl);
    }
    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        System.out.print(client.help());
    }

    public void communicate() {

    }
}
