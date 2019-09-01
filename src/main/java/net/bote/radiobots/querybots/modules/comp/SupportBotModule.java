package net.bote.radiobots.querybots.modules.comp;

import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.google.common.collect.Maps;
import net.bote.radiobots.querybots.QueryBotApplication;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.modules.Module;
import net.bote.radiobots.querybots.util.MapBuilder;
import net.bote.radiobots.querybots.util.MapPair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

/**
 * @author Elias Arndt | bote100
 * Created on 28.07.2019
 */

public class SupportBotModule extends Module {

    public SupportBotModule(QueryBot queryBot) { super(queryBot); }

    private Map<String, String> vals;

    private Map<Integer, Channel> channelMap = Maps.newHashMap();

    @Override
    public String getName() {
        return "SupportBot Modul";
    }

    @Override
    public String getUniqueName() {
        return "supportbot";
    }

    @Override
    public void resetVariables() {
        vals = null;
        channelMap.clear();
    }

    @Override
    public void onClientSwitchChannel(ClientMovedEvent event) {
        Map<String, String> infos = getSupportInfos();

        if(channelMap.size() != getQueryBot().getTs3Api().getChannels().size()) {
            channelMap.clear();
            for (Channel channel : getQueryBot().getTs3Api().getChannels()) channelMap.put(channel.getId(), channel);
        }

        if (channelMap.get(event.getTargetChannelId()).getTopic().equalsIgnoreCase("SUPPORT_WAITING")) {
            getQueryBot().getTs3Api().getClients().stream()
                    .filter(client -> client.isInServerGroup(Integer.parseInt(infos.get("group"))))
                    .forEach(client -> getQueryBot().getTs3Api().pokeClient(client.getId(), infos.get("message")));
        }
    }

    private Map<String, String> getSupportInfos() {

        if(Objects.nonNull(vals)) return vals;

        try {

            ResultSet resultSet = QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery(
                    "SELECT message, tsgroup FROM support_bot_entity WHERE id='" + getQueryBot().getUuid() + "'"
            );
            resultSet.next();

            vals = MapBuilder.buildStringMap(
                    new MapPair("message", resultSet.getString("message")),
                    new MapPair("group", String.valueOf(resultSet.getInt("tsgroup")))
            );

            return vals;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
