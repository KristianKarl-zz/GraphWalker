package org.graphwalker.webrenderer;

import org.junit.Test;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.handler.HttpToWebSocketHandler;
import org.webbitserver.handler.StaticFileHandler;
import org.webbitserver.handler.exceptions.PrintStackTraceExceptionHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * Created by nilols on 27/01/14.
 */
public class WebRendererTest {

    @Test
    public void simpleTest() throws Exception {

    }

    static int port = 8080;

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, URISyntaxException {

        WebServer server = WebServers.createWebServer(8080);
        server.uncaughtExceptionHandler(new PrintStackTraceExceptionHandler());
        server.connectionExceptionHandler(new PrintStackTraceExceptionHandler());
        server.add(new StaticFileHandler("src/main/resources"));
        server.start();


        System.out.println("Running on " + server.getUri());




        /*
        //WebRenderer renderer = new WebRenderer("/graphwalker", 9000);
        //new Thread(renderer).start();

        //NettyWebServer webServer = new NettyWebServer(Executors.newSingleThreadScheduledExecutor(), 8080);
        //webServer.add(new StaticFileHandler("src/main/resources"));
        StaticFileHandler handler = new StaticFileHandler( "src/main/resources");
        handler.enableDirectoryListing(true);
        handler.welcomeFile("index.html");

        WebServers.createWebServer(Executors.newSingleThreadScheduledExecutor()
                , new InetSocketAddress("127.0.0.1", 8080), new URI("graphwalker")).add(handler).start().get();
        //EchoWsServer server = new EchoWsServer(WebServers.createWebServer(8080));
        //server.start();
        */
    }

    static class EchoWsServer {

        private final WebServer webServer;

        public EchoWsServer(WebServer webServer) throws IOException {
            this.webServer = webServer;
            webServer.add(new HttpToWebSocketHandler(new EchoHandler())).connectionExceptionHandler(new PrintStackTraceExceptionHandler());
        }

        public void start() throws ExecutionException, InterruptedException {
            webServer.start().get();
        }

        public URI uri() throws IOException {
            return webServer.getUri();
        }

        public void stop() throws ExecutionException, InterruptedException {
            webServer.stop().get();
        }

    }

    static class EchoHandler extends BaseWebSocketHandler {
        @Override
        public void onMessage(WebSocketConnection connection, String msg) throws Exception {
            connection.send(msg);
        }

        @Override
        public void onMessage(WebSocketConnection connection, byte[] msg) {
            connection.send(msg);
        }
    }
}
