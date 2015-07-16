package org.biiig.fsm.gspan;

/**
 * Created by peet on 15.07.15.
 */
public class GSpanEdge implements Comparable<GSpanEdge> {

  private final GSpanVertex source;
  private final Long label;
  private final GSpanVertex target;

  public GSpanEdge(GSpanVertex source, Long label, GSpanVertex target) {
    this.source = source;
    this.label = label;
    this.target = target;
  }

  @Override
  public int compareTo(GSpanEdge other) {

    int comparison = this.source.compareTo(other.source);

    if(comparison == 0) {
      comparison = (int)(this.label - other.label);
    }

    if(comparison == 0) {
      comparison = this.target.compareTo(other.target);
    }

    return comparison;
  }

  @Override
  public String toString(){
    return source.toString() + "-" + this.label + "->" + target.toString();
  }
}
