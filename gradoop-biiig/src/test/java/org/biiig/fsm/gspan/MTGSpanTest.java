package org.biiig.fsm.gspan;


import org.biiig.fsm.gspan.multithreaded.MTGSpan;
import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class MTGSpanTest {

  @Test
  public void testWithoutCyclesLoopsAndParallelEdges() {

    System.out.println("*** Test GSpan without cycles, loops and parallel " +
      "edges ***");

    // search space
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

    // expected result
    Set<LabeledGraph> expectedSubgraphs = new HashSet<>();

    // sA : (A)-a->(X)
    LabeledGraph sA = new LabeledGraph();
    expectedSubgraphs.add(sA);
    sA.newEdge(sA.newVertex("A"),"a",sA.newVertex("X"));

    // sB : (Y)-y->(X)
    LabeledGraph sB = new LabeledGraph();
    expectedSubgraphs.add(sB);
    sB.newEdge(sB.newVertex("Y"),"y",sB.newVertex("X"));

    // sC : (Z)-z->(X)
    LabeledGraph sC = new LabeledGraph();
    expectedSubgraphs.add(sC);
    sC.newEdge(sC.newVertex("Z"),"z",sC.newVertex("X"));

    // sD : (A)-a->(X)<-z-(Z)
    LabeledGraph sD = new LabeledGraph();
    expectedSubgraphs.add(sD);
    LabeledVertex sDvX = sD.newVertex("X");
    sD.newEdge(sD.newVertex("A"),"a",sDvX);
    sD.newEdge(sD.newVertex("Z"),"z",sDvX);

    // sE : (Y)-y->(X)<-z-(Z)
    LabeledGraph sE = new LabeledGraph();
    expectedSubgraphs.add(sE);
    LabeledVertex sEvX = sE.newVertex("X");
    sE.newEdge(sE.newVertex("Y"),"y",sEvX);
    sE.newEdge(sE.newVertex("Z"),"z",sEvX);

    // FSM
    MTGSpan gSpan = new MTGSpan();
    Map<LabeledGraph,Float> frequentSubgraphs =
      gSpan.frequentSubgraphs(graphs, 0.7f);

    checkAssertions(frequentSubgraphs, expectedSubgraphs);
  }

  @Test
  public void testCycles(){
    System.out.println("\n*** Test GSpan with cycles ***");

    // search space
    List<LabeledGraph> graphs = new ArrayList<>();

    //   <-a-(B)-a->
    // (A)  <-a-   (A)

    LabeledGraph gA = new LabeledGraph();
    graphs.add(gA);
    LabeledVertex gAvA1 = gA.newVertex("A");
    LabeledVertex gAvB = gA.newVertex("B");
    LabeledVertex gAvA2 = gA.newVertex("A");
    gA.newEdge(gAvB,"a",gAvA1);
    gA.newEdge(gAvB,"a",gAvA2);
    gA.newEdge(gAvA2,"a",gAvA1);

    //   -a->(A)<-a-
    // (A)  <-a-   (B)

    LabeledGraph gB = new LabeledGraph();
    graphs.add(gB);
    LabeledVertex gBvA1 = gB.newVertex("A");
    LabeledVertex gBvA2 = gB.newVertex("A");
    LabeledVertex gBvB = gB.newVertex("B");
    gB.newEdge(gBvA1,"a",gBvA2);
    gB.newEdge(gBvB,"a",gBvA1);
    gB.newEdge(gBvB,"a",gBvA2);

    // expected result
    Set<LabeledGraph> expectedSubgraphs = new HashSet<>();

    // sBA : (B)-a->(A)
    LabeledGraph sBA = new LabeledGraph();
    expectedSubgraphs.add(sBA);
    sBA.newEdge(sBA.newVertex("B"),"a",sBA.newVertex("A"));

    // sABA : (A)<-a-(B)-a->(A)
    LabeledGraph sABA = new LabeledGraph();
    expectedSubgraphs.add(sABA);
    LabeledVertex sABAvB = sABA.newVertex("B");
    sABA.newEdge(sABAvB, "a", sABA.newVertex("A"));
    sABA.newEdge(sABAvB, "a", sABA.newVertex("A"));

    // sBAoA : (B)-a->(A)-a->(A)
    LabeledGraph sBAoA = new LabeledGraph();
    expectedSubgraphs.add(sBAoA);
    LabeledVertex sBAoAvA = sBAoA.newVertex("A");
    sBAoA.newEdge(sBAoA.newVertex("B"), "a", sBAoAvA);
    sBAoA.newEdge(sBAoAvA, "a", sBAoA.newVertex("A"));

    // sBAiA : (B)-a->(A)<-a-(A)
    LabeledGraph sBAiA = new LabeledGraph();
    expectedSubgraphs.add(sBAiA);
    LabeledVertex sBAiAvA = sBAiA.newVertex("A");
    sBAiA.newEdge(sBAiA.newVertex("B"), "a", sBAiAvA);
    sBAiA.newEdge(sBAiA.newVertex("A"), "a", sBAiAvA );

    // sAA : (A)-a->(A)
    LabeledGraph sAA = new LabeledGraph();
    expectedSubgraphs.add(sAA);
    sAA.newEdge(sAA.newVertex("A"),"a",sAA.newVertex("A"));

    // sFull
    expectedSubgraphs.add(gA);


    // FSM
    MTGSpan gSpan = new MTGSpan();

    Map<LabeledGraph,Float> frequentSubgraphs =
      gSpan.frequentSubgraphs(graphs, 0.7f);

    checkAssertions(frequentSubgraphs, expectedSubgraphs);
  }

  private void checkAssertions(Map<LabeledGraph, Float> frequentSubgraphs,
    Set<LabeledGraph> expectedSubgraphs) {
    assertThat(frequentSubgraphs.size(), Is.is(expectedSubgraphs.size()));

    // all frequent subgraphs are distinct
    for(LabeledGraph leftGraph : frequentSubgraphs.keySet()) {
      for(LabeledGraph rightGraph : frequentSubgraphs.keySet()) {
        if(leftGraph != rightGraph) {
          assertFalse(leftGraph.isIsomorphicTo(rightGraph));
        }
      }
    }

    // every frequent subgraph was expected
    Map<LabeledGraph,LabeledGraph> graphMap = new HashMap<>();

    for(LabeledGraph expectedSubgraph : expectedSubgraphs) {
      for(LabeledGraph frequentSubgraph : frequentSubgraphs.keySet()) {
        if(!graphMap.containsKey(frequentSubgraph)
          && expectedSubgraph.isIsomorphicTo(frequentSubgraph)) {
          graphMap.put(frequentSubgraph,expectedSubgraph);
        }
      }
    }

    for(LabeledGraph expectedSubgraph : expectedSubgraphs) {
      if(!graphMap.containsValue(expectedSubgraph)){
        System.out.println("expectation not found : " + expectedSubgraph);
        System.out.println(expectedSubgraph.getAdjacencyMatrixCode());
      }
    }
    for(LabeledGraph frequentSubgraph : frequentSubgraphs.keySet()) {
      if(!graphMap.containsKey(frequentSubgraph)){
        System.out.println("not expected : " + frequentSubgraph);
        System.out.println(frequentSubgraph.getAdjacencyMatrixCode());
      }
    }

    assertThat(graphMap.size(), Is.is(expectedSubgraphs.size()));
  }
}