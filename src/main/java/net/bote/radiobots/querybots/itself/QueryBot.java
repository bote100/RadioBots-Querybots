package net.bote.radiobots.querybots.itself;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.bote.radiobots.querybots.modules.Module;
import net.bote.radiobots.querybots.modules.ModuleService;
import net.bote.radiobots.querybots.util.Document;
import org.json.JSONArray;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * @author Elias Arndt | bote100
 * Created on 22.07.2019
 */

@Getter
@ToString
public class QueryBot {

    private final int uuid;
    private final TS3Config ts3Config;
    private final TS3Api ts3Api;
    private final TS3Query ts3Query;
    private final Document document;
    private final String location;

    @Setter
    private String nickname;

    private final List<Module> moduleList;

    private final Map<Integer, Channel> channelMap = Maps.newHashMap();

    public QueryBot(int uid, Document document, String location) {
        this.uuid = uid;
        this.document = document;
        this.location = location;
        this.moduleList = ModuleService.getModules(this);

        this.ts3Config = new TS3Config();
        this.ts3Config.setHost(document.getString("host"));
        this.ts3Config.setEnableCommunicationsLogging(true);

        this.ts3Query = new TS3Query(this.ts3Config);
        this.ts3Query.connect();

        this.ts3Api = ts3Query.getApi();
        this.ts3Api.login(document.getString("user"), new String(Base64.getDecoder().decode(document.getString("password")), StandardCharsets.UTF_8));
        this.ts3Api.selectVirtualServerById(1);

        try {
            ts3Api.setNickname(document.getString("name"));
            this.nickname = document.getString("name");
        } catch (TS3CommandFailedException ex) {
            ex.printStackTrace();
            this.nickname = "RadioBotsEU Querybot #" + ThreadLocalRandom.current().nextInt(999);
            setNickname(this.nickname);
        }

        System.out.println("Querybot#" + uid + " ("+this.nickname+") <=> started <=> " + document.getString("host"));

        ts3Api.registerAllEvents();
        ts3Api.addTS3Listeners(new TS3EventAdapter() {
            @Override public void onClientJoin(ClientJoinEvent e) { getActiveModules().forEach(module -> module.onClientJoin(e)); }
            @Override public void onClientLeave(ClientLeaveEvent e) { getActiveModules().forEach(module -> module.onClientLeave(e)); }
            @Override public void onClientMoved(ClientMovedEvent e) { getActiveModules().forEach(module -> module.onClientSwitchChannel(e)); }
            @Override public void onTextMessage(TextMessageEvent e) { getActiveModules().forEach(module -> module.onTextMessage(e)); }
        });

        getActiveModules().forEach(Module::onEnable);

    }

    public void stop() {
        System.out.println("Querybot#" + this.uuid + " ("+getNickname()+") <=> disconnecting <=> " + getDocument().getString("host"));
        getTs3Query().exit();
        QBManager.removeBot(getUuid());
    }

    public Map<Integer, Channel> getChannelMap() {
        if(getTs3Api().getChannels().size() == channelMap.size()) return channelMap;

        channelMap.clear();
        for (Channel channel : getTs3Api().getChannels()) channelMap.put(channel.getId(), channel);
        return channelMap;
    }

    public void ping(Consumer<Integer> callback) {
        Executors.newCachedThreadPool().execute(() -> {
            long start = System.currentTimeMillis();
            getTs3Api().getServerInfo().getIp();
            callback.accept((int)(System.currentTimeMillis() - start));
        });
    }

    private List<Module> activeModules;

    public List<Module> getActiveModules() {
        if(!Objects.isNull(activeModules)) return activeModules;
        JSONArray array = new JSONArray(this.getDocument().getString("modules"));
        List<Module> list = new ArrayList<>();
        System.out.println(array.toString());
        for(int i = 0; i < array.length(); i++)
            for(Module current : moduleList)
                if(current.getUniqueName().equals(array.getString(i))) {
                    list.add(current);
                    System.out.println("Added module to #" + uuid + " <=> " + current.getName());
                }
        activeModules = new ArrayList<>(list);
        return list;
    }

}
