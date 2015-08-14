package org.biiig.fsm.gspan.common;

import java.io.Serializable;
import java.util.Comparator;

/**
 * implementation of GSpan lexicographic ordering
 *
 * Created by p3et on 14.08.15.
 */
public class DfsEdgeComparator implements Comparator<DfsEdge>, Serializable {
  @Override
  public int compare(DfsEdge e1, DfsEdge e2) {
    int comparison;

    // same direction
    if (e1.isForward() == e2.isForward()) {

      // both forward
      if (e1.isForward()) {

        // starts from same position
        if (e1.getFromPosition().equals(e2.getFromPosition())) {

          // inherit edge comparison by labels (lexicographically order)
          comparison = compareLabelsAndDirection(e1, e2);

          // starts from a later visited vertex
        } else if (e1.getFromPosition() > e2.getFromPosition()) {
          comparison = -1;

          // starts from an earlier visited vertex
        } else {
          comparison = 1;
        }

        // both backward
      } else {

        // refers same position
        if (e1.getToPosition().equals(e2.getToPosition())) {

          // inherit edge comparison by labels (lexicographically order)
          comparison = compareLabelsAndDirection(e1, e2);

          // refers an earlier visited vertex
        } else if (e1.getToPosition() < e2.getToPosition()) {
          comparison = -1;

          // refers a later visited vertex
        } else {
          comparison = 1;
        }

      }

      // inverse direction
    } else {
      if (e1.isBackward()) {
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
   * @param e1 first DFS edge
   * @param e2 second DFS edge
   * @return comparison result
   */
  private int compareLabelsAndDirection(DfsEdge e1, DfsEdge e2) {
    int comparison;

    if (e1.getFromLabel() < e2.getFromLabel()) {
      comparison = -1;
    } else if (e1.getFromLabel() > e2.getFromLabel()) {
      comparison = 1;
    } else {
      if (e1.isInDirection() && !e2.isInDirection()) {
        comparison = -1;
      } else if (!e1.isInDirection() && e2.isInDirection()) {
        comparison = 1;
      } else {
        if (e1.getEdgeLabel() < e2.getEdgeLabel()) {
          comparison = -1;
        } else if (e1.getEdgeLabel() > e2.getEdgeLabel()) {
          comparison = 1;
        } else {
          if (e1.getToLabel() < e2.getToLabel()) {
            comparison = -1;
          } else if (e1.getToLabel() > e2.getToLabel()) {
            comparison = 1;
          } else {
            comparison = 0;
          }
        }
      }
    }
    return comparison;
  }
}
