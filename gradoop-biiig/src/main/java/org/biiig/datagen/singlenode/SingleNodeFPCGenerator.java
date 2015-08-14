package org.biiig.datagen.singlenode;

import org.biiig.fsm.common.LabeledGraph;
import org.biiig.fsm.common.LabeledVertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by p3et on 09.07.15.
 *
 * graph collection data generator
 */
public class SingleNodeFPCGenerator {
  /**
   * starts generation
   * @param collectionScaleFactor scale factor for collection size
   * @param graphScaleFactor scale factor for graph size
   * @return graph collection
   */
  public List<LabeledGraph> generate(
    int collectionScaleFactor, int graphScaleFactor) {

    int targetCollectionSize = collectionScaleFactor * 10;

    List<LabeledGraph> graphs = new ArrayList<>();

    for (int graphID = 0; graphID < targetCollectionSize; graphID++) {

      LabeledGraph graph = new LabeledGraph();
      String edgeLabel = "G" + graphID;
      int support = 100 - (graphID % 10) * 10;

      LabeledVertex vS = graph.newVertex("S");

      for (int i = 1; i <= graphScaleFactor; i++) {
        // vertices
        LabeledVertex vA = graph.newVertex("A");
        LabeledVertex vB = graph.newVertex("B");
        LabeledVertex vC = graph.newVertex("C");
        LabeledVertex vD = graph.newVertex("D");
        LabeledVertex vE1 = graph.newVertex("E");
        LabeledVertex vE2 = graph.newVertex("E");
        LabeledVertex vF = graph.newVertex("F");
        LabeledVertex vG = graph.newVertex("G");
        LabeledVertex vH = graph.newVertex("H");
        LabeledVertex vI = graph.newVertex("I");
        LabeledVertex vK = graph.newVertex("K");

        // 100% 1-edge graph
        graph.newEdge(vA, "100", vB);

        // 90% 2-edge graph
        graph.newEdge(vB, support <= 90 ? "90" : edgeLabel, vC);

        // 80% parallel edges
        graph.newEdge(vB, support <= 80 ? "80" : edgeLabel, vC);

        // 70% mirrored edges

        graph.newEdge(vD, support <= 70 ? "70" : edgeLabel, vE1);
        graph.newEdge(vD, support <= 70 ? "70" : edgeLabel, vE2);

        // 60% mirrored path
        graph.newEdge(vF, support <= 60 ? "60" : edgeLabel, vE1);
        graph.newEdge(vF, support <= 60 ? "60" : edgeLabel, vE2);

        // 50% cycle
        graph.newEdge(vG, support <= 50 ? "50" : edgeLabel, vH);
        graph.newEdge(vH, support <= 50 ? "50" : edgeLabel, vI);
        graph.newEdge(vI, support <= 50 ? "50" : edgeLabel, vG);

        // 40% loop
        graph.newEdge(vK, support <= 40 ? "40" : edgeLabel, vK);

        // stress edges
        graph.newEdge(vS, edgeLabel, vA);
        graph.newEdge(vS, edgeLabel, vG);
        graph.newEdge(vS, edgeLabel, vD);
        graph.newEdge(vS, edgeLabel, vK);
        graph.newEdge(vC, edgeLabel, vI);
        graph.newEdge(vI, edgeLabel, vE2);
        graph.newEdge(vE1, edgeLabel, vK);
        graph.newEdge(vK, edgeLabel, vB);
      }
      graphs.add(graph);
    }
    return graphs;
  }
}
