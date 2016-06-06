package ru.ares4322.moneytransfer.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

//TODO move listen address, port other jetty config to properties
class HttpServer {

    private final Server server;

    public HttpServer(int port) {
        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setReuseAddress(true);
        server.setConnectors(new Connector[]{connector});

        WebAppContext webapp1 = new WebAppContext();
        webapp1.setResourceBase(".");
        webapp1.setContextPath("/");
        webapp1.setDefaultsDescriptor("web.xml");

        server.setHandler(webapp1);
        server.setStopAtShutdown(true);

        this.server = server;
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
