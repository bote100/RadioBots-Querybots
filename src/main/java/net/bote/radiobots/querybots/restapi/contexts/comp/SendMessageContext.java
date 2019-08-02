package net.bote.radiobots.querybots.restapi.contexts.comp;

import com.sun.net.httpserver.HttpExchange;
import net.bote.radiobots.querybots.itself.QBManager;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

public class SendMessageContext extends RestAPIContext {

    @Override
    public String getURLDirectory() {
        return "sendmessage";
    }

    @Override
    public List<String> getRequiredKeys() {
        return Arrays.asList("id", "user", "message");
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
            executor.execute(() -> {
                try {
                    QBManager.getQueryBot(Integer.parseInt(stringID)).
                            getTs3Api().sendPrivateMessage(Integer.parseInt(getHeaderVal(httpExchange, "user")),
                            getHeaderVal(httpExchange, "message"));
                } catch (SQLException e) {
                    e.printStackTrace();
                    sendResponse(new JSONObject().put("success", false).put("data", e.getMessage()).toString(), httpExchange);
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendResponse(new JSONObject().put("success", false).put("data", e.getMessage()).toString(), httpExchange);
            return;
        }
        sendResponse(new JSONObject().put("success", true).toString(), httpExchange);
    }
}
