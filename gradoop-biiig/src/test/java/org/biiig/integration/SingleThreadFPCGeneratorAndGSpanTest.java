package org.biiig.integration;

import org.biiig.datagen.singlenode.SingleNodeFPCGenerator;
import org.biiig.fsm.gspan.SingleThreadGSpan;
import org.biiig.model.LabeledGraph;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by peet on 09.07.15.
 */
public class SingleThreadFPCGeneratorAndGSpanTest {
  @Test
  public void testGSpanOnFPC(){
    SingleNodeFPCGenerator generator = new SingleNodeFPCGenerator();
    List<LabeledGraph> graphs = generator.generate(1,2);

    SingleThreadGSpan gSpan = new SingleThreadGSpan();

    // 100% frequency
    Map<LabeledGraph,Float> frequentSubgraphs
      = gSpan.findFrequentSubgraphs(graphs, 0.8f);

    for (Map.Entry<LabeledGraph, Float> entry : frequentSubgraphs.entrySet()) {
      System.out.println(entry.getKey() + ":" + entry.getValue());
    }
  }
}
