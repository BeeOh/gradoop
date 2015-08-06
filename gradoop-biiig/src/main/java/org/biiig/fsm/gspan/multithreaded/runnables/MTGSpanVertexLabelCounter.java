package org.biiig.fsm.gspan.multithreaded.runnables;

import org.biiig.fsm.gspan.multithreaded.MTGSpanWorker;
import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;

/**
 * Created by peet on 15.07.15.
 */
public class MTGSpanVertexLabelCounter extends MTGSpanAbstractRunnable {
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
        Integer oldSupport = worker.getVertexLabelSupports().get(label);
        worker.getVertexLabelSupports().put(label,
          oldSupport == null ? 1 : oldSupport + 1);
      }
    }
  }
}
