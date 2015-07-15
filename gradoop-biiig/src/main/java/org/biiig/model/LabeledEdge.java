package org.biiig.model;

/**
 * Created by peet on 25.06.15.
 */
public class LabeledEdge extends LabeledGraphElement
  implements Comparable<LabeledEdge> {

  private final LabeledVertex sourceVertex;
  private final LabeledVertex targetVertex;

  public LabeledEdge(LabeledVertex sourceVertex, String label,
    LabeledVertex targetVertex) {
    super(label);
    this.sourceVertex = sourceVertex;
    this.targetVertex = targetVertex;
  }

  public LabeledVertex getSourceVertex() {
    return sourceVertex;
  }

  public LabeledVertex getTargetVertex() {
    return targetVertex;
  }

  public String toString(){
    return sourceVertex.toString() + "-" + label + "->" + targetVertex
      .toString();
  }

  public boolean contains(LabeledVertex vertex) {
    return sourceVertex == vertex || targetVertex == vertex;
  }

  public LabeledVertex getOtherVertex(LabeledVertex vertex) {
    return vertex == sourceVertex ? targetVertex : sourceVertex;
  }

  @Override
  public int compareTo(LabeledEdge otherEdge) {

    // minimum vertex label
    int comparison = this.getMinVertex().compareTo(otherEdge.getMinVertex());

    if (comparison == 0) {
      // edge label
      comparison = this.getLabel().compareTo(otherEdge.getLabel());

      if ( comparison == 0) {
        // maximum vertex label
        comparison = this.getMaxVertex().compareTo(otherEdge.getMaxVertex());

        if (comparison == 0) {
          // direction

          if (this.getSourceVertex().compareTo(otherEdge.getSourceVertex()) == 0) {
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

  public LabeledVertex getMinVertex() {
    return sourceVertex.compareTo(targetVertex) < 0
      ? sourceVertex : targetVertex;
  }

  public LabeledVertex getMaxVertex() {
    return this.getOtherVertex(this.getMinVertex());
  }
}
