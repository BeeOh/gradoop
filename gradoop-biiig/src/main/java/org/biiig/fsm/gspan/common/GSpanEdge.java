package org.biiig.fsm.gspan.common;

/**
 * Created by p3et on 15.07.15.
 *
 * POJO describing an edge of a GSpan graph,
 * i.e., a directed edge providing an integer label
 */
public class GSpanEdge implements Comparable<GSpanEdge> {

  /**
   * source vertex reference
   */
  private final GSpanVertex sourceVertex;


  /**
   * integer label
   */
  private final Integer label;
  /**
   * target vertex  reference
   */
  private final GSpanVertex targetVertex;

  // behaviour

  /**
   * constructor
   * @param sourceVertex source vertex reference
   * @param label integer label
   * @param targetVertex target vertex reference
   */
  public GSpanEdge(
    GSpanVertex sourceVertex, Integer label, GSpanVertex targetVertex) {
    this.sourceVertex = sourceVertex;
    this.label = label;
    this.targetVertex = targetVertex;
  }

  // override methods

  /**
   * comparator
   * @param other other edge
   * @return result of comparison
   */
  @Override
  public int compareTo(GSpanEdge other) {

    int comparison = this.sourceVertex.compareTo(other.sourceVertex);

    if (comparison == 0) {
      comparison = this.label - other.label;
    }

    if (comparison == 0) {
      comparison = this.targetVertex.compareTo(other.targetVertex);
    }

    return comparison;
  }
  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    return sourceVertex.toString() +
      "-" + this.label +
      "->" + targetVertex.toString();
  }

  // getters and setters

  public Integer getLabel() {
    return label;
  }

  public GSpanVertex getSourceVertex() {
    return sourceVertex;
  }

  public GSpanVertex getTargetVertex() {
    return targetVertex;
  }
}
