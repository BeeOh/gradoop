package org.biiig.fsm.gspan.multithreaded;

import org.biiig.model.LabeledGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peet on 15.07.15.
 */
public class MTGSpan {

  /**
   * workflow for GSpan frequent subgraph mining
   * @param graphs search space
   * @param threshold minimum frequency of frequent subgraphs
   * @return map of frequent subgraphs and frequencies
   */
  public Map<LabeledGraph,Float> frequentSubgraphs(
    Collection<LabeledGraph> graphs, float threshold) {

    // calculate minimum support from threshold
    Long minSupport = Float.valueOf(graphs.size() * threshold).longValue();

    // instantiate master node
    MTGSpanMaster master = new MTGSpanMaster(minSupport);

    // distribute graphs equally to workers
    master.distribute(graphs);

    // generate dictionaries for frequent vertex and edge labels
    master.generateFrequentLabelDictionaries();

    System.out.println("*** dictionaries ***");
    System.out.println(master.getVertexLabelDictionary());
    System.out.println(master.getEdgeLabelDictionary());


    // count edge pattern support and prune by infrequent patterns

    System.out.println("*** edge patterns ***");

    master.indexFrequentEdgePatterns();

    for(MTGSpanWorker worker : master.getWorkers()) {
      System.out.println(worker.toString() + " : " + worker
        .getEdgePatternIndex()
        .keySet());
    }




    return new HashMap<>();
  }
}
