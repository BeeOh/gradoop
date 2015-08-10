package org.biiig.fsm.gspan.common;

/**
 * Created by p3et on 09.07.15.
 *
 * POJO representing a label and the number of supporting graphs (support)
 */
public class LabelSupport implements Comparable<LabelSupport> {
  /**
   * label
   */
  private final String label;
  /**
   * supporter count
   */
  private final Integer support;

  // behaviour

  /**
   * constructor
    * @param label label
   * @param support support
   */
  public LabelSupport(String label, Integer support) {
    this.label = label;
    this.support = support;
  }

  // override methods

  /**
   * comparator, first by support, second by labels
   * @param other other
   * @return comparison
   */
  @Override
  public int compareTo(LabelSupport other) {
    int comparison = other.support - this.support;

    if (comparison == 0) {

      comparison = this.label.compareTo(other.label) * -1;

      if (comparison == 0) {
        comparison = this.hashCode() - other.hashCode();
      }
    }

    return comparison;
  }

  // getters and setters

  public String getLabel() {
    return label;
  }
}
