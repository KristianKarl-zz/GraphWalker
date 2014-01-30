package org.graphwalker.webrenderer;

/*
 * #%L
 * GraphWalker Web Renderer
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
