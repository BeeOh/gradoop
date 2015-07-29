package org.gradoop.model.impl.operators.io.formats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.gradoop.model.GraphElement;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by galpha on 27.07.15.
 */
public class FlinkBTGVertexValue implements GraphElement {
  /**
   * The vertex type which is defined in {@link FlinkBTGVertexType}
   */
  private FlinkBTGVertexType vertexType;
  /**
   * The value of that vertex.
   */
  private Double vertexValue;
  /**
   * The list of BTGs that vertex belongs to.
   */
  private List<Long> btgIDs;
  /**
   * Stores the minimum vertex ID per message sender. This is only used by
   * vertices of type {@link FlinkBTGVertexType} MASTER, so it is only
   * initialized
   * when needed.
   */
  private Map<Long, Long> neighborMinimumBTGIds;

  /**
   * Initializes an IIGVertex based on the given parameters.
   *
   * @param vertexType  The type of that vertex
   * @param vertexValue The value stored at that vertex
   * @param btgIDs      A list of BTGs that vertex belongs to
   */
  public FlinkBTGVertexValue(FlinkBTGVertexType vertexType, Double vertexValue,
    List<Long> btgIDs) {
    this.vertexType = vertexType;
    this.vertexValue = vertexValue;
    this.btgIDs = btgIDs;
  }

  /**
   * Returns the type of that vertex.
   *
   * @return vertex type
   */
  public FlinkBTGVertexType getVertexType() {
    return this.vertexType;
  }

  /**
   * Returns the value of that vertex.
   *
   * @return vertex value
   */
  public Double getVertexValue() {
    return this.vertexValue;
  }

  /**
   * Sets the value of that vertex.
   *
   * @param vertexValue value to be set
   */
  public void setVertexValue(Double vertexValue) {
    this.vertexValue = vertexValue;
  }

  @Override
  public Iterable<Long> getGraphs() {
    return this.btgIDs;
  }

  @Override
  public void addGraph(Long graph) {
    if (this.btgIDs.isEmpty()) {
      resetGraphs();
    }
    this.btgIDs.add(graph);
  }

  @Override
  public void addGraphs(Iterable<Long> graphs) {
    if (this.btgIDs.isEmpty()) {
      resetGraphs();
    }
    for (Long btg : graphs) {
      this.btgIDs.add(btg);
    }
  }

  @Override
  public void resetGraphs() {
    this.btgIDs = Lists.newArrayList();
  }

  @Override
  public int getGraphCount() {
    return this.btgIDs.size();
  }

  /**
   * TODO NEW
   */
  public Long getLastGraph() {
    if (this.btgIDs.size() > 0) {
      return btgIDs.get(btgIDs.size() - 1);
    }
    return null;
  }

  /**
   * Removes the last inserted BTG ID. This is necessary for non-master vertices
   * as they need to store only the minimum BTG ID, because they must only occur
   * in one BTG.
   */
  public void removeLastBtgID() {
    if (this.btgIDs.size() > 0) {
      this.btgIDs.remove(this.btgIDs.size() - 1);
    }
  }

  /**
   * Stores the given map between vertex id and BTG id if the pair does not
   * exist. It it exists, the BTG id is updated iff it is smaller than the
   * currently stored BTG id.
   *
   * @param vertexID vertex id of a neighbour node
   * @param btgID    BTG id associated with the neighbour node
   */
  public void updateNeighbourBtgID(Long vertexID, Long btgID) {
    if (neighborMinimumBTGIds == null) {
      initNeighbourMinimBTGIDMap();
    }
    if (!neighborMinimumBTGIds.containsKey(vertexID) ||
      (neighborMinimumBTGIds.containsKey(vertexID) &&
        neighborMinimumBTGIds.get(vertexID) > btgID)) {
      neighborMinimumBTGIds.put(vertexID, btgID);
    }
  }

  /**
   * Updates the set of BTG ids this vertex is involved in according to the set
   * of minimum values stored in the mapping between neighbour nodes and BTG
   * ids. This is only necessary for master data nodes like described in
   */
  public void updateBtgIDs() {
    if (this.neighborMinimumBTGIds != null) {
      Set<Long> newBtgIDs = new HashSet<>();
      for (Map.Entry<Long, Long> e : this.neighborMinimumBTGIds.entrySet()) {
        newBtgIDs.add(e.getValue());
      }
      this.btgIDs = Lists.newArrayList(newBtgIDs);
    }
  }

  /**
   * Initializes the internal map with default size when needed.
   */
  private void initNeighbourMinimBTGIDMap() {
    initNeighbourMinimumBTGIDMap(-1);
  }

  /**
   * Initializes the internal map with given size when needed. If size is -1 a
   * default map will be created.
   *
   * @param size the expected size of the Map or -1 if unknown.
   */
  private void initNeighbourMinimumBTGIDMap(int size) {
    if (size == -1) {
      this.neighborMinimumBTGIds = Maps.newHashMap();
    } else {
      this.neighborMinimumBTGIds = Maps.newHashMapWithExpectedSize(size);
    }
  }

  @Override
  public String toString() {
    Pattern VALUE_TOKEN_SEPARATOR = Pattern.compile(" ");
    StringBuilder sb = new StringBuilder();
    sb.append(vertexType);
    sb.append(VALUE_TOKEN_SEPARATOR);
    sb.append(vertexValue);
    sb.append(VALUE_TOKEN_SEPARATOR);
    sb.append(getGraphs().toString());
    return sb.toString();
  }
}