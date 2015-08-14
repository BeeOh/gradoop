package org.biiig.fsm.gspan.common;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by peet on 14.08.15.
 */
public class GSpanVertexComparator implements Comparator<GSpanVertex>,
  Serializable {
  @Override
  public int compare(GSpanVertex v1, GSpanVertex v2) {
    return v1.getLabel() - v2.getLabel();
  }
}
