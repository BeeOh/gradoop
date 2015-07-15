package org.biiig.fsm.gspan;

import org.biiig.model.LabeledVertex;

/**
 * Created by peet on 01.07.15.
 */
public class DfsVertex implements Comparable<DfsVertex> {
  private final int position;
  private final LabeledVertex wrappedVertex;
  private boolean isFullyProcessed = false;

  public DfsVertex(int position, LabeledVertex vertex) {
    this.position = position;
    this.wrappedVertex = vertex;
  }

  @Override
  public int compareTo(DfsVertex other) {
    int comparison;

    if (this.position < other.position) {
      comparison = -1;
    } else if (this.position > other.position) {
      comparison = 1;
    } else {
      comparison = this.wrappedVertex.getLabel()
        .compareTo(other.wrappedVertex.getLabel());
    }

    return comparison;
  }

  public int getPosition() {
    return position;
  }

  public boolean isFullyProcessed() {
    return isFullyProcessed;
  }

  public void setFullyProcessed(boolean isFullyProcessed) {
    this.isFullyProcessed = isFullyProcessed;
  }

  public LabeledVertex getWrappedVertex() {
    return wrappedVertex;
  }

  public String toString(){
    return wrappedVertex.toString();
  }
}
