package org.gradoop.model.rest;

import org.gradoop.model.EPEdgeData;
import org.gradoop.model.EPFlinkTest;
import org.gradoop.model.EPVertexData;
import org.gradoop.model.impl.EPGraph;
import org.gradoop.model.impl.EPGraphCollection;
import org.json.JSONWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Root resource (exposed at "json" path)
 */
@Path("json")
public class MyResource extends EPFlinkTest {

  /**
   * Method handling HTTP GET requests. The returned object will be sent
   * to the client as "text/plain" media type.
   *
   * @return String that will be returned as a text/plain response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getIt() throws Exception {
    String s = "";


    EPGraphCollection graphColl = this.createSocialGraph().getCollection();

    EPGraph g = graphColl.getGraph(2l);

    StringWriter writer = new StringWriter();
    JSONWriter jsonWriter = new JSONWriter(writer);
    jsonWriter.object();
    jsonWriter.key("comment").value("test graph");
    List<EPVertexData> vertices = new ArrayList<>(g.getVertices().collect());
    List<EPEdgeData> edges = new ArrayList<>(g.getEdges().collect());
    writeVertices(vertices, jsonWriter);
    writeEdges(edges, jsonWriter);
    jsonWriter.endObject();
    s = writer.toString();

    return s;
  }

  public void writeVertices(List<EPVertexData> vertices, JSONWriter writer) {
    writer.key("nodes");
    writer.array();
    for (EPVertexData vertex : vertices) {
      writer.object();
      writer.key("id").value(vertex.getId());
      writer.key("caption").value(vertex.getProperties().get(PROPERTY_KEY_NAME));
      writer.endObject();
    }
    writer.endArray();
    return;
  }

  public void writeEdges(List<EPEdgeData> edges, JSONWriter writer) {
    writer.key("edges");
    writer.array();
    for (EPEdgeData edge : edges) {
      writer.object();
      writer.key("source").value(edge.getSourceVertex());
      writer.key("target").value(edge.getTargetVertex());
      writer.key("caption").value(edge.getLabel());
      writer.endObject();
    }
    writer.endArray();
    return;
  }
}
