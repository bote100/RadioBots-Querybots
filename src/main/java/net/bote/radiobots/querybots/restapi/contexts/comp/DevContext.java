package net.bote.radiobots.querybots.restapi.contexts.comp;

import com.google.common.collect.Lists;
import com.sun.net.httpserver.HttpExchange;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;

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
        return Lists.newArrayList();
    }

    @Override
    public void handle(HttpExchange httpExchange) {

        if(!checkParams(httpExchange)) return;

        sendResponse( checkAccess(httpExchange, 1) + "", httpExchange);
    }
}
