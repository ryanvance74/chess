package server;

import spark.Request;
import spark.Response;

class GameHandler {
    public Object getGame(Request req, Response res) {
        return "nothing";
    }

    public Object joinGame(Request req, Response res) {
        return "empty";
    }

    public Object createGame(Request req, Response res) {
        return "pass";
    }
}