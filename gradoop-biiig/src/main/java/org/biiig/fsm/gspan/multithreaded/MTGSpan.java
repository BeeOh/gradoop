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

    // instantiate master node
    MTGSpanMaster master = new MTGSpanMaster(graphs,threshold);

    // distribute graphs equally to workers
    master.distribute(graphs);

    // generate dictionaries for frequent vertex and edge labels
    master.mine();

    master.printDictionaries();
    master.printfrequentDfsCodes();
    master.printFrequentSubgraphs();

    return master.getFrequentSubgraphs();
  }
}
