package org.biiig.fsm.gspan.multithreaded;

import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;

import java.util.Collection;
import java.util.Map;

/**
 * Created by peet on 15.07.15.
 */
public class MTGSpanVertexLabelCounter extends MTGSSpanRunnable {
  /**
   * constructor
   * @param worker calling worker
   */
  public MTGSpanVertexLabelCounter(MTGSpanWorker worker) {
    super(worker);
  }

  /**
   * count local vertex labels
   */
  @Override
  public void run() {
    for(LabeledGraph graph : worker.getGraphs()) {
      for(LabeledVertex vertex : graph.getVertices()) {
        String label = vertex.getLabel();
        Long oldSupport = worker.getVertexLabelSupports().get(label);
        worker.getVertexLabelSupports().put(label,
          oldSupport == null ? 1 : oldSupport + 1);
      }
    }
  }
}
