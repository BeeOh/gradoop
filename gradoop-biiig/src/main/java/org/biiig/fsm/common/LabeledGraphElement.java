package org.biiig.fsm.common;

/**
 * Transitional POJO representing a generalization of string-labelled
 * vertices and edges
 *
 * Created by p3et on 25.06.15.
 */
public abstract class LabeledGraphElement {
  /**
   * string representing vertex or edge label
   */
  protected final String label;

  /**
   * constructor
   * @param label label of the new element
   */
  protected LabeledGraphElement(String label) {
    this.label = label;
  }

  // getters and setters

  public String getLabel() {
    return label;
  }
}
