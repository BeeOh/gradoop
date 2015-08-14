package org.biiig.fsm.common;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by peet on 14.08.15.
 */
public class LabeledEdgeComparator implements Comparator<LabeledEdge>,
  Serializable {

  @Override
  public int compare(LabeledEdge e1, LabeledEdge e2) {
    LabeledVertexComparator vertexComparator = new LabeledVertexComparator();

    // minimum vertex label
    int comparison = vertexComparator.compare(
      e1.getMinVertex(), e2.getMinVertex());

    if (comparison == 0) {
      // edge label
      comparison = e1.getLabel().compareTo(e2.getLabel());

      if (comparison == 0) {
        // maximum vertex label
        comparison = vertexComparator.compare(
          e1.getMaxVertex(), e2.getMaxVertex());

        if (comparison == 0) {
          // direction

          if (vertexComparator.compare(
            e1.getSourceVertex(), e2.getSourceVertex()) == 0) {
            // same direction
            comparison = 0;
          } else if (e1.getSourceVertex() == e1.getMinVertex()) {
            comparison = -1;
          } else {
            comparison = 1;
          }
        }
      }
    }

    return comparison;
  }
}
