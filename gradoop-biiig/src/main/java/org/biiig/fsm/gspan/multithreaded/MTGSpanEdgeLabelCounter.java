package org.biiig.fsm.gspan.multithreaded;

import org.biiig.model.LabeledEdge;
import org.biiig.model.LabeledGraph;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by peet on 15.07.15.
 */
public class MTGSpanEdgeLabelCounter extends MTGSSpanRunnable  {

  /**
   * constructor
   * @param worker calling worker
   */
  public MTGSpanEdgeLabelCounter(MTGSpanWorker worker) {
    super(worker);
  }

  /**
   * count local edge labels
   */
  @Override
  public void run() {
    for(LabeledGraph graph : worker.getGraphs()) {
      for(LabeledEdge edge : graph.getEdges()) {
        if(worker.getVertexLabelDictionary()
            .containsKey(edge.getSourceVertex().getLabel())
          && worker.getVertexLabelDictionary()
            .containsKey(edge.getTargetVertex().getLabel())) {

          String label = edge.getLabel();
          Long oldCount = worker.getEdgeLabelSupports().get(label);
          worker.getEdgeLabelSupports().put(label,
            oldCount == null ? 1 : oldCount + 1);
        }
      }
    }
  }
}
