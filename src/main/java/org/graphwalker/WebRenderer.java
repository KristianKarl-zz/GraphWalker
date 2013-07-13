package org.graphwalker;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.graphwalker.graph.AbstractElement;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.graphwalker.multipleModels.ModelAPI;
import org.graphwalker.multipleModels.ModelHandler;
import org.json.simple.JSONObject;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.handler.EmbeddedResourceHandler;


public class WebRenderer extends BaseWebSocketHandler implements Observer {
  private static Logger logger = Util.setupLogger(WebRenderer.class);

  private WebSocketConnection wsConnection = null;

  private JSONObject model;
  private JSONObject edge;
  private JSONObject node;
  private JSONObject point;
  private JSONObject allModels;
  private JSONObject updateModels;

  private String stringTest = "";

  private ArrayList<JSONObject> edges;
  private ArrayList<JSONObject> nodes;
  private ArrayList<JSONObject> models;
  private ArrayList<JSONObject> points;
  private ArrayList<JSONObject> updateBuffer = new ArrayList<JSONObject>();

  private ModelHandler modelHandler = null;
  private Map<String, ModelBasedTesting> mbts = new HashMap<String, ModelBasedTesting>();

  public WebRenderer(ModelHandler modelHandler, String name, ModelAPI api) {
    this.modelHandler = modelHandler;
    addModel(name, api);
  }

  public void startup() {
    connect();
    createInitialJSONString();
  }

  public void addModel(String name, ModelAPI api) {
    logger.debug("Adding model: " + name + ", to web renderer");
    api.getMbt().addObserver(this);
    mbts.put(name, api.getMbt());
  }

  public WebSocketConnection getWSConnection() {
    return wsConnection;
  }

  public ModelBasedTesting getMbt(String name) {
    return mbts.get(name);
  }

  /**
   * Send a message to the connected browser.
   * 
   * @param message the message to send
   */
  private void send(String message) {
    if (getWSConnection() != null) {
      getWSConnection().send(message);
    }
  }

  @SuppressWarnings("unchecked")
  public void update(Observable obj, Object arg) {
    logger.debug("Updating webrenderer");
    updateModels = new JSONObject();

    updateModels.put("type", "update");

    models = new ArrayList<JSONObject>();
    model = new JSONObject();

    model.put("id", modelHandler.getCurrentRunningModel());
    logger.debug(mbts.get(modelHandler.getCurrentRunningModel()).getDataAsJSON());
    model.put("variables", mbts.get(modelHandler.getCurrentRunningModel()).getDataAsJSON());

    JSONObject startNode = new JSONObject();
    startNode.put("id", "n0");
    startNode.put("state", "visited");

    if (arg instanceof Vertex) {
      node = new JSONObject();
      node.put("id", ((AbstractElement) arg).getIdKey());
      node.put("state", getState((AbstractElement) arg));
      node.put("visited", ((AbstractElement) arg).getVisitedKey());

      nodes = new ArrayList<JSONObject>();
      nodes.add(node);
      nodes.add(startNode);
      model.put("nodes", nodes);
    } else if (arg instanceof Edge) {
      edge = new JSONObject();
      edge.put("id", ((AbstractElement) arg).getIdKey());
      edge.put("state", getState((AbstractElement) arg));

      edges = new ArrayList<JSONObject>();
      edges.add(edge);
      model.put("edges", edges);
    } else {
      // TODO: More classes should be added to the generic method "update".
      // Everything that could be updated should be in here.
      logger.warn("Error, trying to update something that is not an edge nor vertex");
    }
    models.add(model);

    updateModels.put("models", models);

    if (getWSConnection() != null)
      send(updateModels.toJSONString());
    else
      updateBuffer.add(updateModels);
  }

  @SuppressWarnings("unchecked")
  public JSONObject createInitialJSONString() {
    logger.debug("Creating initial json string for webrenderer");

    allModels = new JSONObject();
    allModels.put("type", "initial");

    // Models
    models = new ArrayList<JSONObject>();

    // Loop over all the input models
    Set<Entry<String, ModelBasedTesting>> s = mbts.entrySet();
    Iterator<Entry<String, ModelBasedTesting>> it = s.iterator();

    while (it.hasNext()) {

      Entry<String, ModelBasedTesting> m = (Entry<String, ModelBasedTesting>) it.next();
      String name = (String) m.getKey();
      Graph g = (Graph) ((ModelBasedTesting) m.getValue()).getGraph();

      model = new JSONObject();

      model.put("state", "started");
      model.put("id", name);
      model.put("name", name);

      // Node
      nodes = new ArrayList<JSONObject>();
      for (Vertex n : g.getVertices()) {

        node = new JSONObject();

        node.put("id", n.getIdKey()); // Unique node id
        node.put("state", "unvisited");
        node.put("color", "#" + Integer.toHexString(n.getFillColor().getRGB()).substring(2, 8).toUpperCase());
        node.put("label", n.getLabelKey());


        // Node info, geometry
        JSONObject geometry = new JSONObject(); // Object representing the yEd stored position

        geometry.put("x", n.getLocation().getX()); // Left most value
        geometry.put("y", n.getLocation().getY()); // Top value
        geometry.put("width", n.getWidth());
        geometry.put("height", n.getHeight());

        node.put("geometry", geometry);
        nodes.add(node);

      }

      // Edge
      edges = new ArrayList<JSONObject>();
      for (Edge e : g.getEdges()) {

        edge = new JSONObject();

        edge.put("id", e.getIdKey()); // Unique edge id
        edge.put("source", g.getSource(e).getIdKey()); // Source node id
        edge.put("target", g.getDest(e).getIdKey()); // Target node id
        edge.put("state", "unvisited");

        // Edge info, label
        JSONObject edgeLabel = new JSONObject();

        edgeLabel.put("x", e.getLabelLocation().getX()); // yEd stored label position
        edgeLabel.put("y", e.getLabelLocation().getY());
        edgeLabel.put("label", e.getLabelKey());

        // Edge info, path
        JSONObject path = new JSONObject(); // Object representing yEd stored edge path

        path.put("sx", e.getPathSourceLocation().getX()); // Source node offset position
        path.put("sy", e.getPathSourceLocation().getY());
        path.put("tx", e.getPathTargetLocation().getX()); // Target node offset position
        path.put("ty", e.getPathTargetLocation().getY());

        // Path info, Points
        points = new ArrayList<JSONObject>(); // Array of additional points on the path
        for (Point2D p : e.getPathPoints()) {
          point = new JSONObject();

          point.put("x", p.getX());
          point.put("y", p.getY());

          points.add(point);
        }

        // Putting the JSON together
        path.put("points", points);
        edge.put("path", path);
        edge.put("label", edgeLabel);

        edges.add(edge);
      }

      model.put("edges", edges);
      model.put("nodes", nodes);
      ArrayList<JSONObject> variables = new ArrayList<JSONObject>();
      model.put("variables", variables );
      models.add(model);
    }

    allModels.put("models", models);

    stringTest = allModels.toJSONString();
    return allModels;
  }

  private String getState(AbstractElement n) {
    logger.debug("getState AbstractElement n: " + n + ", n.getVisitedKey(): " + n.getVisitedKey() + ", mbt: "
        + ObjectUtils.identityToString(mbts.get(modelHandler.getCurrentRunningModel()).getCurrentAbstractElement()));
    String state = "";
    if (n.getVisitedKey() == 0) {
      state = "unvisited";
    } else {
      state = "visited";
    }
    if (mbts.get(modelHandler.getCurrentRunningModel()).getCurrentAbstractElement() == n) {
      state = "active";
    }
    return state;
  }

  /*
   * WebSockets
   */
  @Override
  public void onOpen(WebSocketConnection connection) {
    this.wsConnection = connection;
    send(stringTest);
    if (updateBuffer.size() > 0) {
      for (JSONObject jo : updateBuffer) {
        send(jo.toJSONString());
      }
    }

  }

  public static Boolean readRunProperty() {
    PropertiesConfiguration conf = null;
    if (new File("graphwalker.properties").canRead()) {
      try {
        conf = new PropertiesConfiguration("graphwalker.properties");
      } catch (ConfigurationException e) {
        logger.error(e.getMessage());
      }
    } else {
      conf = new PropertiesConfiguration();
      try {
        conf.load(WebRenderer.class.getResourceAsStream("/org/graphwalker/resources/graphwalker.properties"));
      } catch (ConfigurationException e) {
        logger.error(e.getMessage());
      }
    }

    String readprop = conf.getString("graphwalker.wr.run");
    Boolean run = false;
    if (readprop != null) {
      run = Boolean.valueOf(readprop);
    }
    logger.debug("Read graphwalker.wr.port from graphwalker.properties. Will run webrenderer: " + run);
    return run;
  }

  public static String readWRPort() {
    PropertiesConfiguration conf = null;
    if (new File("graphwalker.properties").canRead()) {
      try {
        conf = new PropertiesConfiguration("graphwalker.properties");
      } catch (ConfigurationException e) {
        logger.error(e.getMessage());
      }
    } else {
      conf = new PropertiesConfiguration();
      try {
        conf.load(WebRenderer.class.getResourceAsStream("/org/graphwalker/resources/graphwalker.properties"));
      } catch (ConfigurationException e) {
        logger.error(e.getMessage());
      }
    }
    String port = conf.getString("graphwalker.wr.port");
    logger.debug("Read graphwalker.wr.port from graphwalker.properties: " + port);
    if (port == null) {
      port = "9191";
      logger.debug("Setting WebRenderer port to: 9191");
    }
    return port;
  }

  @Override
  public void onClose(WebSocketConnection connection) {}

  @Override
  public void onMessage(WebSocketConnection connection, String message) {
    send(message);
  }

  public void connect() {
    int port = Integer.parseInt(readWRPort());
    WebServer webServer = WebServers.createWebServer(port).add("/graphwalker", this).add(new EmbeddedResourceHandler("org/graphwalker/web"));
    webServer.start();
    logger.debug("WebRenderer running at: " + webServer.getUri());
  }

}
