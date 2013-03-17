/*
 * #%L
 * GraphWalker Server
 * %%
 * Copyright (C) 2011 - 2013 GraphWalker
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
package org.graphwalker.server;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.graphwalker.service.GraphWalkerService;
import org.graphwalker.core.utils.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * <p>GraphWalkerServer class.</p>
 *
 * @author nilols
 */
public class GraphWalkerServer {

    private final static Logger logger = LoggerFactory.getLogger(GraphWalkerServer.class);
    private final CommandParser commandParser;
    private final TServer server;
    private final Thread thread;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        GraphWalkerServer server = new GraphWalkerServer(args);
        server.start();
    }

    /**
     * <p>Constructor for GraphWalkerServer.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public GraphWalkerServer(String[] args) {
        Properties version = loadProperties("/version.properties");
        logger.info(Resource.getText(Bundle.NAME, "info.separator"));
        logger.info(Resource.getText(Bundle.NAME, "info.server"
                , version.getProperty("server.title")
                , version.getProperty("server.version")
                , version.getProperty("server.build.timestamp")));
        logger.info(Resource.getText(Bundle.NAME, "info.separator"));
        this.commandParser = new CommandParser(args, loadProperties("/default.properties"));
        logger.info(Resource.getText(Bundle.NAME, "info.separator"));
        this.server = createServer();

        this.thread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                logger.info(Resource.getText(Bundle.NAME, "info.server.shutdown"));
                stop();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }));
    }

    private Properties loadProperties(String filename) {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(filename));
        } catch (IOException e) {
            logger.error(Resource.getText(Bundle.NAME, "error.loading.properties", filename), e);
        }
        return properties;
    }

    private TServer createServer() {
        try {
            GraphWalkerService.Iface service = new GraphWalkerServiceImpl();
            GraphWalkerService.Processor<GraphWalkerService.Iface> processor = new GraphWalkerService.Processor<GraphWalkerService.Iface>(service);
            TServerTransport serverTransport = new TServerSocket(Integer.parseInt(commandParser.getProperty("server.port")));
            return new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>start.</p>
     */
    public void start() {
        if (!server.isServing()) {
            logger.info(Resource.getText(Bundle.NAME, "info.server.start"));
            server.serve();
        }
    }

    /**
     * <p>stop.</p>
     */
    public void stop() {
        if (server.isServing()) {
            logger.info(Resource.getText(Bundle.NAME, "info.server.stop"));
            server.stop();
        }
    }

}
