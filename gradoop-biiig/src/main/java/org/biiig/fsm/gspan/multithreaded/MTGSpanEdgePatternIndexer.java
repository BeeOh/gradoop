package org.biiig.fsm.gspan.multithreaded;

import com.google.common.collect.Sets;
import org.biiig.fsm.gspan.GSpanVertex;
import org.biiig.fsm.gspan.common.EdgePattern;
import org.biiig.fsm.gspan.GSpanGraph;
import org.biiig.model.LabeledEdge;
import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;
import org.gradoop.model.Edge;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

/**
 * Created by peet on 15.07.15.
 */
public class MTGSpanEdgePatternIndexer extends MTGSSpanRunnable  {
  /**
   * constructor
   * @param worker calling worker
   */
  public MTGSpanEdgePatternIndexer(MTGSpanWorker worker) {
    super(worker);
  }

  /**
   * indexes edge pattern occurrences
   */
  @Override
  public void run() {

    // for each local graph
    for(LabeledGraph graph : worker.getGraphs()) {

      for(LabeledEdge edge : graph.getEdges()) {
        if(worker.getEdgeLabelDictionary().containsKey(edge.getLabel())
          && worker.getVertexLabelDictionary()
          .containsKey(edge.getSourceVertex().getLabel())
          && worker.getVertexLabelDictionary()
          .containsKey(edge.getTargetVertex().getLabel())) {

          EdgePattern edgePattern = new EdgePattern(edge);

          Map<LabeledGraph,Set<LabeledEdge>> edgePatternIndexEntry = worker
            .getEdgePatternIndex().get(edgePattern);

          if(edgePatternIndexEntry == null) {
            edgePatternIndexEntry = new HashMap<>();
            worker.getEdgePatternIndex().put(edgePattern,edgePatternIndexEntry);
          }

          Set<LabeledEdge> edgePatternInstances = edgePatternIndexEntry.get
            (graph);

          if(edgePatternInstances == null) {
            edgePatternInstances = new HashSet<>();
            edgePatternIndexEntry.put(graph,edgePatternInstances);
          }

          edgePatternInstances.add(edge);
        }
      }
    }
  }
}
