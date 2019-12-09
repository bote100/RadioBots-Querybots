package net.bote.radiobots.querybots.restapi;

import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import net.bote.radiobots.querybots.restapi.contexts.RestAPIContext;
import net.bote.radiobots.querybots.util.PackageUtils;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Elias Arndt | bote100
 * Created on 24.07.2019
 */

@Getter
public class WebServerService {

    private final int port;
    private HttpServer httpServer;

    public WebServerService(final int port) {
        this.port = port;
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            this.httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Started " + this.getClass().getSimpleName() + " on " + this.httpServer.getAddress().getHostString() + ":" + this.httpServer.getAddress().getPort());

        System.out.println("Loading restful contexts...");
        System.out.print("[");

        try {
            Thread.sleep(500);

            PackageUtils.performForClasses(RestAPIContext.class, clazz -> {
                try {
                    RestAPIContext context = clazz.newInstance();
                    this.httpServer.createContext("/" + context.getURLDirectory(), context);
                    System.out.print("##");
                    Thread.sleep(125);
                } catch (InstantiationException | IllegalAccessException | InterruptedException e) {
                    e.printStackTrace();
                }
            }, "net.bote.radiobots.querybots.restapi.contexts.comp");

            System.out.print("]");
            System.out.println(" Loaded all contexts!");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
