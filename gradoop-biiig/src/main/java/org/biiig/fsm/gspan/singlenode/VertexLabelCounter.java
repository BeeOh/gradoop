package org.biiig.fsm.gspan.singlenode;

import org.biiig.fsm.common.LabeledGraph;
import org.biiig.fsm.common.LabeledVertex;

/**
 * runnable to count vertex labels support on a the worker
 *
 * Created by p3et on 15.07.15.
 */
public class VertexLabelCounter extends AbstractRunnable {
  /**
   * constructor
   * @param worker calling worker
   */
  public VertexLabelCounter(GSpanWorker worker) {
    super(worker);
  }

  /**
   * count local vertex label support
   */
  @Override
  public void run() {
    for (LabeledGraph graph : worker.getGraphs()) {
      for (LabeledVertex vertex : graph.getVertices()) {
        String label = vertex.getLabel();
        Integer oldSupport = worker.getVertexLabelSupports().get(label);
        worker.getVertexLabelSupports().put(
          label, oldSupport == null ? 1 : oldSupport + 1);
      }
    }
  }
}
