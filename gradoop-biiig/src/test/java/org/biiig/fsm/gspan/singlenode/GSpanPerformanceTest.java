package org.biiig.fsm.gspan.singlenode;

import org.biiig.fsm.common.AbstractCorrectnessTest;
import org.biiig.fsm.common.FSMTestData;
import org.biiig.fsm.common.FSMTestDataGenerator;
import org.biiig.fsm.common.LabeledGraph;
import org.biiig.fsm.gspan.common.DfsCode;
import org.junit.Test;

import java.util.Map;

/**
 * Created by peet on 11.08.15.
 */
public class GSpanPerformanceTest extends AbstractCorrectnessTest {

  @Test
  public void test(){

    int numberOfGraphs = 1000;

    System.out.println("\n*** test  GSpan with " + numberOfGraphs +
      " complex graphs ***\n");

    FSMTestData testData = FSMTestDataGenerator.newComplexScalableTestData(numberOfGraphs);

    GSpanMaster master = new GSpanMaster();

    distribute(testData.getSearchSpace(), master);

    System.out.println(master.getWorkers());

    master.mine(testData.getThreshold());

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
        graphSupport.getKey() + "\t" + graphSupport.getKey().getAdjacencyMatrixCode());
    }

    checkAssertions(master.getFrequentSubgraphs().keySet(),
      testData.getExpectedResult());
  }
}
