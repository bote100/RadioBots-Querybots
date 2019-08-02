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
 * Created on 27.07.2019
 */

public class AFKBotModule extends Module {

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
                if(!getQueryBot().getTs3Query().isConnected()) return;
                getQueryBot().getTs3Api().getClients().stream()
                        .filter(c -> c.getIdleTime() > TimeUnit.MINUTES.toMillis(5))
                        .forEach(c -> {
                            if(getQueryBot().getTs3Api().getChannelsByName("AFK").size() > 0) {
                                if(c.getChannelId() == getQueryBot().getTs3Api().getChannelsByName("AFK").get(0).getId()) return;
                                getQueryBot().getTs3Api().moveClient(c.getId(), getQueryBot().getTs3Api().getChannelsByName("AFK").get(0).getId());
                            }
                            else {
                                if(c.getChannelId()==defaultChannel.getId()) return;
                                getQueryBot().getTs3Api().moveClient(c.getId(), defaultChannel.getId());
                            }

                            getQueryBot().getTs3Api().pokeClient(c.getId(), getPokeMessage());
                        });
            }
        }, 0, 10000);
    }

    private String pokeMessage;

    @Override
    public void resetVariables() {
        pokeMessage = null;
    }

    private String getPokeMessage() {
        if(Objects.nonNull(pokeMessage)) return pokeMessage;
        try {
            ResultSet rs = QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery(
                    "SELECT message FROM afk_poke_entity WHERE id='" + getQueryBot().getUuid() + "'"
            );
            rs.next();
            pokeMessage = rs.getString("message");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Switching to emergency wrong text!");
            pokeMessage = "Du wurdest in den AFK Channel, aufgrund zu langer Inaktivität, verschoben!";
        }
        return pokeMessage;
    }

}
