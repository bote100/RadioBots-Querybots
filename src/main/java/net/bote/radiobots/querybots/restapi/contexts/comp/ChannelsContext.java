package net.bote.radiobots.querybots.restapi.contexts.comp;

import com.sun.net.httpserver.HttpExchange;
import net.bote.radiobots.querybots.itself.QBManager;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Elias Arndt | bote100
 * Created on 09.09.2019
 */

public class ChannelsContext extends RestAPIContext {

    @Override
    public String getURLDirectory() {
        return "channels";
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
                sendResponse(new JSONObject().put("success", false).put("data", "Bot isn't running!").toString(), httpExchange);
                return;
            }

            QueryBot bot = QBManager.getQueryBot(Integer.parseInt(stringID));
            JSONObject jsonObject = new JSONObject();
            AtomicInteger channelCounter = new AtomicInteger(0);
            bot.getChannelMap().entrySet().stream().sorted(Comparator.comparing(gab -> gab.getValue().getOrder()))
                    .forEach(gab -> jsonObject.put(channelCounter.addAndGet(1)+"", gab.getKey() + ";" + gab.getValue().getName()));
            sendResponse(jsonObject.toString(), httpExchange);

        } catch (Exception nfe) {
            sendResponse(new JSONObject().put("success", false).put("data", nfe.getMessage()).toString(), httpExchange);
            return;
        }
    }
}
