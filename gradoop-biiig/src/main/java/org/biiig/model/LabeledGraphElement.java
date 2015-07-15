package org.biiig.model;

/**
 * Created by peet on 25.06.15.
 */
public abstract class LabeledGraphElement {
  protected final String label;

  protected LabeledGraphElement(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
