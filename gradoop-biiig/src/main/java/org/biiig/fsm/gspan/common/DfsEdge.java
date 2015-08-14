package org.biiig.fsm.gspan.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by p3et on 01.07.15.
 *
 * POJO describing an edge traversed while depth first search according to the
 * GSpan Technical Report but extended by a direction bit expressing if the
 * edge was traversed in direction or inversely
 */
public class DfsEdge implements Comparable<DfsEdge>, Serializable {
  /**
   * discovery position (time) of the traversal start vertex
   */
  private final Integer fromPosition;
  /**
   * label of traversal start vertex
   */
  private final Integer fromLabel;
  /**
   * true, if edge was traversed in direction
   */
  private final boolean inDirection;
  /**
   * label of the traversed edge
   */
  private final Integer edgeLabel;
  /**
   * discovery position (time)  of the traversal end vertex
   */
  private final Integer toPosition;
  /**
   * label of traversal end vertex
   */
  private final Integer toLabel;

  // behaviour

  /**
   * constructor
   * @param fromPosition discovery position (time) of the traversal start vertex
   * @param fromLabel label of traversal start vertex
   * @param inDirection true, if edge was traversed in direction
   * @param edgeLabel label of the traversed edge
   * @param toPosition discovery position (time)  of the traversal end vertex
   * @param toLabel label of traversal end vertex
   */
  public DfsEdge(Integer fromPosition, Integer fromLabel, boolean inDirection,
    Integer edgeLabel, Integer toPosition, Integer toLabel) {
    this.fromPosition = fromPosition;
    this.fromLabel = fromLabel;
    this.inDirection = inDirection;
    this.edgeLabel = edgeLabel;
    this.toPosition = toPosition;
    this.toLabel = toLabel;
  }

  // convenience methods

  private boolean isBackward() {
    // = included for self-loops
    return toPosition <= fromPosition;
  }

  public boolean isForward() {
    return !isBackward();
  }

  // override methods

  /**
   * implementation of GSpan lexicographic ordering
   * @param other other DFS edge
   * @return comparison result
   */
  @Override
  public int compareTo(DfsEdge other) {
    int comparison;

    // same direction
    if (this.isForward() == other.isForward()) {

      // both forward
      if (this.isForward()) {

        // starts from same position
        if (this.fromPosition == other.fromPosition) {

          // inherit edge comparison by labels (lexicographically order)
          comparison = compareLabelsAndDirection(other);

          // starts from a later visited vertex
        } else if (this.fromPosition > other.fromPosition) {
          comparison = -1;

          // starts from an earlier visited vertex
        } else {
          comparison = 1;
        }

        // both backward
      } else {

        // refers same position
        if (this.toPosition == other.toPosition) {

          // inherit edge comparison by labels (lexicographically order)
          comparison = compareLabelsAndDirection(other);

          // refers an earlier visited vertex
        } else if (this.toPosition < other.toPosition) {
          comparison = -1;

          // refers a later visited vertex
        } else {
          comparison = 1;
        }

      }

      // inverse direction
    } else {
      if (this.isBackward()) {
        comparison = -1;
      } else {
        comparison = 1;
      }
    }

    return comparison;
  }
  /**
   * extracted method to compare DFS edges based on start, edge and edge labels
   * as well as the traversal direction
   * @param other other DFS edge
   * @return comparison result
   */
  private int compareLabelsAndDirection(DfsEdge other) {
    int comparison;

    if (this.fromLabel < other.fromLabel) {
      comparison = -1;
    } else if (this.fromLabel > other.fromLabel) {
      comparison = 1;
    } else {
      if (this.inDirection && !other.inDirection) {
        comparison = -1;
      } else if (!this.inDirection && other.inDirection) {
        comparison = 1;
      } else {
        if (this.edgeLabel < other.edgeLabel) {
          comparison = -1;
        } else if (this.edgeLabel > other.edgeLabel) {
          comparison = 1;
        } else {
          if (this.toLabel < other.toLabel) {
            comparison = -1;
          } else if (this.toLabel > other.toLabel) {
            comparison = 1;
          } else {
            comparison = 0;
          }
        }
      }
    }
    return comparison;
  }
  /**
   * checks equality to other DFS edge
   * @param other other DFS edge
   * @return true, if considered to be equal
   */
  @Override
  public boolean equals(Object other) {
    boolean equals = false;

    if (other instanceof DfsEdge) {
      DfsEdge otherDfsEdge = (DfsEdge) other;
      EqualsBuilder builder = new EqualsBuilder();

      builder.append(this.fromPosition, otherDfsEdge.fromPosition);
      builder.append(this.toPosition, otherDfsEdge.toPosition);
      builder.append(this.fromLabel, otherDfsEdge.fromLabel);
      builder.append(this.inDirection, otherDfsEdge.inDirection);
      builder.append(this.edgeLabel, otherDfsEdge.edgeLabel);
      builder.append(this.toLabel, otherDfsEdge.toLabel);

      equals = builder.isEquals();
    }

    return equals;
  }
  /**
   * generates hash code based on all fields
   * @return hash code
   */
  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    builder.append(fromPosition);
    builder.append(fromLabel);
    builder.append(inDirection);
    builder.append(edgeLabel);
    builder.append(toPosition);
    builder.append(toLabel);

    return builder.hashCode();
  }
  /**
   * to string method
   * @return string representation
   */
  public String toString() {
    String string = "(" + fromPosition + ":" + fromLabel + ")";

    if (!inDirection) {
      string += "<";
    }

    string += "-" + edgeLabel + "-";

    if (inDirection) {
      string += ">";
    }

    string += "(" + toPosition + ":" + toLabel + ")";

    return string;
  }

  // getters and setters

  public Integer getFromPosition() {
    return fromPosition;
  }

  public Integer getFromLabel() {
    return fromLabel;
  }

  public boolean isInDirection() {
    return inDirection;
  }

  public Integer getEdgeLabel() {
    return edgeLabel;
  }

  public Integer getToPosition() {
    return toPosition;
  }

  public Integer getToLabel() {
    return toLabel;
  }

}
