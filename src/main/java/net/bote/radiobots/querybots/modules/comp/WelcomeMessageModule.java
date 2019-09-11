package net.bote.radiobots.querybots.modules.comp;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import net.bote.radiobots.querybots.QueryBotApplication;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.modules.Module;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Objects;


/**
 * @author Elias Arndt | bote100
 * Created on 22.07.2019
 */

public class WelcomeMessageModule extends Module {

    public WelcomeMessageModule(QueryBot queryBot) {
        super(queryBot);
    }

    @Override
    public String getName() {
        return "Welcome Message Modul";
    }

    @Override
    public String getUniqueName() {
        return "welcome";
    }

    @Override
    public void resetVariables() {
        welcomeMessage = null;
    }

    @Override
    public void onClientJoin(ClientJoinEvent event) {
        getQueryBot().getTs3Api().sendPrivateMessage(event.getClientId(), getMessage());
    }

    private String welcomeMessage;

    private String getMessage() {
        if(Objects.nonNull(welcomeMessage)) return welcomeMessage;
        try {
            ResultSet rs = QueryBotApplication.getInstance().getMysqlConnection().createStatement().executeQuery(
                    "SELECT welcomemessage FROM query_bot_entity WHERE uuid='" + getQueryBot().getUuid() + "'"
            );
            rs.next();
            welcomeMessage = rs.getString("welcomemessage").replace("%time%", DateFormat.getTimeInstance(DateFormat.SHORT).format(new GregorianCalendar().getTime()));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Switching to emergency wrong text!");
            welcomeMessage = "[color=red]Welcome on our teamspeak!";
        }
        return welcomeMessage;
    }

}
