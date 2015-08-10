package org.biiig.fsm.gspan.singlenode;

import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.DfsEdge;
import org.biiig.fsm.common.LabeledGraph;
import org.biiig.fsm.common.LabeledVertex;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by p3et on 29.07.15.
 *
 * create labelled graphs from a subset of frequent DFS codes allocated by
 * the master to the workers partition
 */
public class FrequentSubgraphDecoder extends AbstractRunnable {
  /**
   * constructor
   * @param worker initializing worker
   */
  public FrequentSubgraphDecoder(GSpanWorker worker) {
    super(worker);
  }

  /**
   * read allocated DFS codes and create labelled graphs
   */
  @Override
  public void run() {
    // get allocated frequent DFS codes from worker
    Map<DfsCode, Integer> codeSupports = worker.getMaster()
      .getPartitionedFrequentDfsCodeSupports().get(worker.getPartition());

    // flip label dictionaries for decoding
    Map<Integer, String> edgeLabelDictionary = worker
      .getEdgeLabelDictionary().inverse();

    Map<Integer, String> vertexLabelDictionary = worker
      .getVertexLabelDictionary().inverse();

    // get total number of graphs
    Integer numberOfGraphs = worker.getMaster().getNumberOfGraphs();

    // for each DFS code
    for (Map.Entry<DfsCode, Integer> codeSupport : codeSupports.entrySet()) {

      DfsCode code = codeSupport.getKey();
      Integer support = codeSupport.getValue();

      // create graph and to local set of frequent subgraphs including frequency
      LabeledGraph graph = new LabeledGraph();
      worker.getFrequentSubgraphs().put(
        graph, Float.valueOf(support) / numberOfGraphs);

      Map<Integer, LabeledVertex> vertexMap = new HashMap<>();

      // for each edge
      for (DfsEdge dfsEdge : code.getDfsEdges()) {

        // determine source and target vertex
        Integer sourcePosition;
        Integer targetPosition;

        if (dfsEdge.isInDirection()) {
          sourcePosition = dfsEdge.getFromPosition();
          targetPosition = dfsEdge.getToPosition();
        } else {
          sourcePosition = dfsEdge.getToPosition();
          targetPosition = dfsEdge.getFromPosition();
        }

        // decode source vertex label if not already mapped
        LabeledVertex sourceVertex = vertexMap.get(sourcePosition);

        if (sourceVertex == null) {
          String sourceLabel;

          if (dfsEdge.isInDirection()) {
            sourceLabel = vertexLabelDictionary.get(dfsEdge.getFromLabel());
          } else {
            sourceLabel = vertexLabelDictionary.get(dfsEdge.getToLabel());
          }

          sourceVertex = graph.newVertex(sourceLabel);
          vertexMap.put(sourcePosition, sourceVertex);
        }

        // decode target vertex label if not already mapped
        LabeledVertex targetVertex = vertexMap.get(targetPosition);

        if (targetVertex == null) {
          String targetLabel;

          if (dfsEdge.isInDirection()) {
            targetLabel = vertexLabelDictionary.get(dfsEdge.getToLabel());
          } else {
            targetLabel = vertexLabelDictionary.get(dfsEdge.getFromLabel());
          }

          targetVertex = graph.newVertex(targetLabel);
          vertexMap.put(targetPosition, targetVertex);
        }

        // decode edge label
        String edgeLabel = edgeLabelDictionary.get(dfsEdge.getEdgeLabel());

        // add create edge to labelled graph
        graph.newEdge(sourceVertex, edgeLabel, targetVertex);
      }
    }
  }
}
