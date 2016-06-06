package ru.ares4322.moneytransfer.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        int port = 8080;
        HttpServer httpServer = new HttpServer(port);
        try {
            httpServer.start();
            log.info("server started; port: {}", port);
        } catch (InterruptedException e) {
            log.info("server interrupted;", e);
            try {
                httpServer.stop();
            } catch (Exception e1) {
                log.error("error during server stop;", e);
            }
        } catch (Exception e) {
            log.error("error during server work;", e);
        }
    }

}
