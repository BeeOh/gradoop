package org.biiig.fsm.gspan.common;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by peet on 14.08.15.
 */
public class GSpanEdgeComparator implements Comparator<GSpanEdge>,
  Serializable {
  @Override
  public int compare(GSpanEdge e1, GSpanEdge e2) {
    GSpanVertexComparator vertexComparator = new GSpanVertexComparator();

    int comparison = vertexComparator.compare(
      e1.getSourceVertex(), e2.getSourceVertex());

    if (comparison == 0) {
      comparison = e1.getLabel() - e2.getLabel();
    }

    if (comparison == 0) {
      comparison = vertexComparator.compare(
        e1.getTargetVertex(), e2.getTargetVertex());
    }

    return comparison;
  }
}
