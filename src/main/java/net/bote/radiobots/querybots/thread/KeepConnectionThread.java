package net.bote.radiobots.querybots.thread;

import net.bote.radiobots.querybots.QueryBotApplication;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Elias Arndt | bote100
 * Created on 09.12.2019
 */

public class KeepConnectionThread extends Thread {

    @Override
    public void run() {
        long nextRequest = System.currentTimeMillis();
        while (Objects.nonNull(QueryBotApplication.getInstance().getMysqlConnection())) {
            if (nextRequest <= System.currentTimeMillis()) {
                try {
                    QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery("SELECT * FROM query_bot_entity LIMIT 1");
                    System.out.println("Send query to database to keep connection alive");
                    nextRequest = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Interrupted alive-system!");
    }
}
