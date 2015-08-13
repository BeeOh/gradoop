package org.biiig.fsm.common;


import org.biiig.fsm.gspan.singlenode.GSpanMaster;
import org.biiig.fsm.gspan.singlenode.GSpanWorker;
import org.hamcrest.core.Is;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Created by peet on 10.08.15.
 */
public abstract class AbstractCorrectnessTest {

  protected void checkAssertions(Collection<LabeledGraph> frequentSubgraphs,
    Collection<LabeledGraph> expectedSubgraphs) {

    assertThat(frequentSubgraphs.size(), Is.is(expectedSubgraphs.size()));

    // all frequent subgraphs are distinct
    for(LabeledGraph leftGraph : frequentSubgraphs) {
      for(LabeledGraph rightGraph : frequentSubgraphs) {
        if(leftGraph != rightGraph) {
          assertFalse(leftGraph.isIsomorphicTo(rightGraph));
        }
      }
    }

    // every frequent subgraph was expected
    Map<LabeledGraph,LabeledGraph> graphMap = new HashMap<>();

    for(LabeledGraph expectedSubgraph : expectedSubgraphs) {
      for(LabeledGraph frequentSubgraph : frequentSubgraphs) {
        if(!graphMap.containsKey(frequentSubgraph)
          && expectedSubgraph.isIsomorphicTo(frequentSubgraph)) {
          graphMap.put(frequentSubgraph,expectedSubgraph);
        }
      }
    }

    for(LabeledGraph expectedSubgraph : expectedSubgraphs) {
      if(!graphMap.containsValue(expectedSubgraph)){
        System.out.println("not found : " + expectedSubgraph);
        System.out.println(expectedSubgraph.getAdjacencyMatrixCode());
      }
    }
    for(LabeledGraph frequentSubgraph : frequentSubgraphs) {
      if(!graphMap.containsKey(frequentSubgraph)){
        System.out.println("not expected : " + frequentSubgraph);
        System.out.println(frequentSubgraph.getAdjacencyMatrixCode());
      }
    }

    assertThat(graphMap.size(), Is.is(expectedSubgraphs.size()));
  }

  public void distribute(Collection<LabeledGraph> searchSpace,
    GSpanMaster master) {

    List<GSpanWorker> workers =  master.getWorkers();
    int numberOfWorkers = workers.size();
    int graphCount = 0;

    for(LabeledGraph graph : searchSpace) {
      workers.get(graphCount % numberOfWorkers).getGraphs().add(graph);
      graphCount++;
    }

  }

}
