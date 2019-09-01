package net.bote.radiobots.querybots.restapi.contexts.comp;

import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;
import net.bote.radiobots.querybots.itself.QBManager;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

public class StartContext extends RestAPIContext {

    private HashMap<String, Integer> wrongLogins = Maps.newHashMap();

    @Override
    public String getURLDirectory() {
        return "start";
    }

    @Override
    public List<String> getRequiredKeys() {
        return Collections.singletonList("id");
    }

    @Override
    public void handle(HttpExchange httpExchange) {

        if(!checkParams(httpExchange)) return;

        String stringID = getHeaderVal(httpExchange, "id");

        QueryBot queryBot;
        try {

            if(wrongLogins.getOrDefault(getHeaderVal(httpExchange, "apikey"), 0) >= 3) {
                sendResponse(new JSONObject().put("success", false).put("data", "You tried to login too often!").toString(), httpExchange);
                return;
            }

            if(!checkAccess(httpExchange, Integer.parseInt(stringID))) {
                sendResponse(new JSONObject().put("success", false).put("data", "This is not your bot!").toString(), httpExchange);
                return;
            }

            if(QBManager.isInitalized(Integer.parseInt(stringID))) {
                sendResponse(new JSONObject().put("success", false).put("data", "Bot is already started!").toString(), httpExchange);
                return;
            }
            queryBot = QBManager.getQueryBot(Integer.parseInt(stringID));
        } catch (Exception e) {

            if(Objects.isNull(e.getMessage())) e.printStackTrace();
            else System.err.println(e.getMessage());

            wrongLogins.put(getHeaderVal(httpExchange, "apikey"), wrongLogins.getOrDefault(getHeaderVal(httpExchange, "apikey"), 0) + 1);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            wrongLogins.put(getHeaderVal(httpExchange, "apikey"), wrongLogins.getOrDefault(getHeaderVal(httpExchange, "apikey"), 1) - 1);
                        }
                    },
                    60000
            );

            sendResponse(new JSONObject().put("success", false).put("data", e.getMessage()).put("left", (3 - wrongLogins.get(getHeaderVal(httpExchange, "apikey")))).toString(), httpExchange);
            return;
        }

        sendResponse(new JSONObject().put("success", true).put("host", queryBot.getDocument().getString("host")).toString(), httpExchange);
    }
}
