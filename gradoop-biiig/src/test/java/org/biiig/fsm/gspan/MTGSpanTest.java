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

  @Test
  public void testParallelEdges() {
    System.out.println("\n*** Test GSpan with parallel edges ***");

    // search space
    List<LabeledGraph> graphs = new ArrayList<>();

    // gA :      -a->   -b->(A)
    //        (A)    (A)
    //           -a->   -b->(B)

    LabeledGraph gA = new LabeledGraph();
    graphs.add(gA);

    LabeledVertex gAv1 = gA.newVertex("A");
    LabeledVertex gAv2 = gA.newVertex("A");
    LabeledVertex gAv3 = gA.newVertex("A");
    LabeledVertex gAv4 = gA.newVertex("B");


    gA.newEdge(gAv1,"a",gAv2);
    gA.newEdge(gAv1,"a",gAv2);
    gA.newEdge(gAv2,"b",gAv3);
    gA.newEdge(gAv2,"b",gAv4);


    // gB :             -a->
    //        (A)-b->(A)    (A)-b->(B)
    //                  -a->

    LabeledGraph gB = new LabeledGraph();
    graphs.add(gB);

    LabeledVertex gBv1 = gB.newVertex("A");
    LabeledVertex gBv2 = gB.newVertex("A");
    LabeledVertex gBv3 = gB.newVertex("A");
    LabeledVertex gBv4 = gB.newVertex("B");

    gB.newEdge(gBv1,"b",gBv2);
    gB.newEdge(gBv2,"a",gBv3);
    gB.newEdge(gBv2,"a",gBv3);
    gB.newEdge(gBv3,"b",gBv4);

    // expected result
    Set<LabeledGraph> expectedSubgraphs = new HashSet<>();

    // sAaA : (A)-a->(A)

    LabeledGraph sAaA = new LabeledGraph();
    expectedSubgraphs.add(sAaA);
    sAaA.newEdge(sAaA.newVertex("A"),"a",sAaA.newVertex("A"));

    // sAbA : (A)-b->(A)

    LabeledGraph sAbA = new LabeledGraph();
    expectedSubgraphs.add(sAbA);
    sAbA.newEdge(sAbA.newVertex("A"),"b",sAbA.newVertex("A"));

    // sAbB : (A)-b->(B)

    LabeledGraph sAbB = new LabeledGraph();
    expectedSubgraphs.add(sAbB);
    sAbB.newEdge(sAbB.newVertex("A"),"b",sAbB.newVertex("B"));

    // sAbA : (A)-a->(A)-b->(B)

    LabeledGraph sAbAbB = new LabeledGraph();
    expectedSubgraphs.add(sAbAbB);
    LabeledVertex sAbAbBvA2 = sAbAbB.newVertex("A");
    sAbAbB.newEdge(sAbAbB.newVertex("A"),"a",sAbAbBvA2);
    sAbAbB.newEdge(sAbAbBvA2,"b",sAbAbB.newVertex("B"));

    // sA2aA :   -a->
    //        (A)    (A)
    //           -a->

    LabeledGraph sA2aA = new LabeledGraph();
    expectedSubgraphs.add(sA2aA);
    LabeledVertex sA2aAvA1 = sA2aA.newVertex("A");
    LabeledVertex sA2aAvA2 = sA2aA.newVertex("A");

    sA2aA.newEdge(sA2aAvA1,"a",sA2aAvA2);
    sA2aA.newEdge(sA2aAvA1,"a",sA2aAvA2);

    // sA2aAbB :     -a->
    //            (A)    (A)-b->(B)
    //               -a->

    LabeledGraph sA2aAbB = new LabeledGraph();
    expectedSubgraphs.add(sA2aAbB);
    LabeledVertex sA2aAbBvA1 = sA2aAbB.newVertex("A");
    LabeledVertex sA2aAbBvA2 = sA2aAbB.newVertex("A");

    sA2aAbB.newEdge(sA2aAbBvA1,"a",sA2aAbBvA2);
    sA2aAbB.newEdge(sA2aAbBvA1,"a",sA2aAbBvA2);
    sA2aAbB.newEdge(sA2aAbBvA2,"b",sA2aAbB.newVertex("B"));

    // FSM
    MTGSpan gSpan = new MTGSpan();

    Map<LabeledGraph,Float> frequentSubgraphs =
      gSpan.frequentSubgraphs(graphs, 1.0f);

    checkAssertions(frequentSubgraphs, expectedSubgraphs);


  }

  @Test
  public void testLoops(){

    System.out.println("\n*** Test GSpan with self-loops ***");

    // search space
    List<LabeledGraph> graphs = new ArrayList<>();

    // gA:  ->        <-
    //     a-(A)-d->(B)-b

    LabeledGraph gA = new LabeledGraph();
    graphs.add(gA);

    LabeledVertex gAvA = gA.newVertex("A");
    LabeledVertex gAvB = gA.newVertex("B");

    gA.newEdge(gAvA,"a",gAvA);
    gA.newEdge(gAvA,"b",gAvB);
    gA.newEdge(gAvB,"b",gAvB);

    // gB:  ->        <-
    //     a-(A)-b->(D)-d

    LabeledGraph gB = new LabeledGraph();
    graphs.add(gB);

    LabeledVertex gBvA = gB.newVertex("A");
    LabeledVertex gBvD = gB.newVertex("D");

    gB.newEdge(gBvA,"a",gBvA);
    gB.newEdge(gBvA,"b",gBvD);
    gB.newEdge(gBvD,"d",gBvD);

    // gC:  ->        <-
    //     c-(A)-b->(D)-d

    LabeledGraph gC = new LabeledGraph();
    graphs.add(gC);

    LabeledVertex gCvA = gC.newVertex("A");
    LabeledVertex gCvD = gC.newVertex("D");

    gC.newEdge(gCvA,"c",gCvA);
    gC.newEdge(gCvA,"b",gCvD);
    gC.newEdge(gCvD,"d",gCvD);

    // gD: (A)-c->(A)-a->(B)<-b-(B)

    LabeledGraph gD = new LabeledGraph();
    graphs.add(gD);

    LabeledVertex gDvA1 = gD.newVertex("A");
    LabeledVertex gDvA2 = gD.newVertex("A");

    LabeledVertex gDvB1 = gD.newVertex("B");
    LabeledVertex gDvB2 = gD.newVertex("B");

    gD.newEdge(gDvA1,"c",gDvA2);
    gD.newEdge(gDvA2,"a",gDvB1);
    gD.newEdge(gDvB2,"b",gDvB1);

    // expected result
    Set<LabeledGraph> expectedSubgraphs = new HashSet<>();

    // sLA: ->
    //     a-(A)

    LabeledGraph sLA = new LabeledGraph();
    expectedSubgraphs.add(sLA);

    LabeledVertex sLAvA = sLA.newVertex("A");

    sLA.newEdge(sLAvA,"a",sLAvA);

    // sLD: ->
    //     d-(D)

    LabeledGraph sLD = new LabeledGraph();
    expectedSubgraphs.add(sLD);

    LabeledVertex sLDvD = sLD.newVertex("D");

    sLD.newEdge(sLDvD,"d",sLDvD);

    // SAbD:  (A)-b->(D)

    LabeledGraph SAbD = new LabeledGraph();
    expectedSubgraphs.add(SAbD);

    LabeledVertex sAbDvA = SAbD.newVertex("A");
    LabeledVertex sAbDvD = SAbD.newVertex("D");

    SAbD.newEdge(sAbDvA,"b",sAbDvD);

    // sAbLD:          <-
    //        (A)-b->(D)-d

    LabeledGraph sAbLD = new LabeledGraph();
    expectedSubgraphs.add(sAbLD);

    LabeledVertex sAbLDvA = sAbLD.newVertex("A");
    LabeledVertex sAbLDvD = sAbLD.newVertex("D");

    sAbLD.newEdge(sAbLDvA,"b",sAbLDvD);
    sAbLD.newEdge(sAbLDvD,"d",sAbLDvD);

    // FSM
    MTGSpan gSpan = new MTGSpan();

    Map<LabeledGraph,Float> frequentSubgraphs =
      gSpan.frequentSubgraphs(graphs, 0.5f);

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
        System.out.println("not found : " + expectedSubgraph);
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