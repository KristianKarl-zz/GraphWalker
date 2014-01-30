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

import org.graphwalker.core.Machine;
import org.graphwalker.core.event.MachineSink;
import org.json.simple.JSONObject;
import org.webbitserver.*;
import org.webbitserver.handler.EmbeddedResourceHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public class WebRenderer extends BaseWebSocketHandler implements MachineSink {

    private final Machine machine;
    private final WebServer server;
    private final Set<WebSocketConnection> connections = new HashSet<>();
    private final JSONObject models;

    public WebRenderer(Machine machine) {
        this(machine, 9191);
    }

    public WebRenderer(Machine machine, int port) {
        this(machine, "/graphwalker", port);
    }

    public WebRenderer(Machine machine, String path, int port) {
        this.machine = machine;
        this.models = build(machine);
        this.server = WebServers.createWebServer(port);
        this.server.add(path, this);
        this.server.add(new EmbeddedResourceHandler(""));
    }

    public WebServer add(HttpHandler handler) {
        return server.add(handler);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        for (WebSocketConnection connection : connections) {
            connection.close();
        }
        server.stop();
    }

    private JSONObject build(Machine machine) {
        JSONObject models = new JSONObject();
        //models
        return models;
    }


//        allModels = new JSONObject();
//        allModels.put("type", "initial");
//
//        // Models
//        models = new ArrayList<JSONObject>();
//
//        // Loop over all the input models
//        Set<Map.Entry<String, ModelBasedTesting>> s = mbts.entrySet();
//        Iterator<Map.Entry<String, ModelBasedTesting>> it = s.iterator();
//
//        while (it.hasNext()) {
//
//            Map.Entry<String, ModelBasedTesting> m = (Map.Entry<String, ModelBasedTesting>) it.next();
//            String name = (String) m.getKey();
//            Graph g = (Graph) ((ModelBasedTesting) m.getValue()).getGraph();
//
//            model = new JSONObject();
//
//            model.put("state", "started");
//            model.put("id", name);
//            model.put("name", name);
//
//            // Node
//            nodes = new ArrayList<JSONObject>();
//            for (Vertex n : g.getVertices()) {
//
//                node = new JSONObject();
//
//                node.put("id", n.getIdKey()); // Unique node id
//                node.put("state", "unvisited");
//                node.put("color", "#" + Integer.toHexString(n.getFillColor().getRGB()).substring(2, 8).toUpperCase());
//                node.put("label", n.getLabelKey());
//
//
//                // Node info, geometry
//                JSONObject geometry = new JSONObject(); // Object representing the yEd stored position
//
//                geometry.put("x", n.getLocation().getX()); // Left most value
//                geometry.put("y", n.getLocation().getY()); // Top value
//                geometry.put("width", n.getWidth());
//                geometry.put("height", n.getHeight());
//
//                node.put("geometry", geometry);
//                nodes.add(node);
//
//            }
//
//            // Edge
//            edges = new ArrayList<JSONObject>();
//            for (Edge e : g.getEdges()) {
//
//                edge = new JSONObject();
//
//                edge.put("id", e.getIdKey()); // Unique edge id
//                edge.put("source", g.getSource(e).getIdKey()); // Source node id
//                edge.put("target", g.getDest(e).getIdKey()); // Target node id
//                edge.put("state", "unvisited");
//
//                // Edge info, label
//                JSONObject edgeLabel = new JSONObject();
//
//                edgeLabel.put("x", e.getLabelLocation().getX()); // yEd stored label position
//                edgeLabel.put("y", e.getLabelLocation().getY());
//                edgeLabel.put("label", e.getLabelKey());
//
//                // Edge info, path
//                JSONObject path = new JSONObject(); // Object representing yEd stored edge path
//
//                path.put("sx", e.getPathSourceLocation().getX()); // Source node offset position
//                path.put("sy", e.getPathSourceLocation().getY());
//                path.put("tx", e.getPathTargetLocation().getX()); // Target node offset position
//                path.put("ty", e.getPathTargetLocation().getY());
//
//                // Path info, Points
//                points = new ArrayList<JSONObject>(); // Array of additional points on the path
//                for (Point2D p : e.getPathPoints()) {
//                    point = new JSONObject();
//
//                    point.put("x", p.getX());
//                    point.put("y", p.getY());
//
//                    points.add(point);
//                }
//
//                // Putting the JSON together
//                path.put("points", points);
//                edge.put("path", path);
//                edge.put("label", edgeLabel);
//
//                edges.add(edge);
//            }
//
//            model.put("edges", edges);
//            model.put("nodes", nodes);
//            ArrayList<JSONObject> variables = new ArrayList<JSONObject>();
//            model.put("variables", variables );
//            models.add(model);
//        }
//
//        allModels.put("models", models);
//
//        stringTest = allModels.toJSONString();
//        return allModels;
//    }


    @Override
    public void onOpen(WebSocketConnection connection) {
        connections.add(connection);
        connection.send(models.toJSONString());
        // send status
    }

    @Override
    public void onClose(WebSocketConnection connection) {
        connections.remove(connection);
    }


    //private static //logger //logger = Util.setupLogger(WebRendererOld.class);

//    private WebSocketConnection wsConnection = null;
//
//    private JSONObject model;
//    private JSONObject edge;
//    private JSONObject node;
//    private JSONObject point;
//    private JSONObject allModels;
//    private JSONObject updateModels;
//
//    private String stringTest = "";
//
//    private List<JSONObject> edges;
//    private List<JSONObject> nodes;
//    private List<JSONObject> models;
//    private List<JSONObject> points;
//    private List<JSONObject> updateBuffer = new ArrayList<JSONObject>();
//
//    //private ModelHandler modelHandler = null;
//    //private Map<String, ModelBasedTesting> mbts = new HashMap<String, ModelBasedTesting>();
//
//    //public WebRendererOld(ModelHandler modelHandler, String name, ModelAPI api) {
//    //    this.modelHandler = modelHandler;
//    //    addModel(name, api);
//    //}
//
//
//    public void addModel(String name, ModelAPI api) {
//        //logger.debug("Adding model: " + name + ", to web renderer");
//        api.getMbt().addObserver(this);
//        mbts.put(name, api.getMbt());
//    }
//
//    @SuppressWarnings("unchecked")
//    public void update(Observable obj, Object arg) {
//        //logger.debug("Updating webrenderer");
//        updateModels = new JSONObject();
//
//        updateModels.put("type", "update");
//
//        models = new ArrayList<JSONObject>();
//        model = new JSONObject();
//
//        model.put("id", modelHandler.getCurrentRunningModel());
//        //logger.debug(mbts.get(modelHandler.getCurrentRunningModel()).getDataAsJSON());
//        model.put("variables", mbts.get(modelHandler.getCurrentRunningModel()).getDataAsJSON());
//
//        JSONObject startNode = new JSONObject();
//        startNode.put("id", "n0");
//        startNode.put("state", "visited");
//
//        if (arg instanceof Vertex) {
//            node = new JSONObject();
//            node.put("id", ((AbstractElement) arg).getIdKey());
//            node.put("state", getState((AbstractElement) arg));
//            node.put("visited", ((AbstractElement) arg).getVisitedKey());
//
//            nodes = new ArrayList<JSONObject>();
//            nodes.add(node);
//            nodes.add(startNode);
//            model.put("nodes", nodes);
//        } else if (arg instanceof Edge) {
//            edge = new JSONObject();
//            edge.put("id", ((AbstractElement) arg).getIdKey());
//            edge.put("state", getState((AbstractElement) arg));
//
//            edges = new ArrayList<JSONObject>();
//            edges.add(edge);
//            model.put("edges", edges);
//        } else {
//            // TODO: More classes should be added to the generic method "update".
//            // Everything that could be updated should be in here.
//            //logger.warn("Error, trying to update something that is not an edge nor vertex");
//        }
//        models.add(model);
//
//        updateModels.put("models", models);
//
//        if (getWSConnection() != null)
//            send(updateModels.toJSONString());
//        else
//            updateBuffer.add(updateModels);
//    }
//

//
//    private String getState(AbstractElement n) {
//        //logger.debug("getState AbstractElement n: " + n + ", n.getVisitedKey(): " + n.getVisitedKey() + ", mbt: "+ ObjectUtils.identityToString(mbts.get(modelHandler.getCurrentRunningModel()).getCurrentAbstractElement()));
//        String state = "";
//        if (n.getVisitedKey() == 0) {
//            state = "unvisited";
//        } else {
//            state = "visited";
//        }
//        if (mbts.get(modelHandler.getCurrentRunningModel()).getCurrentAbstractElement() == n) {
//            state = "active";
//        }
//        return state;
//    }
//
//    /*
//     * WebSockets
//     */
//    @Override
//    public void onOpen(WebSocketConnection connection) {
//        this.wsConnection = connection;
//        send(stringTest);
//        if (updateBuffer.size() > 0) {
//            for (JSONObject jo : updateBuffer) {
//                send(jo.toJSONString());
//            }
//        }
//
//    }
//
//    public static Boolean readRunProperty() {
//        PropertiesConfiguration conf = null;
//        if (new File("graphwalker.properties").canRead()) {
//            try {
//                conf = new PropertiesConfiguration("graphwalker.properties");
//            } catch (ConfigurationException e) {
//                //logger.error(e.getMessage());
//            }
//        } else {
//            conf = new PropertiesConfiguration();
//            try {
//                conf.load(WebRendererOld.class.getResourceAsStream("/org/graphwalker/resources/graphwalker.properties"));
//            } catch (ConfigurationException e) {
//                //logger.error(e.getMessage());
//            }
//        }
//
//        String readprop = conf.getString("graphwalker.wr.run");
//        Boolean run = false;
//        if (readprop != null) {
//            run = Boolean.valueOf(readprop);
//        }
//        //logger.debug("Read graphwalker.wr.port from graphwalker.properties. Will run webrenderer: " + run);
//        return run;
//    }
//
//    public static String readWRPort() {
//        PropertiesConfiguration conf = null;
//        if (new File("graphwalker.properties").canRead()) {
//            try {
//                conf = new PropertiesConfiguration("graphwalker.properties");
//            } catch (ConfigurationException e) {
//                //logger.error(e.getMessage());
//            }
//        } else {
//            conf = new PropertiesConfiguration();
//            try {
//                conf.load(WebRendererOld.class.getResourceAsStream("/org/graphwalker/resources/graphwalker.properties"));
//            } catch (ConfigurationException e) {
//                //logger.error(e.getMessage());
//            }
//        }
//        String port = conf.getString("graphwalker.wr.port");
//        //logger.debug("Read graphwalker.wr.port from graphwalker.properties: " + port);
//        if (port == null) {
//            port = "9191";
//            //logger.debug("Setting WebRendererOld port to: 9191");
//        }
//        return port;
//    }


}
