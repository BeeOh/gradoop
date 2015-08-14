package org.biiig.fsm.common;

/**
 * Transitional POJO representing a string-labelled vertex
 *
 * Created by peet on 25.06.15.
 */
public class LabeledVertex extends LabeledGraphElement {
  /**
   * constructor
   * @param label vertex label
   */
  public LabeledVertex(String label) {
    super(label);
  }

  // override methods

  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    return "(" + label + ")";
  }

}
