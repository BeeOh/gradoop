package org.biiig.fsm.gspan;

/**
 * Created by peet on 15.07.15.
 */
public class GSpanEdge implements Comparable<GSpanEdge> {

  private final GSpanVertex sourceVertex;
  private final Integer label;
  private final GSpanVertex targetVertex;

  public GSpanEdge(GSpanVertex sourceVertex, Integer label, GSpanVertex targetVertex) {
    this.sourceVertex = sourceVertex;
    this.label = label;
    this.targetVertex = targetVertex;
  }

  @Override
  public int compareTo(GSpanEdge other) {

    int comparison = this.sourceVertex.compareTo(other.sourceVertex);

    if(comparison == 0) {
      comparison = (int)(this.label - other.label);
    }

    if(comparison == 0) {
      comparison = this.targetVertex.compareTo(other.targetVertex);
    }

    return comparison;
  }

  @Override
  public String toString(){
    return sourceVertex.toString() + "-" + this.label + "->" + targetVertex
      .toString();
  }

  public Integer getLabel() {
    return label;
  }

  public GSpanVertex getOtherVertex(GSpanVertex vertex) {
    return vertex == sourceVertex ? targetVertex : sourceVertex;
  }

  public GSpanVertex getSourceVertex() {
    return this.sourceVertex;
  }

  public GSpanVertex getTargetVertex() {
    return targetVertex;
  }

  public boolean contains(GSpanVertex rightmostVertex) {
    return sourceVertex == rightmostVertex || targetVertex == rightmostVertex;
  }
}
