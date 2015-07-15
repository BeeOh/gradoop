package org.biiig.fsm.gspan;

import scala.Int;

/**
 * Created by peet on 01.07.15.
 */
public class DfsEdge implements Comparable<DfsEdge> {

  private final int fromPosition;
  private final int toPosition;
  private final int fromLabel;
  private final boolean outgoing;
  private final int edgeLabel;
  private final int toLabel;

  public DfsEdge(int fromPosition, int toPosition, String fromLabel,
    boolean outgoing, String edgeLabel, String toLabel) {
    this.fromPosition = fromPosition;
    this.toPosition = toPosition;
    this.fromLabel = Integer.valueOf(fromLabel);
    this.outgoing = outgoing;
    this.edgeLabel = Integer.valueOf(edgeLabel);
    this.toLabel = Integer.valueOf(toLabel);
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
}
