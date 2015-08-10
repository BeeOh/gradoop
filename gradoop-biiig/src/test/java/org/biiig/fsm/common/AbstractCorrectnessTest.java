package org.biiig.fsm.common;


import org.hamcrest.core.Is;

import java.util.Collection;
import java.util.HashMap;
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

  protected FSMTestData getSimpleTestData(){
    FSMTestData testData =
      new FSMTestData("without cycles, loops and parallel edges", 0.7f);

    // search space

    // (Z)-z->(X)<-a-(A)
    //           <-a-(A)

    LabeledGraph gA = testData.newSearchGraph();

    LabeledVertex gAvX = gA.newVertex("X");
    LabeledVertex gAvZ = gA.newVertex("Z");
    LabeledVertex gAvA1 = gA.newVertex("A");
    LabeledVertex gAvA2 = gA.newVertex("A");

    gA.newEdge(gAvZ,"z",gAvX);
    gA.newEdge(gAvA1,"a",gAvX);
    gA.newEdge(gAvA2,"a",gAvX);


    // (Z)-z->(X)<-y-(Y)
    //           <-y-(Y)

    LabeledGraph gB = testData.newSearchGraph();

    LabeledVertex gBvX = gB.newVertex("X");
    LabeledVertex gBvZ = gB.newVertex("Z");
    LabeledVertex gBvY1 = gB.newVertex("Y");
    LabeledVertex gBvY2 = gB.newVertex("Y");

    gB.newEdge(gBvZ,"z",gBvX);
    gB.newEdge(gBvY1,"y",gBvX);
    gB.newEdge(gBvY2,"y",gBvX);

    // (Y)-y->(X)<-z-(Z)
    //           <-a-(A)

    LabeledGraph gC = testData.newSearchGraph();

    LabeledVertex gCvX = gC.newVertex("X");
    LabeledVertex gCvY = gC.newVertex("Y");
    LabeledVertex gCvZ = gC.newVertex("Z");
    LabeledVertex gCvA = gC.newVertex("A");

    gC.newEdge(gCvY,"y",gCvX);
    gC.newEdge(gCvZ,"z",gCvX);
    gC.newEdge(gCvA,"a",gCvX);

    // expected result

    // sA : (A)-a->(X)
    LabeledGraph sA = testData.newExpectation();
    sA.newEdge(sA.newVertex("A"),"a",sA.newVertex("X"));

    // sB : (Y)-y->(X)
    LabeledGraph sB = testData.newExpectation();
    sB.newEdge(sB.newVertex("Y"),"y",sB.newVertex("X"));

    // sC : (Z)-z->(X)
    LabeledGraph sC = testData.newExpectation();
    sC.newEdge(sC.newVertex("Z"),"z",sC.newVertex("X"));

    // sD : (A)-a->(X)<-z-(Z)
    LabeledGraph sD = testData.newExpectation();
    LabeledVertex sDvX = sD.newVertex("X");
    sD.newEdge(sD.newVertex("A"),"a",sDvX);
    sD.newEdge(sD.newVertex("Z"),"z",sDvX);

    // sE : (Y)-y->(X)<-z-(Z)
    LabeledGraph sE = testData.newExpectation();
    LabeledVertex sEvX = sE.newVertex("X");
    sE.newEdge(sE.newVertex("Y"),"y",sEvX);
    sE.newEdge(sE.newVertex("Z"),"z",sEvX);

    return testData;
  }

  protected FSMTestData getTestDataWithParallelEdges(){
    FSMTestData testData = new FSMTestData("with parallel edges", 1.0f);

    // search space

    // gA :      -a->   -b->(A)
    //        (A)    (A)
    //           -a->   -b->(B)

    LabeledGraph gA = testData.newSearchGraph();

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

    LabeledGraph gB = testData.newSearchGraph();

    LabeledVertex gBv1 = gB.newVertex("A");
    LabeledVertex gBv2 = gB.newVertex("A");
    LabeledVertex gBv3 = gB.newVertex("A");
    LabeledVertex gBv4 = gB.newVertex("B");

    gB.newEdge(gBv1,"b",gBv2);
    gB.newEdge(gBv2,"a",gBv3);
    gB.newEdge(gBv2,"a",gBv3);
    gB.newEdge(gBv3,"b",gBv4);

    // expected result

    // sAaA : (A)-a->(A)

    LabeledGraph sAaA = testData.newExpectation();

    sAaA.newEdge(sAaA.newVertex("A"),"a",sAaA.newVertex("A"));

    // sAbA : (A)-b->(A)

    LabeledGraph sAbA = testData.newExpectation();

    sAbA.newEdge(sAbA.newVertex("A"),"b",sAbA.newVertex("A"));

    // sAbB : (A)-b->(B)

    LabeledGraph sAbB = testData.newExpectation();

    sAbB.newEdge(sAbB.newVertex("A"),"b",sAbB.newVertex("B"));

    // sAbA : (A)-a->(A)-b->(B)

    LabeledGraph sAbAbB = testData.newExpectation();

    LabeledVertex sAbAbBvA2 = sAbAbB.newVertex("A");
    sAbAbB.newEdge(sAbAbB.newVertex("A"),"a",sAbAbBvA2);
    sAbAbB.newEdge(sAbAbBvA2,"b",sAbAbB.newVertex("B"));

    // sA2aA :   -a->
    //        (A)    (A)
    //           -a->

    LabeledGraph sA2aA = testData.newExpectation();

    LabeledVertex sA2aAvA1 = sA2aA.newVertex("A");
    LabeledVertex sA2aAvA2 = sA2aA.newVertex("A");

    sA2aA.newEdge(sA2aAvA1,"a",sA2aAvA2);
    sA2aA.newEdge(sA2aAvA1,"a",sA2aAvA2);

    // sA2aAbB :     -a->
    //            (A)    (A)-b->(B)
    //               -a->

    LabeledGraph sA2aAbB = testData.newExpectation();

    LabeledVertex sA2aAbBvA1 = sA2aAbB.newVertex("A");
    LabeledVertex sA2aAbBvA2 = sA2aAbB.newVertex("A");

    sA2aAbB.newEdge(sA2aAbBvA1,"a",sA2aAbBvA2);
    sA2aAbB.newEdge(sA2aAbBvA1,"a",sA2aAbBvA2);
    sA2aAbB.newEdge(sA2aAbBvA2,"b",sA2aAbB.newVertex("B"));

    return testData;
  }

  protected FSMTestData getTestDataWithCycles(){
    FSMTestData testData = new FSMTestData("with cycles", 0.7f);

    // search space

    //   <-a-(B)-a->
    // (A)  <-a-   (A)

    LabeledGraph gA = testData.newSearchGraph();

    LabeledVertex gAvA1 = gA.newVertex("A");
    LabeledVertex gAvB = gA.newVertex("B");
    LabeledVertex gAvA2 = gA.newVertex("A");
    gA.newEdge(gAvB,"a",gAvA1);
    gA.newEdge(gAvB,"a",gAvA2);
    gA.newEdge(gAvA2,"a",gAvA1);

    //   -a->(A)<-a-
    // (A)  <-a-   (B)

    LabeledGraph gB = testData.newSearchGraph();

    LabeledVertex gBvA1 = gB.newVertex("A");
    LabeledVertex gBvA2 = gB.newVertex("A");
    LabeledVertex gBvB = gB.newVertex("B");
    gB.newEdge(gBvA1,"a",gBvA2);
    gB.newEdge(gBvB,"a",gBvA1);
    gB.newEdge(gBvB,"a",gBvA2);

    // expected result

    // sBA : (B)-a->(A)
    LabeledGraph sBA = testData.newExpectation();

    sBA.newEdge(sBA.newVertex("B"),"a",sBA.newVertex("A"));

    // sABA : (A)<-a-(B)-a->(A)
    LabeledGraph sABA = testData.newExpectation();

    LabeledVertex sABAvB = sABA.newVertex("B");
    sABA.newEdge(sABAvB, "a", sABA.newVertex("A"));
    sABA.newEdge(sABAvB, "a", sABA.newVertex("A"));

    // sBAoA : (B)-a->(A)-a->(A)
    LabeledGraph sBAoA = testData.newExpectation();

    LabeledVertex sBAoAvA = sBAoA.newVertex("A");
    sBAoA.newEdge(sBAoA.newVertex("B"), "a", sBAoAvA);
    sBAoA.newEdge(sBAoAvA, "a", sBAoA.newVertex("A"));

    // sBAiA : (B)-a->(A)<-a-(A)
    LabeledGraph sBAiA = testData.newExpectation();

    LabeledVertex sBAiAvA = sBAiA.newVertex("A");
    sBAiA.newEdge(sBAiA.newVertex("B"), "a", sBAiAvA);
    sBAiA.newEdge(sBAiA.newVertex("A"), "a", sBAiAvA );

    // sAA : (A)-a->(A)
    LabeledGraph sAA = testData.newExpectation();

    sAA.newEdge(sAA.newVertex("A"),"a",sAA.newVertex("A"));

    // sFull
    testData.getExpectedResult().add(gA);

    return testData;
  }

  protected FSMTestData getTestDataWithLoops(){
    FSMTestData testData = new FSMTestData("with loops", 0.5f);

    // search space

    // gA:  ->        <-
    //     a-(A)-d->(B)-b

    LabeledGraph gA = testData.newSearchGraph();

    LabeledVertex gAvA = gA.newVertex("A");
    LabeledVertex gAvB = gA.newVertex("B");

    gA.newEdge(gAvA,"a",gAvA);
    gA.newEdge(gAvA,"b",gAvB);
    gA.newEdge(gAvB,"b",gAvB);

    // gB:  ->        <-
    //     a-(A)-b->(D)-d

    LabeledGraph gB = testData.newSearchGraph();

    LabeledVertex gBvA = gB.newVertex("A");
    LabeledVertex gBvD = gB.newVertex("D");

    gB.newEdge(gBvA,"a",gBvA);
    gB.newEdge(gBvA,"b",gBvD);
    gB.newEdge(gBvD,"d",gBvD);

    // gC:  ->        <-
    //     c-(A)-b->(D)-d

    LabeledGraph gC = testData.newSearchGraph();

    LabeledVertex gCvA = gC.newVertex("A");
    LabeledVertex gCvD = gC.newVertex("D");

    gC.newEdge(gCvA,"c",gCvA);
    gC.newEdge(gCvA,"b",gCvD);
    gC.newEdge(gCvD,"d",gCvD);

    // gD: (A)-c->(A)-a->(B)<-b-(B)

    LabeledGraph gD = testData.newSearchGraph();

    LabeledVertex gDvA1 = gD.newVertex("A");
    LabeledVertex gDvA2 = gD.newVertex("A");

    LabeledVertex gDvB1 = gD.newVertex("B");
    LabeledVertex gDvB2 = gD.newVertex("B");

    gD.newEdge(gDvA1,"c",gDvA2);
    gD.newEdge(gDvA2,"a",gDvB1);
    gD.newEdge(gDvB2,"b",gDvB1);

    // expected result

    // sLA: ->
    //     a-(A)

    LabeledGraph sLA = testData.newExpectation();

    LabeledVertex sLAvA = sLA.newVertex("A");

    sLA.newEdge(sLAvA,"a",sLAvA);

    // sLD: ->
    //     d-(D)

    LabeledGraph sLD = testData.newExpectation();

    LabeledVertex sLDvD = sLD.newVertex("D");

    sLD.newEdge(sLDvD,"d",sLDvD);

    // SAbD:  (A)-b->(D)

    LabeledGraph SAbD = testData.newExpectation();

    LabeledVertex sAbDvA = SAbD.newVertex("A");
    LabeledVertex sAbDvD = SAbD.newVertex("D");

    SAbD.newEdge(sAbDvA,"b",sAbDvD);

    // sAbLD:          <-
    //        (A)-b->(D)-d

    LabeledGraph sAbLD = testData.newExpectation();

    LabeledVertex sAbLDvA = sAbLD.newVertex("A");
    LabeledVertex sAbLDvD = sAbLD.newVertex("D");

    sAbLD.newEdge(sAbLDvA,"b",sAbLDvD);
    sAbLD.newEdge(sAbLDvD,"d",sAbLDvD);

    return testData;
  }
}
