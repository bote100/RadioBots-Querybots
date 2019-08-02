package net.bote.radiobots.querybots.restapi.contexts.comp;

import com.sun.net.httpserver.HttpExchange;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;

import java.util.Collections;
import java.util.List;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

public class DevContext extends RestAPIContext {

    @Override
    public String getURLDirectory() {
        return "dev";
    }

    @Override
    public List<String> getRequiredKeys() {
        return Collections.emptyList();
    }

    @Override
    public void handle(HttpExchange httpExchange) {

        StringBuilder builder = new StringBuilder();

        httpExchange.getRequestHeaders().forEach((s, list) -> builder.append(" | s = "+s+": "+list.toString()+" | ; \n"));
        sendResponse(builder.toString(), httpExchange);
    }
}
