package org.biiig.fsm.gspan.common;

/**
 * Created by p3et on 15.07.15.
 *
 * POJO representing a GSpan vertex, i.e., a vertex with integer label
 */
public class GSpanVertex implements Comparable<GSpanVertex> {
  /**
   * label of the vertex
   */
  private final Integer label;

  // behaviour

  /**
   * constructor
   * @param label label of the vertex
   */
  public GSpanVertex(Integer label) {
    this.label = label;
  }

  // override methods

  /**
   * comparator based on vertex label
   * @param other other vertex
   * @return comparison
   */
  @Override
  public int compareTo(GSpanVertex other) {
    return this.label - other.label;
  }

  /**
   * to string
   * @return string representation
   */
  @Override
  public String toString() {
    return "(" + label + ":" + hashCode() + ")";
  }

  // getters and setters

  public Integer getLabel() {
    return label;
  }
}
