package org.biiig.fsm.gspan.singlenode;


import org.biiig.fsm.common.AbstractCorrectnessTest;
import org.biiig.fsm.common.FSMTestData;
import org.biiig.fsm.common.LabeledGraph;
import org.biiig.fsm.gspan.common.DfsCode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


public class GSpanCorrectnessTest extends AbstractCorrectnessTest {

  @Test
  public void test() {
    Collection<FSMTestData> testDataSets = new ArrayList<>();
    testDataSets.add(getSimpleTestData());
    testDataSets.add(getTestDataWithCycles());
    testDataSets.add(getTestDataWithParallelEdges());
    testDataSets.add(getTestDataWithLoops());

    for (FSMTestData testData : testDataSets) {

      System.out.println("\n*** test single node GSpan " +
        testData.getDescription() + " ***\n");

      GSpanMaster master = new GSpanMaster();

      distribute(testData.getSearchSpace(),master);

      master.mine(testData.getThreshold());

      System.out.println(master.getVertexLabelDictionary());
      System.out.println(master.getEdgeLabelDictionary());

      for(Map<DfsCode,Integer> frequentDfsCodeSupports :
        master.getPartitionedFrequentDfsCodeSupports()
          .values()) {
        for(Map.Entry<DfsCode,Integer> entry : frequentDfsCodeSupports.entrySet()) {
          System.out.println(entry.getValue() + "\t" + entry.getKey());
        }
      }

      for(Map.Entry<LabeledGraph, Float> graphSupport :
        master.getFrequentSubgraphs().entrySet()) {
        System.out.println(graphSupport.getValue() + "\t" +
          graphSupport.getKey());
      }

      checkAssertions(master.getFrequentSubgraphs().keySet(),
        testData.getExpectedResult());

    }
  }

  public void distribute(Collection<LabeledGraph> searchSpace,
    GSpanMaster master) {

    Collection<GSpanWorker> workers =  master.getWorkers();

    int graphsPerWorker = searchSpace.size() / workers.size();

    Long graphCount = 0l;

    Iterator<GSpanWorker> workerIterator = workers.iterator();
    GSpanWorker worker = workerIterator.next();

    for(LabeledGraph graph : searchSpace) {

      if(graphCount >= graphsPerWorker) {
        worker = workerIterator.next();
      }

      graphCount++;
      worker.getGraphs().add(graph);
    }
  }
}