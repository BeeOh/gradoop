package org.biiig.fsm.gspan.common;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by peet on 15.07.15.
 */
public class GSpanGraph {

  private final Set<GSpanVertex> vertices = new HashSet<>();
  private final Set<GSpanEdge> edges = new HashSet<>();

  public GSpanVertex newVertex(Integer label) {
    GSpanVertex vertex = new GSpanVertex(label);
    vertices.add(vertex);
    return vertex;
  }

  public GSpanEdge newEdge(GSpanVertex source, Integer label, GSpanVertex target)
  {
    GSpanEdge edge = new GSpanEdge(source,label,target);
    edges.add(edge);
    return edge;
  }

  public Set<GSpanEdge> getEdges() {
    return edges;
  }


}
