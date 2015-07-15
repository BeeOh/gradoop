package org.biiig.fsm.gspan;

import com.google.common.collect.Lists;
import org.biiig.model.LabeledEdge;
import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;

import java.util.*;

/**
 * Created by peet on 07.07.15.
 */
public class DfsCodeMapper implements Comparable<DfsCodeMapper> {

  private final LabeledGraph graph;
  private final DfsCode dfsCode;
  private final Map<DfsEdge,LabeledEdge> edgeMap = new HashMap<>();

  public DfsCode getDfsCode() {
    return dfsCode;
  }

  public DfsCodeMapper(DfsCode dfsCode, LabeledGraph graph) {
    this.dfsCode = dfsCode;
    this.graph = graph;
  }

  @Override
  public int compareTo(DfsCodeMapper other) {
    return this.graph == other.graph
      && this.dfsCode.compareTo(other.dfsCode)  == 0 ? 0 : 1;
  }

  public void map(DfsEdge dfsEdge, LabeledEdge edge) {
    edgeMap.put(dfsEdge,edge);
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    for(Map.Entry<DfsEdge,LabeledEdge> mapping : edgeMap.entrySet()) {
      buffer.append(mapping.getKey());
      buffer.append("=>");
      buffer.append(mapping.getValue());
    }
    return buffer.toString();
  }

  public boolean contains(DfsEdge dfsEdge) {
    return edgeMap.containsKey(dfsEdge);
  }

  public LabeledGraph getGraph() {
    return graph;
  }

  public boolean contains(LabeledEdge edge) {
    return edgeMap.containsValue(edge);
  }
}
