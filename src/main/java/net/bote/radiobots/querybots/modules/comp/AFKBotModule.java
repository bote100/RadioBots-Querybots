package net.bote.radiobots.querybots.modules.comp;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import net.bote.radiobots.querybots.QueryBotApplication;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.modules.Module;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Elias Arndt | bote100
 * Created on 27.07.2019gamecloud_invoices
 */

public class AFKBotModule extends Module {

    private long idletime = -991231231141241L;

    public AFKBotModule(QueryBot queryBot) {
        super(queryBot);
    }

    @Override
    public String getName() {
        return "AFK Mover Modul";
    }

    @Override
    public String getUniqueName() {
        return "afkbot";
    }

    @Override
    public void onEnable() {
        Channel defaultChannel = getQueryBot().getTs3Api().getChannels().stream().filter(ChannelBase::isDefault).findFirst().get();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!getQueryBot().getTs3Query().isConnected()) return;
                getQueryBot().getTs3Api().getClients().stream()
                        .filter(c -> c.getIdleTime() > getIdleTime())
                        .forEach(c -> {

                            Channel afk = null;

                            try {
                                afk = getQueryBot().getTs3Api().getChannels().stream()
                                        .filter(chan -> chan.getTopic().equals("AFK_CHANNEL"))
                                        .findFirst().orElseGet(null);
                            } catch (NullPointerException nex) { }

                            if (Objects.isNull(afk)) {
                                if (c.getChannelId() == defaultChannel.getId()) return;
                                getQueryBot().getTs3Api().moveClient(c.getId(), defaultChannel.getId());
                            } else {
                                if (c.getChannelId() == getQueryBot().getTs3Api().getChannelsByName("AFK").get(0).getId())
                                    return;
                                getQueryBot().getTs3Api().moveClient(c.getId(), afk.getId());
                            }
                            try {
                                getQueryBot().getTs3Api().pokeClient(c.getId(), getPokeMessage().replace("%channel%", Objects.isNull(afk) ? defaultChannel.getName() : afk.getName()));
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                            }
                        });
            }
        }, 0, 10000);
    }

    private String pokeMessage;

    @Override
    public void resetVariables() {
        pokeMessage = null;
        idletime = -991231231141241L;
    }

    private long getIdleTime() {
        if (idletime != -991231231141241L) return idletime;
        try {
            ResultSet rs = QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery(
                    "SELECT afk_idle_time FROM query_bot_entity WHERE uuid='" + getQueryBot().getUuid() + "'"
            );
            rs.next();
            idletime = TimeUnit.SECONDS.toMillis(rs.getInt("afk_idle_time"));
            return TimeUnit.SECONDS.toMillis(rs.getInt("afk_idle_time"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        idletime = TimeUnit.MINUTES.toMillis(5);
        return TimeUnit.MINUTES.toMillis(5);
    }

    private String getPokeMessage() {
        if (Objects.nonNull(pokeMessage)) return pokeMessage;
        try {
            ResultSet rs = QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery(
                    "SELECT afkmessage FROM query_bot_entity WHERE uuid='" + getQueryBot().getUuid() + "'"
            );
            rs.next();
            pokeMessage = rs.getString("afkmessage");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Switching to emergency wrong text!");
            pokeMessage = "Du wurdest in den AFK Channel, aufgrund zu langer Inaktivität, verschoben!";
        }
        return pokeMessage;
    }

}
