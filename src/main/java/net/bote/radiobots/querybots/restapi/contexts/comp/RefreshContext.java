package net.bote.radiobots.querybots.restapi.contexts.comp;

import com.sun.net.httpserver.HttpExchange;
import net.bote.radiobots.querybots.itself.QBManager;
import net.bote.radiobots.querybots.modules.Module;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

public class RefreshContext extends RestAPIContext {

    @Override
    public String getURLDirectory() {
        return "refresh";
    }

    @Override
    public List<String> getRequiredKeys() {
        return Collections.singletonList("id");
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        if(!checkParams(httpExchange)) return;

        String stringID = getHeaderVal(httpExchange, "id");

        try {

            if(!checkAccess(httpExchange, Integer.parseInt(stringID))) {
                sendResponse(new JSONObject().put("success", false).put("data", "This is not your bot!").toString(), httpExchange);
                return;
            }

            if(!QBManager.isInitalized(Integer.parseInt(stringID))) {
                sendResponse(new JSONObject().put("success", false).put("data", "Bot isn't online!").toString(), httpExchange);
                return;
            }
            executor.execute(() -> {
                try {
                    QBManager.getQueryBot(Integer.parseInt(stringID)).getActiveModules().forEach(Module::resetVariables);
                } catch (SQLException e) {
                    e.printStackTrace();
                    sendResponse(new JSONObject().put("success", false).put("data", e.getMessage()).toString(), httpExchange);
                    return;
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
