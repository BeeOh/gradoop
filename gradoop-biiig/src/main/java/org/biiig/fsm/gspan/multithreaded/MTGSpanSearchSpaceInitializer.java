package org.biiig.fsm.gspan.multithreaded;

import org.biiig.fsm.gspan.DfsCode;
import org.biiig.fsm.gspan.DfsCodeMapper;
import org.biiig.fsm.gspan.DfsEdge;
import org.biiig.fsm.gspan.GSpanEdge;
import org.biiig.fsm.gspan.GSpanGraph;
import org.biiig.fsm.gspan.GSpanVertex;
import org.biiig.model.LabeledEdge;
import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by peet on 17.07.15.
 */
public class MTGSpanSearchSpaceInitializer extends MTGSpanRunnable {
  public MTGSpanSearchSpaceInitializer(MTGSpanWorker worker) {
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

    // for each graph
    for(LabeledGraph graph : worker.getGraphs()) {
      GSpanGraph gSpanGraph = new GSpanGraph();
      Map<LabeledVertex,GSpanVertex> vertexMap = new HashMap<>();

      // for each edge
      for(LabeledEdge edge : graph.getEdges()) {

        // get edge label translation
        Integer gsEdgeLabel = worker.getEdgeLabelDictionary().get(edge
          .getLabel());

        // only continue if edge label is in dictionary (i.e., is frequent)
        if(gsEdgeLabel != null) {
          LabeledVertex sourceVertex = edge.getSourceVertex();
          GSpanVertex gsSourceVertex = vertexMap.get(sourceVertex);

          Integer gsSourceLabel = null;

          // if source vertex is not mapped
          if(gsSourceVertex == null) {
             gsSourceLabel = worker.getVertexLabelDictionary().get
              (sourceVertex.getLabel());
          }

          // only continue if source vertex is mapped or source vertex label is
          // in dictionary (i.e., is frequent)
          if(gsSourceVertex != null || gsSourceLabel != null ) {
            LabeledVertex targetVertex = edge.getTargetVertex();
            GSpanVertex gsTargetVertex = vertexMap.get(targetVertex);

            Integer gsTargetLabel = null;

            // if source vertex is not mapped
            if(gsTargetVertex == null) {
              gsTargetLabel = worker.getVertexLabelDictionary().get
                (targetVertex.getLabel());
            }

            // only continue if target vertex is mapped or target vertex
            // label is in dictionary (i.e., is frequent)
            if(gsTargetVertex != null || gsTargetLabel != null ) {

              // create GSpan source vertex if necessary
              if(gsSourceVertex == null) {
                gsSourceVertex = gSpanGraph.newVertex(gsSourceLabel);
                vertexMap.put(sourceVertex,gsSourceVertex);
              }

              // create GSpan target vertex if necessary
              if(sourceVertex == targetVertex) {
                gsTargetVertex = gsSourceVertex;
              } else if(gsTargetVertex == null ) {
                gsTargetVertex = gSpanGraph.newVertex(gsTargetLabel);
                vertexMap.put(targetVertex,gsTargetVertex);
              }

              // create GSpan edge
              GSpanEdge gsEdge = gSpanGraph.newEdge(gsSourceVertex, gsEdgeLabel,
                gsTargetVertex);


              // create 1-edge DFS code and mapper
              DfsCode dfsCode = new DfsCode();
              DfsCodeMapper mapper = new DfsCodeMapper(gSpanGraph);

              DfsEdge dfsEdge;

              if (gsSourceVertex.compareTo(gsTargetVertex) <= 0) {
                dfsEdge = new DfsEdge(0, 1, gsSourceVertex.getLabel()
                  , true, gsEdge.getLabel(), gsTargetVertex.getLabel());
                mapper.add(gsSourceVertex);
                mapper.add(gsTargetVertex);
              } else {
                dfsEdge =
                  new DfsEdge(0, 1, gsTargetVertex.getLabel(), false, gsEdge.getLabel(),
                    gsSourceVertex.getLabel());
                mapper.add(gsTargetVertex);
                mapper.add(gsSourceVertex);
              }
              dfsCode.add(dfsEdge);
              mapper.add(gsEdge);

              // add mapper to existing DFS codes or create new entry
              Collection<DfsCodeMapper> mappers =
                worker.getDfsCodeMappersMap().get(dfsCode);

              if (mappers == null) {
                mappers = new ArrayList<>();
                worker.getDfsCodeMappersMap().put(dfsCode,mappers);
              }

              // add graph to existing DFS codes or create new entry
              Set<GSpanGraph> supporters =
                worker.getDfsCodeSupportersMap().get(dfsCode);

              if (supporters == null) {
                supporters = new HashSet<>();
                worker.getDfsCodeSupportersMap().put(dfsCode,supporters);
              }

              mappers.add(mapper);
              supporters.add(gSpanGraph);
            }
          }
        }
      }
    }
  }
}
