package org.biiig.fsm.gspan.common;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * POJO wrapping a label-encoded GSpan graph and all mappers to
 * subgraphs instantiating DFS codes of the current mining round
 *
 * Created by p3et on 12.08.15.
 */
public class SearchSpaceItem {
  /**
   * label-encoded search graph
   */
  private final GSpanGraph graph;
  /**
   * index of mappers per minimum DFS code;
   * reset while post pruning in each mining round
   */
  private Map<DfsCode, List<DfsCodeMapper>> dfsCodeMappersIndex;

  // behaviour

  /**
   * constructor
   * includes creating an empty search graph
   */
  public SearchSpaceItem() {
    this.graph = new GSpanGraph();
  }

  // convenience methods

  /**
   * determine the mappers of a given DFS code
   * @param code DFS code
   * @return list of associated mappers
   */
  public List<DfsCodeMapper> getMappers(DfsCode code) {
    return dfsCodeMappersIndex.get(code);
  }

  /**
   * determine minimum DFS codes
   * @return key set of the minimum DFS code index
   */
  public Set<DfsCode> getMinimumDfsCodes() {
    return dfsCodeMappersIndex.keySet();
  }

  // override methods

  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append("\n--- Search Space Item ---\n");

    for (DfsCode code : getMinimumDfsCodes()) {
      buffer.append(
        code + "(" + dfsCodeMappersIndex.get(code).size() + " mapper)\n");
    }

    return buffer.toString();
  }

  // getters and setters

  public GSpanGraph getGraph() {
    return graph;
  }

  public void setDfsCodeMappersIndex(
    Map<DfsCode, List<DfsCodeMapper>> dfsCodeMappersIndex) {
    this.dfsCodeMappersIndex = dfsCodeMappersIndex;
  }
}
