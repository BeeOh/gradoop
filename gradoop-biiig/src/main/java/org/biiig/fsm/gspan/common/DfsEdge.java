package org.biiig.fsm.gspan.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by peet on 01.07.15.
 */
public class DfsEdge implements Comparable<DfsEdge> {

  private final Integer fromPosition;
  private final Integer toPosition;
  private final Integer fromLabel;
  private final boolean outgoing;
  private final Integer edgeLabel;
  private final Integer toLabel;

  public DfsEdge(Integer fromPosition, Integer toPosition, Integer fromLabel,
    boolean outgoing, Integer edgeLabel, Integer toLabel) {
    this.fromPosition = fromPosition;
    this.toPosition = toPosition;
    this.fromLabel = fromLabel;
    this.outgoing = outgoing;
    this.edgeLabel = edgeLabel;
    this.toLabel = toLabel;
  }

  private int compareLexicographically(DfsEdge other) {
    int comparison = 0;

    if(this.fromLabel < other.fromLabel) {
      comparison = -1;
    } else if(this.fromLabel > other.fromLabel) {
      comparison = 1;
    } else {
      if(this.outgoing && !other.outgoing) {
        comparison = -1;
      } else if(!this.outgoing && other.outgoing) {
        comparison = 1;
      } else {
        if(this.edgeLabel < other.edgeLabel) {
          comparison = -1;
        } else if(this.edgeLabel > other.edgeLabel) {
          comparison = 1;
        } else {
          if(this.toLabel < other.toLabel) {
            comparison = -1;
          } else if(this.toLabel > other.toLabel) {
            comparison = 1;
          } else {
            comparison = 0;
          }
        }
      }
    }

    return comparison;
  }

  public boolean isForward() {
    return fromPosition < toPosition;
  }

  private boolean isBackward() {
    // = included for loops
    return !isForward();
  }

  public String toString() {
    String string = "(" + fromPosition + ":" + fromLabel + ")";

    if(!outgoing) {
      string += "<";
    }

    string += "-" + edgeLabel + "-";

    if(outgoing) {
      string += ">";
    }

    string += "(" + toPosition + ":" + toLabel + ")";

    return string;
  }

  @Override
  public int hashCode(){
    HashCodeBuilder builder = new HashCodeBuilder();

    builder.append(fromPosition);
    builder.append(toPosition);
    builder.append(fromLabel);
    builder.append(outgoing);
    builder.append(edgeLabel);
    builder.append(toLabel);

    return builder.hashCode();
  }

  @Override
  public boolean equals(Object other){
    boolean equals = false;

    if (other instanceof DfsEdge) {
      DfsEdge otherDfsEdge = (DfsEdge) other;
      EqualsBuilder builder = new EqualsBuilder();

      builder.append(this.fromPosition, otherDfsEdge.fromPosition);
      builder.append(this.toPosition, otherDfsEdge.toPosition);
      builder.append(this.fromLabel, otherDfsEdge.fromLabel);
      builder.append(this.outgoing, otherDfsEdge.outgoing);
      builder.append(this.edgeLabel, otherDfsEdge.edgeLabel);
      builder.append(this.toLabel, otherDfsEdge.toLabel);

      equals = builder.isEquals();
    }

    return equals;
  }

  @Override
  public int compareTo(DfsEdge other) {
    int comparison = 0;

    // same direction
    if (this.isForward() == other.isForward()) {

      // both forward
      if (this.isForward()) {

        // starts from same position
        if (this.fromPosition == other.fromPosition) {

          // inherit edge comparison by labels (lexicographically order)
          comparison = compareLexicographically(other);

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
          comparison = compareLexicographically(other);

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

  public Integer getFromPosition() {
    return fromPosition;
  }

  public Integer getToPosition() {
    return toPosition;
  }

  public Integer getFromLabel() {
    return fromLabel;
  }

  public boolean isOutgoing() {
    return outgoing;
  }

  public Integer getEdgeLabel() {
    return edgeLabel;
  }

  public Integer getToLabel() {
    return toLabel;
  }

  public boolean contains(Integer position) {
    return fromPosition == position || toPosition == position;
  }
}
