package net.bote.radiobots.querybots.restapi.contexts.comp;

import com.sun.net.httpserver.HttpExchange;
import net.bote.radiobots.querybots.itself.QBManager;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

public class StatusContext extends RestAPIContext {

    @Override
    public String getURLDirectory() {
        return "status";
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
            if(!QBManager.isInitalized(Integer.parseInt(stringID))) {
                sendResponse(new JSONObject().put("success", false).put("data", "Bot isn't online!").toString(), httpExchange);
                return;
            }
            queryBot = QBManager.getQueryBot(Integer.parseInt(stringID));

            sendResponse(new JSONObject()
                    .put("success", true)
                    .put("host", queryBot.getDocument().getString("host"))
                    .put("name", queryBot.getNickname())
                    .put("onlineClients", queryBot.getTs3Api().getClients().size())
                    .put("activeModules", queryBot.getDocument().getString("modules"))
                    .put("id", queryBot.getUuid())
                    .toString()
                    , httpExchange);
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            sendResponse(new JSONObject().put("success", false).put("data", e.getMessage()).toString(), httpExchange);
            return;
        }
    }
}
