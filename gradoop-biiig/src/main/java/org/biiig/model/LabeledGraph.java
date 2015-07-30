package org.biiig.model;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by peet on 25.06.15.
 */
public class LabeledGraph {

  private final List<LabeledVertex> vertices = new ArrayList<>();
  private final List<LabeledEdge> edges = new ArrayList<>();
  private final Map<LabeledVertex,List<LabeledEdge>>
    vertexOutgoingEdges = new HashMap<>();
  private final Map<LabeledVertex,List<LabeledEdge>>
    vertexIncomingEdges = new HashMap<>();


  public LabeledVertex newVertex(String label) {
    LabeledVertex vertex = new LabeledVertex(label);
    this.vertices.add(vertex);
    vertexOutgoingEdges.put(vertex,new ArrayList<LabeledEdge>());
    vertexIncomingEdges.put(vertex,new ArrayList<LabeledEdge>());
    return vertex;
  }

  public LabeledEdge newEdge(LabeledVertex sourceVertex, String label,
    LabeledVertex targetVertex) {
    LabeledEdge edge = new LabeledEdge(sourceVertex,label,targetVertex);
    this.edges.add(edge);
    vertexOutgoingEdges.get(sourceVertex).add(edge);
    vertexIncomingEdges.get(targetVertex).add(edge);
    return edge;
  }

  public String toString() {
    List<String> parts = new ArrayList<>();

    for(LabeledEdge edge : edges) {
      parts.add(
        "("
          + vertices.indexOf(edge.getSourceVertex())
          + ":"
          + edge.getSourceVertex().getLabel()
          + ")-"
          + edge.getLabel()
          + "->("
          + vertices.indexOf(edge.getTargetVertex())
          + ":"
          + edge.getTargetVertex().getLabel()
        + ")"
      );
    }
    return "<" + StringUtils.join(parts,",") + ">";
  }

  public List<LabeledVertex> getVertices() {
    return vertices;
  }

  public List<LabeledEdge> getEdges() {
    return edges;
  }

  public List<LabeledEdge> getEdges(LabeledVertex vertex) {
    List<LabeledEdge> vertexEdges = new LinkedList<>();
    vertexEdges.addAll(vertexOutgoingEdges.get(vertex));
    vertexEdges.addAll(vertexIncomingEdges.get(vertex));
    return vertexEdges;
  }

  public void remove(LabeledVertex vertex) {
    vertexIncomingEdges.remove(vertex);
    vertexOutgoingEdges.remove(vertex);
    vertices.remove(vertex);
  }
}
