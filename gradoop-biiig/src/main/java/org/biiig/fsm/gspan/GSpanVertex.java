package org.biiig.fsm.gspan;

/**
 * Created by peet on 15.07.15.
 */
public class GSpanVertex implements Comparable<GSpanVertex> {

  private final Integer label;

  public GSpanVertex(Integer label) {
    this.label = label;
  }

  @Override
  public int compareTo(GSpanVertex other) {
    return (int)(this.label - other.label);
  }

  @Override
  public String toString(){
    return "(" + label + ":" + hashCode() + ")";
  }

  public Integer getLabel() {
    return label;
  }
}
