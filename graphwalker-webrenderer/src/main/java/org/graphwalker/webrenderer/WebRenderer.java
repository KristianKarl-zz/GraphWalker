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

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphwalker.core.Machine;
import org.graphwalker.core.event.MachineSink;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Vertex;
import org.json.simple.JSONObject;
import org.webbitserver.*;
import org.webbitserver.handler.EmbeddedResourceHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public class WebRenderer extends BaseWebSocketHandler implements MachineSink {

    private final Machine machine;
    private final WebServer server;
    private final Set<WebSocketConnection> connections = new HashSet<>();

    public WebRenderer(Machine machine) {
        this(machine, 9191);
    }

    public WebRenderer(Machine machine, int port) {
        this(machine, "/graphwalker", port);
    }

    public WebRenderer(Machine machine, String path, int port) {
        this.machine = machine;
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

    private HierarchicalLayout doLayout(ExecutionContext context) {
        HierarchicalLayout layout = new HierarchicalLayout();
        for (Vertex vertex : context.getModel().getVertices()) {
            layout.nodeAdded(context.getModel().toString(), 0, vertex.getName());
        }
        for (Edge edge : context.getModel().getEdges()) {
            layout.edgeAdded(context.getModel().toString(), 0, edge.getName(), edge.getSourceVertex().getName(), edge.getTargetVertex().getName(), true);
        }
        layout.compute();
        return layout;
    }

    private JSONObject build(Machine machine) {
        JSONObject root = new JSONObject();
        root.put("type", "initial");
        List<JSONObject> models = new ArrayList<>();
        for (ExecutionContext context : machine.getExecutionContexts()) {
            Graph graph = doLayout(context).getGraph();
            JSONObject model = new JSONObject();
            model.put("state", "started");
            model.put("id", "123");
            model.put("name", "123");
            List<JSONObject> vertices = new ArrayList<>();
            for (Node node : graph.getNodeSet()) {
                Vertex vertex = context.getModel().getVertex(node.getId());
                JSONObject element = new JSONObject();
                element.put("id", node.getId());
                element.put("state", "unvisited");
                element.put("color", "#555555");
                element.put("label", vertex.getName());
                JSONObject geometry = new JSONObject();
                geometry.put("x", 100 * (Double) node.getAttribute("x")); //TODO: convert to the viewers coordinate space
                geometry.put("y", 100 * (Double) node.getAttribute("y"));
                geometry.put("width", 100);
                geometry.put("height", 50);
                element.put("geometry", geometry);
                vertices.add(element);
            }
            model.put("nodes", vertices);
            List<JSONObject> edges = new ArrayList<>();
            for (org.graphstream.graph.Edge layoutEdge : graph.getEdgeSet()) {
                //Edge edge = context.getModel().getEdges(layoutEdge.getId()).get(0);
                JSONObject element = new JSONObject();
                element.put("id", layoutEdge.getId());
                element.put("source", layoutEdge.getSourceNode().getId());
                element.put("target", layoutEdge.getTargetNode().getId());
                element.put("state", "unvisited");
                JSONObject edgeLabel = new JSONObject();
                //edgeLabel.put("x", edge.getLabel().getX());
                //edgeLabel.put("y", edge.getLabel().getY());
                //edgeLabel.put("label", edge.getLabel().getText());
                JSONObject edgePath = new JSONObject();
                edgePath.put("sx", 0);//edge.getPathSource().getX());
                edgePath.put("sy", 0);//edge.getPathSource().getY());
                edgePath.put("tx", 0);//edge.getPathTarget().getX());
                edgePath.put("ty", 0);//edge.getPathTarget().getY());
                List<JSONObject> pathPoints = new ArrayList<>();
                //for (Point point : edge.getPathPoints()) {
                //    JSONObject pathPoint = new JSONObject();
                //    pathPoint.put("x", point.getX());
                //    pathPoint.put("y", point.getY());
                //    pathPoints.add(pathPoint);
                //}
                edgePath.put("points", pathPoints);
                element.put("path", edgePath);
                element.put("label", edgeLabel);
                edges.add(element);
            }
            model.put("edges", edges);
            model.put("variables", new ArrayList<JSONObject>());
            models.add(model);
        }
        root.put("models", models);
        return root;
    }

    @Override
    public void onOpen(WebSocketConnection connection) {
        connections.add(connection);
        connection.send(build(machine).toJSONString());
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
