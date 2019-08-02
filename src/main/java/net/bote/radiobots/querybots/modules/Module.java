package net.bote.radiobots.querybots.modules;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.bote.radiobots.querybots.itself.QueryBot;

/**
 * @author Elias Arndt | bote100
 * Created on 22.07.2019
 */

@Getter
@RequiredArgsConstructor
public abstract class Module {

    private final QueryBot queryBot;

    public abstract String getName();

    public abstract String getUniqueName();

    public abstract void resetVariables();

    public boolean useableAsFree() { return true; }

    public void onClientJoin(ClientJoinEvent event){}

    public void onClientLeave(ClientLeaveEvent event){}

    public void onClientSwitchChannel(ClientMovedEvent event){}

    public void onTextMessage(TextMessageEvent event){}

    public void onEnable() {}

    public void onDisable() {}

}
