package org.biiig.model;

/**
 * Created by peet on 25.06.15.
 */
public class LabeledVertex extends LabeledGraphElement
  implements Comparable<LabeledVertex> {

  public LabeledVertex(String label) {
    super(label);
  }

  public String toString(){
    return "(" + label + ")";
  }

  @Override
  public int compareTo(LabeledVertex otherVertex) {
    return this.getLabel().compareTo(otherVertex.getLabel());
  }


}
