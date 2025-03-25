package server;
import java.net.*;
import java.io.*;
import com.google.gson.Gson;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import service.*;

import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public void clear() {
        this.makeRequest("DELETE", "/db", null, Void.class, null);
    }

    // returns authToken
    public RegisterResult register(RegisterRequest req) {
        return this.makeRequest("POST", "/user", req, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest req) {
        return this.makeRequest("POST", "/session", req, LoginResult.class, null);
    }

    public void logout(LogoutRequest req) {
        Map<String, String> map = new HashMap<>();
        map.put("authorization", req.authToken());
        this.makeRequest("DELETE", "/session", null, Void.class, map);
    }

    public ListGamesResult listGames(String authToken) {
        Map<String, String> map = new HashMap<>();
        map.put("authorization", authToken);
        return this.makeRequest("GET", "/game", null, ListGamesResult.class, map);
    }

    public CreateGameResult createGame(CreateGameRequest req) {
        Map<String, String> map = new HashMap<>();
        map.put("authorization", req.authToken());
        return this.makeRequest("POST", "/game", req, CreateGameResult.class, map);
    }

    public void joinGame(UpdateGameRequest req) {
        Map<String, String> map = new HashMap<>();
        map.put("authorization", req.authToken());
        this.makeRequest("PUT", "/game", req, CreateGameResult.class, map);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, Map<String, String> headers) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (headers != null) {
                addHeaders(http, headers);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            if (responseClass == Void.class) return null;
            return readBody(http, responseClass);
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void addHeaders(HttpURLConnection http, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            http.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(respErr))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        throw new ResponseException(status, sb.toString());
                    }
//                    throw new ResponseException(500, respErr.toString());
                }

            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
