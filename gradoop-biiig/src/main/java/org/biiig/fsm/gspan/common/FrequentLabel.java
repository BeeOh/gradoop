package org.biiig.fsm.gspan.common;

/**
 * Created by peet on 09.07.15.
 */
public class FrequentLabel implements Comparable<FrequentLabel> {

  private final String label;
  private final Integer support;

  public FrequentLabel(String label, Integer support) {
    this.label = label;
    this.support = support;
  }


  @Override
  public int compareTo(FrequentLabel other) {
    int comparison = other.support - this.support;

    if (comparison == 0) {

      comparison = this.label.compareTo(other.label)*-1;

      if (comparison == 0) {
        comparison = this.hashCode() - other.hashCode();
      }
    }

    return comparison;
  }

  public String getLabel() {
    return label;
  }
}
