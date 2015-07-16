package org.biiig.fsm.gspan;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by peet on 15.07.15.
 */
public class GSpanGraph {

  private final Map<GSpanVertex,Set<GSpanEdge>> adjacencyLists =
    new HashMap<>();

  public GSpanVertex newVertex(long label) {
    GSpanVertex vertex = new GSpanVertex(label);
    adjacencyLists.put(vertex,new HashSet<GSpanEdge>());
    return vertex;
  }

  public GSpanEdge newEdge(GSpanVertex source, long label, GSpanVertex target)
  {
    GSpanEdge edge = new GSpanEdge(source,label,target);
    adjacencyLists.get(source).add(edge);
    adjacencyLists.get(target).add(edge);
    return edge;
  }

  public Set<GSpanVertex> getVertices() {
    return adjacencyLists.keySet();
  }

  public Set<GSpanEdge> getEdges() {
    Set<GSpanEdge> edges = new HashSet<>();
    for(Set<GSpanEdge> adjacencyList : adjacencyLists.values()) {
      edges.addAll(adjacencyList);
    }
    return edges;
  }

  public Set<GSpanEdge> getAdjacencyList(GSpanVertex vertex) {
    return adjacencyLists.get(vertex);
  }

}
