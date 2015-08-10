package org.biiig.datagen.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.biiig.fsm.common.LabeledGraph;
import org.biiig.fsm.common.LabeledVertex;

/**
 * Created by peet on 19.06.15.
 */
public class FlinkFPCGeneratorWorker implements
  FlatMapFunction<FlinkFPCGeneratorJob, LabeledGraph> {
  @Override
  public void flatMap(FlinkFPCGeneratorJob job,
    Collector<LabeledGraph> graphs) throws Exception {

    int support = job.getGraphType() * 10;

    for (int clone = 1; clone <= job.getCollectionScaleFactor(); clone++) {
      LabeledGraph graph = new LabeledGraph();

      String edgeLabel = "" + support + "." + clone;
      //String workerID = "@" + String.valueOf(Thread.currentThread().getId()) +
      //  InetAddress.getLocalHost().getHostName();

      //graph.setLabel(edgeLabel + workerID);

      LabeledVertex vS = graph.newVertex("S");

      for (int i = 1; i <= job.getGraphScaleFactor(); i++) {
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
        graph.newEdge(vA, support >= 100 ? "100" : edgeLabel, vB);

        // 90% 2-edge graph
        graph.newEdge(vB, support >= 90 ? "90" : edgeLabel, vC);

        // 80% parallel edges
        graph.newEdge(vB, support >= 80 ? "90" : edgeLabel, vC);

        // 70% mirrored edges

        graph.newEdge(vD, support >= 70 ? "70" : edgeLabel, vE1);
        graph.newEdge(vD, support >= 70 ? "70" : edgeLabel, vE2);

        // 60% mirrored path
        graph.newEdge(vF, support >= 60 ? "60" : edgeLabel, vE1);
        graph.newEdge(vF, support >= 60 ? "60" : edgeLabel, vE2);

        // 50% cycle
        graph.newEdge(vG, support >= 50 ? "50" : edgeLabel, vH);
        graph.newEdge(vH, support >= 50 ? "50" : edgeLabel, vI);
        graph.newEdge(vI, support >= 50 ? "50" : edgeLabel, vG);

        // 40% loop
        graph.newEdge(vK, support >= 40 ? "40" : edgeLabel, vK);

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

      graphs.collect(graph);
    }
  }
}
