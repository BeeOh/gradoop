package org.biiig.fsm.common;

/**
 * Transitional POJO representing a string-labelled edge
 *
 * Created by p3et on 25.06.15.
 */
public class LabeledEdge extends LabeledGraphElement
  implements Comparable<LabeledEdge> {
  /**
   * source vertex
   */
  private final LabeledVertex sourceVertex;
  /**
   * target vertex
   */
  private final LabeledVertex targetVertex;

  // behaviour

  /**
   * constructor
   * @param sourceVertex source vertex
   * @param label edge label
   * @param targetVertex target vertex
   */
  public LabeledEdge(
    LabeledVertex sourceVertex, String label, LabeledVertex targetVertex) {
    super(label);
    this.sourceVertex = sourceVertex;
    this.targetVertex = targetVertex;
  }

  // convenience methods

  /**
   * checks if a vertex is source or target of this edge
   * @param vertex vertex to check
   * @return true, if vertex is source or target of this edge
   */
  public boolean contains(LabeledVertex vertex) {
    return sourceVertex == vertex || targetVertex == vertex;
  }
  /**
   * determines smallest vertex from source and target in alphabetical order
   * @return alphabetically smaller vertex
   */
  private LabeledVertex getMinVertex() {
    return
      sourceVertex.compareTo(targetVertex) < 0 ? sourceVertex : targetVertex;
  }
  /**
   * determines largest vertex from source and target in alphabetical order
   * @return alphabetically smaller vertex
   */
  private LabeledVertex getMaxVertex() {
    return this.getOtherVertex(this.getMinVertex());
  }
  /**
   * determines the other vertex of an edge
   * @param vertex vertex to check
   * @return source, if target is contained || target, if source contained
   */
  public LabeledVertex getOtherVertex(LabeledVertex vertex) {
    LabeledVertex otherVertex = null;

    if (this.contains(vertex)) {
      otherVertex = vertex == sourceVertex ? targetVertex : sourceVertex;
    }

    return otherVertex;
  }

  // override methods

  /**
   * comparator
   * @param other other edge
   * @return comparison result
   */
  @Override
  public int compareTo(LabeledEdge other) {

    // minimum vertex label
    int comparison = this.getMinVertex().compareTo(other.getMinVertex());

    if (comparison == 0) {
      // edge label
      comparison = this.getLabel().compareTo(other.getLabel());

      if (comparison == 0) {
        // maximum vertex label
        comparison = this.getMaxVertex().compareTo(other.getMaxVertex());

        if (comparison == 0) {
          // direction

          if (this.getSourceVertex().compareTo(other.getSourceVertex()) == 0) {
            // same direction
            comparison = 0;
          } else if (this.getSourceVertex() == this.getMinVertex()) {
            comparison = -1;
          } else {
            comparison = 1;
          }
        }
      }
    }

    return comparison;
  }
  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    return sourceVertex.toString() + "-" + label + "->" + targetVertex
      .toString();
  }

  // getters and setters

  public LabeledVertex getSourceVertex() {
    return sourceVertex;
  }

  public LabeledVertex getTargetVertex() {
    return targetVertex;
  }
}
