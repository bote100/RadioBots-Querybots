package net.bote.radiobots.querybots.restapi.contexts;

import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.bote.radiobots.querybots.QueryBotApplication;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

public abstract class RestAPIContext implements HttpHandler {

    private HashMap<HttpExchange, HashMap<String, String>> savedParams = Maps.newHashMap();

    public ExecutorService executor = Executors.newCachedThreadPool();

    public abstract String getURLDirectory();

    public abstract List<String> getRequiredKeys();

    public boolean checkParams(HttpExchange exchange) {

        if (!isHeaderSet("apikey", exchange)) {
            sendResponse(new JSONObject()
                            .put("success", false)
                            .put("data", "Missing API key!").toString()
                    , exchange);
            return false;
        }

        for (String current : getRequiredKeys()) {
            if (!isHeaderSet(current, exchange)) {
                sendResponse(new JSONObject()
                                .put("success", false)
                                .put("data", "Missing parameter: " + current).toString()
                        , exchange);
                return false;
            }
        }
        return true;
    }

    public void sendResponse(String response, HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(200, 0);
            try (BufferedOutputStream out = new BufferedOutputStream(httpExchange.getResponseBody())) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(response.getBytes())) {
                    byte[] buffer = response.getBytes();
                    int count;
                    while ((count = bis.read(buffer)) != -1) out.write(buffer, 0, count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isHeaderSet(String key, HttpExchange httpExchange) {
        return getParams(httpExchange).containsKey(key);
    }

    public String getHeaderVal(HttpExchange exchange, String key) {
        return getParams(exchange).get(key);
    }

    public boolean checkAccess(HttpExchange exchange, int botid) {

        return getParams(exchange).getOrDefault("apikey", "").equals(QueryBotApplication.getInstance().getConfig().getString("masterPassword"));

//        try {
//            return QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery(
//                    "SELECT uuid FROM query_bot_entity WHERE apikey='"+getParams(exchange).getOrDefault("apikey", "")+"'" +
//                            "AND uuid='" + botid + "'"
//            ).next();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//      return false;
    }

    public HashMap<String, String> getParams(HttpExchange httpExchange) {
        if (savedParams.containsKey(httpExchange)) return savedParams.get(httpExchange);
        HashMap<String, String> map = Maps.newHashMap();

        JSONObject jsonObject = new JSONObject(httpExchange.getRequestHeaders().get("data").get(0));
        jsonObject.keys().forEachRemaining(s -> map.put(s, String.valueOf(jsonObject.get(s))));

        //httpExchange.getRequestHeaders().forEach((s, list) -> map.put(s.toLowerCase(), list.get(0)));
        savedParams.put(httpExchange, map);
        return map;
    }

}
