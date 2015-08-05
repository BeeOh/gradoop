package org.biiig.fsm.gspan.multithreaded;

import org.biiig.fsm.gspan.DfsCode;
import org.biiig.fsm.gspan.DfsEdge;
import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by peet on 29.07.15.
 */
public class MTSpanFrequentSubgraphDecoder extends MTGSpanAbstractRunnable {
  public MTSpanFrequentSubgraphDecoder(MTGSpanWorker worker) {
    super(worker);
  }

  @Override
  public void run() {
    Map<DfsCode,Integer> dfsCodeSupports = worker.getMaster()
      .getPartitionedFrequentDfsCodeSupports().get(worker.getParition());

    Map<Integer,String> edgeLabelDictionary = worker
      .getEdgeLabelDictionary().inverse();

    Map<Integer,String> vertexLabelDictionary = worker
      .getVertexLabelDictionary().inverse();

    Integer numberOfGraphs = worker.getMaster().getNumberOfGraphs();

    for(Map.Entry<DfsCode,Integer> dfsCodeSupport : dfsCodeSupports.entrySet()) {

      DfsCode dfsCode = dfsCodeSupport.getKey();
      Integer support = dfsCodeSupport.getValue();

      LabeledGraph graph = new LabeledGraph();
      worker.getFrequentSubgraphs().put(graph, Float.valueOf(support) /
        numberOfGraphs);

      Map<Integer,LabeledVertex> vertexMap = new HashMap<>();

      for(DfsEdge dfsEdge : dfsCode.getEdges()) {

        String edgeLabel = edgeLabelDictionary.get(dfsEdge.getEdgeLabel());

        Integer sourcePosition;
        Integer targetPosition;

        if(dfsEdge.isOutgoing()) {
          sourcePosition = dfsEdge.getFromPosition();
          targetPosition = dfsEdge.getToPosition();
        } else {
          sourcePosition = dfsEdge.getToPosition();
          targetPosition = dfsEdge.getFromPosition();
        }

        LabeledVertex sourceVertex = vertexMap.get(sourcePosition);

        if(sourceVertex == null) {
          String sourceLabel;

          if(dfsEdge.isOutgoing()) {
            sourceLabel = vertexLabelDictionary.get(dfsEdge
              .getFromLabel());
          } else {
            sourceLabel = vertexLabelDictionary.get(dfsEdge
              .getToLabel());
          }

          sourceVertex = graph.newVertex(sourceLabel);
          vertexMap.put(sourcePosition,sourceVertex);
        }

        LabeledVertex targetVertex = vertexMap.get(targetPosition);

        if(targetVertex == null) {
          String targetLabel;

          if(dfsEdge.isOutgoing()) {
            targetLabel = vertexLabelDictionary.get(dfsEdge
              .getToLabel());
          } else {
            targetLabel = vertexLabelDictionary.get(dfsEdge
              .getFromLabel());
          }

          targetVertex = graph.newVertex(targetLabel);
          vertexMap.put(targetPosition,targetVertex);
        }

        graph.newEdge(sourceVertex,edgeLabel,targetVertex);
      }
    }
  }
}
