package org.biiig.fsm.gspan.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by p3et on 26.06.15.
 *
 * POJO representing a GSpan DFS code
 */
public class DfsCode implements Comparable<DfsCode>, Serializable {
  /**
   * list of contained DFS edges, where index is the discovery position (time)
   */
  private final List<DfsEdge> dfsEdges = new ArrayList<>();

  // behaviour

  /**
   * extends the code by a DFS edge
   * @param dfsEdge DFS edge for extension
   */
  public void add(DfsEdge dfsEdge) {
    dfsEdges.add(dfsEdge);
  }

  // convenience methods

  /**
   * returns the last discovered edge
   * @return last discovered edge
   */
  public DfsEdge getLastDfsEdge() {
    return dfsEdges.get(dfsEdges.size() - 1);
  }
  /**
   * clone method
   * @return a clone
   */
  public DfsCode newChild() {
    DfsCode clone = new DfsCode();
    clone.getDfsEdges().addAll(dfsEdges);
    return clone;
  }

  // override methods

  /**
   * comparator based on comparing DFS edges at same discovery positions
   * @param other other DFS code
   * @return comparison result
   */
  @Override
  public int compareTo(DfsCode other) {
    int comparison = 0;

    Iterator<DfsEdge> ownIterator = this.dfsEdges.iterator();
    Iterator<DfsEdge> otherIterator = other.dfsEdges.iterator();

    while (comparison == 0 && ownIterator.hasNext() &&
      otherIterator.hasNext()) {
      comparison = new DfsEdgeComparator().compare(
        ownIterator.next(), otherIterator.next());
    }

    if (comparison == 0) {
      if (ownIterator.hasNext()) {
        comparison = 1;
      } else if (otherIterator.hasNext()) {
        comparison = -1;
      }
    }
    return comparison;
  }
  /**
   * checks equality to other DFS code
   * @param other other DFS code
   * @return true, if considered to be equal
   */
  @Override
  public boolean equals(Object other) {
    boolean equals = true;

    if (other instanceof DfsCode) {
      DfsCode otherDfsCode = (DfsCode) other;

      if (this.dfsEdges.size() == otherDfsCode.dfsEdges.size()) {

        Iterator<DfsEdge> ownIterator = dfsEdges.iterator();
        Iterator<DfsEdge> otherIterator = otherDfsCode.dfsEdges
          .iterator();

        while (equals && ownIterator.hasNext()) {
          equals = ownIterator.next().equals(otherIterator.next());
        }

      } else {
        equals = false;
      }
    }

    return equals;
  }
  /**
   * generates hash code by aggregating all DFS edge hash codes
   * @return hash code
   */
  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    for (DfsEdge edge : dfsEdges) {
      builder.append(edge.hashCode());
    }

    return builder.hashCode();
  }
  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    return "[" + StringUtils.join(dfsEdges, ",") + "]";
  }

  // getters and setters

  public List<DfsEdge> getDfsEdges() {
    return dfsEdges;
  }
}
