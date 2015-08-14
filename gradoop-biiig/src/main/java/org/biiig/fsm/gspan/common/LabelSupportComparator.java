package org.biiig.fsm.gspan.common;

import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by peet on 14.08.15.
 */
public class LabelSupportComparator
  implements Comparator<Pair<String, Integer>>, Serializable {

  @Override
  public int compare(Pair<String, Integer> p1, Pair<String, Integer> p2) {
    int comparison = p2.getValue() - p1.getValue();

    if (comparison == 0) {

      comparison = p1.getKey().compareTo(p2.getKey()) * -1;

      if (comparison == 0) {
        comparison = p1.hashCode() - p2.hashCode();
      }
    }

    return comparison;
  }
}
