package net.bote.radiobots.querybots.itself;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.bote.radiobots.querybots.QueryBotApplication;
import net.bote.radiobots.querybots.util.Document;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

@Getter
public class QBManager {

    private static HashMap<Integer, QueryBot> onlineBots = Maps.newHashMap();

    public static QueryBot getQueryBot(int uid) throws SQLException {
        if (onlineBots.containsKey(uid)) return onlineBots.get(uid);
        ResultSet resultSet = QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery(
                "SELECT * FROM query_bot_entity WHERE uuid='" + uid + "'"
        );
        resultSet.next();
        Document doc = new Document()
                .append("host", resultSet.getString("server"))
                .append("user", resultSet.getString("query_user"))
                .append("password", resultSet.getString("query_password"))
                .append("name", resultSet.getString("nickname"))
                .append("modules", resultSet.getString("modules"));

        QueryBot queryBot = new QueryBot(uid, doc);
        onlineBots.put(uid, queryBot);
        return queryBot;
    }

    public static void removeBot(int uid) {
        onlineBots.remove(uid);
    }

    public static void getAllBots(Consumer<QueryBot> consumer) {
        for (Map.Entry<Integer, QueryBot> entry : onlineBots.entrySet()) consumer.accept(entry.getValue());
    }

    public static boolean isInitalized(int uid) {
        return onlineBots.containsKey(uid);
    }

}
