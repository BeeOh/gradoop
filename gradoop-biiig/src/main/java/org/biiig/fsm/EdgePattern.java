package org.biiig.fsm;

import org.biiig.model.LabeledEdge;
import org.biiig.model.LabeledVertex;

/**
 * Created by peet on 09.07.15.
 */
public class EdgePattern implements Comparable<EdgePattern> {

  private final String minVertexLabel;
  private final String edgeLabel;
  private final String maxVertexLabel;

  public String getMinVertexLabel() {
    return minVertexLabel;
  }

  public String getEdgeLabel() {
    return edgeLabel;
  }

  public String getMaxVertexLabel() {
    return maxVertexLabel;
  }

  public boolean isOutgoing() {
    return outgoing;
  }

  private final boolean outgoing;

  public EdgePattern(LabeledEdge edge) {

    String sourceVertexLabel = edge.getSourceVertex().getLabel();
    String targetVertexLabel = edge.getTargetVertex().getLabel();

    if(sourceVertexLabel.compareTo(targetVertexLabel) <= 0) {
      this.minVertexLabel = sourceVertexLabel;
      this.maxVertexLabel = targetVertexLabel;
      this.outgoing = true;
    } else {
      this.minVertexLabel = targetVertexLabel;
      this.maxVertexLabel = sourceVertexLabel;
      this.outgoing = false;
    }

    this.edgeLabel = edge.getLabel();
  }

  @Override
  public int compareTo(EdgePattern other) {

    // minimum vertex label
    int comparison = this.minVertexLabel.compareTo(other.minVertexLabel);

    if (comparison == 0) {
      // out before in
      if (this.outgoing && other.outgoing) {
        // same direction

        // edge label
        comparison = this.edgeLabel.compareTo(other.edgeLabel);

        if ( comparison == 0) {
          // maximum vertex label
          comparison = this.maxVertexLabel.compareTo(other.maxVertexLabel);
        }
      } else if (this.outgoing) {
        comparison = -1;
      } else {
        comparison = 1;
      }
    }
    return comparison;
  }

  public String toString() {
    String string = "(" + minVertexLabel + ")";

    if(!outgoing) {
      string += "<";
    }

    string += "-" + edgeLabel + "-";

    if(outgoing) {
      string += ">";
    }

    string += "(" + maxVertexLabel + ")";

    return string;
  }
}
