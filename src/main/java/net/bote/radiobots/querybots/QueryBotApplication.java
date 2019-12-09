package net.bote.radiobots.querybots;

import lombok.Getter;
import net.bote.radiobots.querybots.itself.QBManager;
import net.bote.radiobots.querybots.itself.QueryBot;
import net.bote.radiobots.querybots.restapi.WebServerService;
import net.bote.radiobots.querybots.thread.KeepConnectionThread;
import net.bote.radiobots.querybots.util.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
            //System.out.println("[DEBUG] The password has " + config.getString("mysqlPassword").toCharArray().length + " characters.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown RadioBotsEU QueryBot system...");
            webServerService.getHttpServer().stop(0);
            System.out.println("Exit all active query bots");
            QBManager.getAllBots(QueryBot::stop);
            try {
                mysqlConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Have a good day!");
        }));

        System.out.println("Started successfully RadioBotsEU QueryBot System!");

        final Properties properties = new Properties();
        try {
            properties.load(getInstance().getClass().getClassLoader().getResourceAsStream("app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Running RadioBotsEU QueryBots Software by bote100 | Elias on " + properties.getProperty("version") + " !");
        System.out.println("Now waiting for incoming signals...");

        keepDatabaseAlive();
        listen();
    }

    private void createConfig() {
        File file = new File("config.json");
        if (!file.exists()) {
            Document document = new Document()
                    .append("restApiPort", 48)
                    .append("masterPassword", "masterPassword")
                    .append("locationNick", "DE_FFM")
                    .append("mysqlHost", "localhost")
                    .append("mysqlUser", "root")
                    .append("mysqlDatabase", "querybots")
                    .append("mysqlPassword", "");
            document.saveAsConfig(file);
            this.config = document;
        }
        this.config = new Document().loadToExistingDocument(file);
    }

    private void keepDatabaseAlive() {
        new KeepConnectionThread().start();
    }

    private static void listen() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0) return;

                if (line.equalsIgnoreCase("stop")) {
                    System.exit(0);
                }
//                else if (line.equalsIgnoreCase("stats")) {
//                    AtomicInteger allBots = new AtomicInteger(0);
//                    QBManager.getAllBots(queryBot -> allBots.addAndGet(1));
//                    System.out.println("Currently are " + allBots.get() + " QueryBots active.");
//
//                    if (QBManager.getOnlineBots().size() == 0) return;
//
//                    System.out.println("Ping random QueryBot...");
//                    QueryBot queryBot = QBManager.getOnlineBots().get(ThreadLocalRandom.current().nextInt(QBManager.getOnlineBots().size()));
//                    queryBot.ping(pingInt -> System.out.println("QueryBots #" + queryBot.getNickname() + " connected to " + queryBot.getDocument().getString("host") + " took " + pingInt + "ms."));
//                }

            }
        } catch (Exception ex) { }
    }

}
