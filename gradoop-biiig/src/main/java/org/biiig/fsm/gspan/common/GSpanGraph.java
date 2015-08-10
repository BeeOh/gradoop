package org.biiig.fsm.gspan.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by p3et on 15.07.15.
 *
 * fat model representing a GSpan graph
 * (directed multigraph with integer vertex and edge labels)
 */
public class GSpanGraph {
  /**
   * set of vertices
    */
  private final Set<GSpanVertex> vertices = new HashSet<>();
  /**
   * set of edges
   */
  private final Set<GSpanEdge> edges = new HashSet<>();

  // behaviour

  /**
   * creates a new vertex in the graph
   * @param label label of the new vertex
   * @return the new vertex
   */
  public GSpanVertex newVertex(Integer label) {
    GSpanVertex vertex = new GSpanVertex(label);
    vertices.add(vertex);
    return vertex;
  }
  /**
   * creates a new edge in the graph
   * @param source source vertex  of the new edge
   * @param label label of the new edge
   * @param target target vertex of the new edge
   * @return the new edge
   */
  public GSpanEdge newEdge(
    GSpanVertex source, Integer label, GSpanVertex target) {
    GSpanEdge edge = new GSpanEdge(source, label, target);
    edges.add(edge);
    return edge;
  }

  // getters and setters

  public Set<GSpanVertex> getVertices() {
    return vertices;
  }

  public Set<GSpanEdge> getEdges() {
    return edges;
  }


}
