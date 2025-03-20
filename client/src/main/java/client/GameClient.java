package client;
import server.ServerFacade;

public class GameClient {
    private final ServerFacade serverFacade;
    public GameClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
    }
    public String eval() {

    }
}
