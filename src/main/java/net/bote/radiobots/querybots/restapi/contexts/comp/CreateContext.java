package net.bote.radiobots.querybots.restapi.contexts.comp;

import com.sun.net.httpserver.HttpExchange;
import net.bote.radiobots.querybots.QueryBotApplication;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Elias Arndt | bote100
 * Created on 12.08.2019
 */

public class CreateContext extends RestAPIContext {

    @Override
    public String getURLDirectory() {
        return "create";
    }

    @Override
    public List<String> getRequiredKeys() {
        return Arrays.asList("name", "querypw", "queryuser", "server", "nickname");
    }

    @Override
    public void handle(HttpExchange hex) {

        if(!checkParams(hex)) return;

        int botid = -1;

        try {
            QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeUpdate(
                    "INSERT INTO query_bot_entity (apikey, modules, query_password, query_user, server, nickname) VALUES " +
                            "('"+getHeaderVal(hex, "apikey")+"', '[\"welcome\"]', '"+getHeaderVal(hex, "querypw")+"'," +
                            "'"+getHeaderVal(hex, "queryuser")+"', '"+getHeaderVal(hex, "server")+"', '"+getHeaderVal(hex, "nickname")+"')"
            );

            ResultSet set = QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery(
                    "SELECT uuid FROM query_bot_entity ORDER BY uuid DESC LIMIT 1"
            );

            if(set.next())
                botid = set.getInt("uuid");

        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(new JSONObject().put("success", false).put("data", e.getMessage()).toString(), hex);
            return;
        }

        sendResponse(new JSONObject().put("success", true).put("bot", botid).toString(), hex);

    }
}
