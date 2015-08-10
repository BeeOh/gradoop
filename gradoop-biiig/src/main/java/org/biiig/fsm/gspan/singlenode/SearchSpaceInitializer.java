package org.biiig.fsm.gspan.singlenode;

import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.DfsCodeMapper;
import org.biiig.fsm.gspan.common.DfsEdge;
import org.biiig.fsm.gspan.common.GSpanEdge;
import org.biiig.fsm.gspan.common.GSpanGraph;
import org.biiig.fsm.gspan.common.GSpanVertex;
import org.biiig.fsm.common.LabeledEdge;
import org.biiig.fsm.common.LabeledGraph;
import org.biiig.fsm.common.LabeledVertex;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by peet on 17.07.15.
 */
public class SearchSpaceInitializer extends AbstractDfsEncoder {
  /**
   * constructor
   * @param worker worker initializing the miner
   */
  public SearchSpaceInitializer(GSpanWorker worker) {
    super(worker);
  }
  /**
   * generates for each graph
   * - GSpan Graph using label dictionaries
   * - 1-edge DFS codes for all edges
   * - mappers between 1-edge DFS codes and instantiating edges
   */
  @Override
  public void run() {
    // ensure an empty search space
    worker.getDfsCodeSupporterMappersMap().clear();

    // for each graph
    for (LabeledGraph graph : worker.getGraphs()) {
      GSpanGraph supporter = new GSpanGraph();
      Map<LabeledVertex, GSpanVertex> vertexMap = new HashMap<>();

      // for each edge
      for (LabeledEdge edge : graph.getEdges()) {

        // get edge label translation
        Integer gsEdgeLabel = worker.getEdgeLabelDictionary().get(edge
          .getLabel());

        // only continue if edge label is in dictionary (i.e., is frequent)
        if (gsEdgeLabel != null) {

          LabeledVertex sourceVertex = edge.getSourceVertex();
          Integer gsSourceLabel = null;

          // get GSpan vertex mapped to source vertex
          // (only vertices with frequent labels can be mapped)
          GSpanVertex gsSourceVertex = vertexMap.get(sourceVertex);

          // if source vertex is not mapped
          if (gsSourceVertex == null) {
            // try to translate source vertex label
            gsSourceLabel =
              worker.getVertexLabelDictionary().get(sourceVertex.getLabel());
          }

          // only continue if source vertex is already mapped
          // or source vertex label is in dictionary (i.e., is frequent)
          if (gsSourceVertex != null || gsSourceLabel != null) {

            LabeledVertex targetVertex = edge.getTargetVertex();
            Integer gsTargetLabel = null;

            // get GSpan vertex mapped to target vertex
            // (only vertices with frequent labels can be mapped)
            GSpanVertex gsTargetVertex = vertexMap.get(targetVertex);

            // if source vertex is not mapped
            if (gsTargetVertex == null) {
              // try to translate target vertex label
              gsTargetLabel =
                worker.getVertexLabelDictionary().get(targetVertex.getLabel());
            }

            // only continue if target vertex is already mapped
            // or source vertex label is in dictionary (i.e., is frequent)
            if (gsTargetVertex != null || gsTargetLabel != null) {

              // create GSpan source vertex if not already mapped
              if (gsSourceVertex == null) {
                gsSourceVertex = supporter.newVertex(gsSourceLabel);
                vertexMap.put(sourceVertex, gsSourceVertex);
              }

              // create GSpan target vertex if not already mapped
              if (sourceVertex == targetVertex) {
                gsTargetVertex = gsSourceVertex;
              } else if (gsTargetVertex == null) {
                gsTargetVertex = supporter.newVertex(gsTargetLabel);
                vertexMap.put(targetVertex, gsTargetVertex);
              }

              // create GSpan edge
              GSpanEdge gsEdge = supporter.newEdge(gsSourceVertex, gsEdgeLabel,
                gsTargetVertex);

              // create 1-edge DFS code and mapper
              DfsCode code = new DfsCode();
              DfsCodeMapper mapper = new DfsCodeMapper(supporter);

              DfsEdge dfsEdge;

              // self-loop
              if (gsSourceVertex == gsTargetVertex) {
                dfsEdge = new DfsEdge(0, gsSourceVertex.getLabel(), true,
                  gsEdge.getLabel(), 0, gsSourceVertex.getLabel());
                mapper.map(gsSourceVertex);
                // from source to target vertex
              } else if (gsSourceVertex.compareTo(gsTargetVertex) <= 0) {
                dfsEdge = new DfsEdge(0, gsSourceVertex.getLabel(), true,
                  gsEdge.getLabel(), 1, gsTargetVertex.getLabel());
                mapper.map(gsSourceVertex);
                mapper.map(gsTargetVertex);
                // from target to source vertex
              } else {
                dfsEdge =
                  new DfsEdge(0, gsTargetVertex.getLabel(), false,
                    gsEdge.getLabel(), 1, gsSourceVertex.getLabel());
                mapper.map(gsTargetVertex);
                mapper.map(gsSourceVertex);
              }

              // add DFS edge to empty code (add root edge) and GSpan edge to
              // mapper
              code.add(dfsEdge);
              mapper.map(gsEdge);

              // report 1-edge DFS code and corresponding mapper
              addDfsCodeSupporterMapper(code, supporter, mapper);
            }
          }
        }
      }
    }
  }
}

