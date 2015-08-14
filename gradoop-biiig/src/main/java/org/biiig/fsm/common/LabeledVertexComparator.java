package org.biiig.fsm.common;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by peet on 14.08.15.
 */
public class LabeledVertexComparator implements Comparator<LabeledVertex>,
  Serializable {

  @Override
  public int compare(LabeledVertex v1, LabeledVertex v2) {
    return v1.getLabel().compareTo(v2.getLabel());
  }
}
