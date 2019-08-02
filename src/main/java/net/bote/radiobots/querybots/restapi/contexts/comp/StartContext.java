package net.bote.radiobots.querybots.restapi.contexts.comp;

import com.sun.net.httpserver.HttpExchange;
import net.bote.radiobots.querybots.itself.QBManager;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

public class StartContext extends RestAPIContext {

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
            if(QBManager.isInitalized(Integer.parseInt(stringID))) {
                sendResponse(new JSONObject().put("success", false).put("data", "Bot is already started!").toString(), httpExchange);
                return;
            }
            queryBot = QBManager.getQueryBot(Integer.parseInt(stringID));
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(new JSONObject().put("success", false).put("data", e.getMessage()).toString(), httpExchange);
            return;
        }

        sendResponse(new JSONObject().put("success", true).put("host", queryBot.getDocument().getString("host")).toString(), httpExchange);
    }
}
