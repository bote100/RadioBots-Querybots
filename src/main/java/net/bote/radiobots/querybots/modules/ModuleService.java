package net.bote.radiobots.querybots.modules;

import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.modules.comp.AFKBotModule;
import net.bote.radiobots.querybots.modules.comp.BadNicknamesModule;
import net.bote.radiobots.querybots.modules.comp.SupportBotModule;
import net.bote.radiobots.querybots.modules.comp.WelcomeMessageModule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Elias Arndt | bote100
 * Created on 23.07.2019
 */

public class ModuleService {

    public static List<Module> getModules(QueryBot queryBot) {
        List<Module> list = new ArrayList<>();
        list.add(new WelcomeMessageModule(queryBot));
        list.add(new AFKBotModule(queryBot));
        list.add(new BadNicknamesModule(queryBot));
        list.add(new SupportBotModule(queryBot));
        return list;
    }

}
