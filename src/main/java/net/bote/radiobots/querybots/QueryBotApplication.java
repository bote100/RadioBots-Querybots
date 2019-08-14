package net.bote.radiobots.querybots;

import lombok.Getter;
import net.bote.radiobots.querybots.itself.QBManager;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.restapi.WebServerService;
import net.bote.radiobots.querybots.util.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

@Getter
public class QueryBotApplication {

    private final WebServerService webServerService;
    private Document config;
    private Connection mysqlConnection;

    @Getter
    private static QueryBotApplication instance;

    public QueryBotApplication() {
        instance = this;
        createConfig();
        this.webServerService = new WebServerService(config.getInt("restApiPort"));

        try {
            this.mysqlConnection = DriverManager.getConnection(
                    "jdbc:mysql://" + config.getString("mysqlHost") + ":3306/" + config.getString("mysqlDatabase") +
                            "?autoReconnect=true&serverTimezone=UTC", config.getString("mysqlUser"), config.getString("mysqlPassword")
            );
        } catch (SQLException e) { e.printStackTrace(); }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown RadioBotsEU QueryBot system...");
            webServerService.getHttpServer().stop(0);
            System.out.println("Exit all active query bots");
            QBManager.getAllBots(QueryBot::stop);
            try { mysqlConnection.close(); } catch (SQLException e) { e.printStackTrace(); }
            System.out.println("Have a good day!");
            try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
        }));

        System.out.println("Started successfully RadioBotsEU QueryBot System!");
        listen();
    }

    private void createConfig() {
        File file = new File("config.json");
        if(!file.exists()) {
            Document document = new Document()
                    .append("restApiPort", 48)
                    .append("mysqlHost", "localhost")
                    .append("mysqlUser", "root")
                    .append("mysqlDatabase", "querybots")
                    .append("mysqlPassword", "");
            document.saveAsConfig(file);
            this.config = document;
        }
        this.config = new Document().loadToExistingDocument(file);
    }

    private static void listen() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.length() == 0) return;

                if(line.equalsIgnoreCase("stop")) {
                    System.exit(0);
                }

            }
        } catch (Exception ex) {}
    }

}
