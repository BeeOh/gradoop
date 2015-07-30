package org.biiig.fsm.gspan;


import org.biiig.fsm.gspan.multithreaded.MTGSpan;
import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;
import org.gradoop.model.Labeled;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MTGSpanTest {

  @Test
  public void testWithoutCyclesLoopsAndParallelEdges() {

    List<LabeledGraph> graphs = new ArrayList<>();

    // (Z)-z->(X)<-a-(A)
    //           <-a-(A)

    LabeledGraph gA = new LabeledGraph();
    graphs.add(gA);

    LabeledVertex gAvX = gA.newVertex("X");
    LabeledVertex gAvZ = gA.newVertex("Z");
    LabeledVertex gAvA1 = gA.newVertex("A");
    LabeledVertex gAvA2 = gA.newVertex("A");

    gA.newEdge(gAvZ,"z",gAvX);
    gA.newEdge(gAvA1,"a",gAvX);
    gA.newEdge(gAvA2,"a",gAvX);


    // (Z)-z->(X)<-y-(Y)
    //           <-y-(Y)

    LabeledGraph gB = new LabeledGraph();
    graphs.add(gB);

    LabeledVertex gBvX = gB.newVertex("X");
    LabeledVertex gBvZ = gB.newVertex("Z");
    LabeledVertex gBvY1 = gB.newVertex("Y");
    LabeledVertex gBvY2 = gB.newVertex("Y");

    gB.newEdge(gBvZ,"z",gBvX);
    gB.newEdge(gBvY1,"y",gBvX);
    gB.newEdge(gBvY2,"y",gBvX);

    // (Y)-y->(X)<-z-(Z)
    //           <-a-(A)

    LabeledGraph gC = new LabeledGraph();
    graphs.add(gC);

    LabeledVertex gCvX = gC.newVertex("X");
    LabeledVertex gCvY = gC.newVertex("Y");
    LabeledVertex gCvZ = gC.newVertex("Z");
    LabeledVertex gCvA = gC.newVertex("A");

    gC.newEdge(gCvY,"y",gCvX);
    gC.newEdge(gCvZ,"z",gCvX);
    gC.newEdge(gCvA,"a",gCvX);


    // (A)-a->(X) => (0:A)-a->(1:X)
    // (X)<-y-(Y) => (0:Y)-y->(1:X)
    // (X)<-z-(Z) => (0:Z)-z->(1:X)
    // (A)-a->(X)<-z-(Z) => (0:A)-a->(1:X),(2:Z)-z->(1:X)
    // (Y)-y->(X)<-z-(Z) => (1:Y)-y->(0:X),(2:Z)-z->(0:X)

    MTGSpan gSpan = new MTGSpan();
    Map<LabeledGraph,Float> frequentSubgraphs =
      gSpan.frequentSubgraphs(graphs, 0.7f);

  }

  public void testMinimalEdgePruning(){
    MTGSpan gSpan = new MTGSpan();

    List<LabeledGraph> graphs = new ArrayList<>();


    // (A)-a->(A)-b->(B)<-c-(B)-dL
    //           -e->

    LabeledGraph gA = new LabeledGraph();
    graphs.add(gA);

    LabeledVertex gAvA1 = gA.newVertex("A");
    LabeledVertex gAvA2 = gA.newVertex("A");
    gA.newEdge(gAvA1,"a",gAvA2);
    LabeledVertex gAvB1 = gA.newVertex("B");
    gA.newEdge(gAvA2,"b",gAvB1);
    gA.newEdge(gAvA2,"e",gAvB1);
    LabeledVertex gAvB2 = gA.newVertex("B");
    gA.newEdge(gAvB2,"c",gAvB1);
    gA.newEdge(gAvB2,"d",gAvB2);



    // (A)-b->(A)-b->(B)<-c-(B)
    //           -e->


    LabeledGraph gB = new LabeledGraph();
    graphs.add(gB);

    LabeledVertex gBvA1 = gB.newVertex("A");
    LabeledVertex gBvA2 = gB.newVertex("A");
    gB.newEdge(gBvA1,"b",gBvA2);
    LabeledVertex gBvB1 = gB.newVertex("B");
    gB.newEdge(gBvA2,"b",gBvB1);
    gB.newEdge(gBvA2,"e",gBvB1);
    LabeledVertex gBvB2 = gB.newVertex("B");
    gB.newEdge(gBvB2,"c",gBvB1);


    // (A)-a->
    //        (A)-b->(B)-dL
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
    gC.newEdge(gCvB1,"d",gCvB1);


    Map<LabeledGraph,Float> frequentSubgraphs =
      gSpan.frequentSubgraphs(graphs, 0.7f);

    //System.out.println(frequentSubgraphs);

  }
}