package net.bote.radiobots.querybots.modules.comp;

import net.bote.radiobots.querybots.QueryBotApplication;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.modules.Module;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Elias Arndt | bote100
 * Created on 28.07.2019
 */

public class BadNicknamesModule extends Module {

    public BadNicknamesModule(QueryBot queryBot) {
        super(queryBot);
    }

    @Override
    public String getName() {
        return "Nickname Check Modul";
    }

    @Override
    public String getUniqueName() {
        return "badnicks";
    }

    @Override
    public void resetVariables() { }

    @Override
    public void onEnable() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!getQueryBot().getTs3Query().isConnected()) return;
                getQueryBot().getTs3Api().getClients().stream()
                        .filter(client -> isBadNick(client.getNickname()))
                        .forEach(client -> getQueryBot().getTs3Api().kickClientFromServer("Nickname", client));
            }
        }, 0, 10000);
    }

    private boolean isBadNick(String nickname) {
        try {
            return QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery(
                    "SELECT * FROM badnicks WHERE id='" + getQueryBot().getUuid() + "' AND LOWER(nick) LIKE '" + nickname.toLowerCase() + "'"
            ).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
