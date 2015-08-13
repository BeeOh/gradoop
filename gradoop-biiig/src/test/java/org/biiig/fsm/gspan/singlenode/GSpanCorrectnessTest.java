package org.biiig.fsm.gspan.singlenode;


import org.biiig.fsm.common.AbstractCorrectnessTest;
import org.biiig.fsm.common.FSMTestData;
import org.biiig.fsm.common.FSMTestDataGenerator;
import org.biiig.fsm.common.LabeledGraph;
import org.biiig.fsm.gspan.common.DfsCode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class GSpanCorrectnessTest extends AbstractCorrectnessTest {

  @Test
  public void test() {
    Collection<FSMTestData> testDataSets = new ArrayList<>();
    testDataSets.add(FSMTestDataGenerator.newSimpleTestData());
    testDataSets.add(FSMTestDataGenerator.newTestDataWithCycles());
    testDataSets.add(FSMTestDataGenerator.newTestDataWithParallelEdges());
    testDataSets.add(FSMTestDataGenerator.newTestDataWithMirroredPath());
    testDataSets.add(FSMTestDataGenerator.newTestDataWithLoops());

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
}