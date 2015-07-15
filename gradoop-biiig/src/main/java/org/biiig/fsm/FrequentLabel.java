package org.biiig.fsm;

/**
 * Created by peet on 09.07.15.
 */
public class FrequentLabel implements Comparable<FrequentLabel> {

  private final String label;
  private final int support;

  public FrequentLabel(String label, int support) {
    this.label = label;
    this.support = support;
  }


  @Override
  public int compareTo(FrequentLabel other) {
    int comparison = other.support - this.support;

    if (comparison == 0) {

      comparison = this.label.compareTo(other.label);

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
