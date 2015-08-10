package org.biiig.fsm.gspan.singlenode;

import org.biiig.fsm.common.LabeledEdge;
import org.biiig.fsm.common.LabeledGraph;

/**
 * runnable to count edge labels support on a the worker
 *
 * Created by p3et on 15.07.15.
 */
public class EdgeLabelCounter extends AbstractRunnable {
  /**
   * constructor
   * @param worker calling worker
   */
  public EdgeLabelCounter(GSpanWorker worker) {
    super(worker);
  }

  /**
   * count local edge label support
   */
  @Override
  public void run() {
    for (LabeledGraph graph : worker.getGraphs()) {
      for (LabeledEdge edge : graph.getEdges()) {
        if (worker.getVertexLabelDictionary()
          .containsKey(edge.getSourceVertex().getLabel()) &&
          worker.getVertexLabelDictionary()
            .containsKey(edge.getTargetVertex().getLabel())) {

          String label = edge.getLabel();
          Integer oldCount = worker.getEdgeLabelSupports().get(label);
          worker.getEdgeLabelSupports().put(
            label, oldCount == null ? 1 : oldCount + 1);
        }
      }
    }
  }
}
