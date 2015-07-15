package org.biiig.fsm.gspan;

import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleThreadGSpanTest {
  @Test
  public void testMinimalEdgePruning(){
    SingleThreadGSpan gSpan = new SingleThreadGSpan();

    List<LabeledGraph> graphs = new ArrayList<>();


    // (A)-a->(A)-b->(B)<-c-(B)

    LabeledGraph gA = new LabeledGraph();
    graphs.add(gA);

    LabeledVertex gAvA1 = gA.newVertex("A");
    LabeledVertex gAvA2 = gA.newVertex("A");
    gA.newEdge(gAvA1,"a",gAvA2);
    LabeledVertex gAvB1 = gA.newVertex("B");
    gA.newEdge(gAvA2,"b",gAvB1);
    LabeledVertex gAvB2 = gA.newVertex("B");
    gA.newEdge(gAvB2,"c",gAvB1);


    // (A)-b->(A)-b->(B)<-c-(B)

    LabeledGraph gB = new LabeledGraph();
    graphs.add(gB);

    LabeledVertex gBvA1 = gB.newVertex("A");
    LabeledVertex gBvA2 = gB.newVertex("A");
    gB.newEdge(gBvA1,"b",gBvA2);
    LabeledVertex gBvB1 = gB.newVertex("B");
    gB.newEdge(gBvA2,"b",gBvB1);
    LabeledVertex gBvB2 = gB.newVertex("B");
    gB.newEdge(gBvB2,"c",gBvB1);


    // (A)-a->
    //        (A)-b->(B)
    // (A)-a->

    LabeledGraph gC = new LabeledGraph();
    graphs.add(gC);

    LabeledVertex gCvA1 = gC.newVertex("A");
    LabeledVertex gCvA2 = gC.newVertex("A");
    LabeledVertex gCvA3 = gC.newVertex("A");
    gC.newEdge(gCvA1,"a",gCvA2);
    gC.newEdge(gCvA3,"a",gCvA2);
    LabeledVertex gCvB1 = gC.newVertex("B");
    gC.newEdge(gCvA2,"b",gCvB1);

    Map<LabeledGraph,Float> frequentSubgraphs =
      gSpan.findFrequentSubgraphs(graphs,0.7f);

    System.out.println(frequentSubgraphs);

  }

}