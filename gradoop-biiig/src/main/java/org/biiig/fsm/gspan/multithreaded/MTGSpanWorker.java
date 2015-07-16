package org.biiig.fsm.gspan.multithreaded;

import com.google.common.collect.Sets;
import org.biiig.fsm.gspan.common.EdgePattern;
import org.biiig.fsm.gspan.GSpanGraph;
import org.biiig.model.LabeledEdge;
import org.biiig.model.LabeledGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by peet on 15.07.15.
 */
public class MTGSpanWorker {
  /**
   * master node
   */
  private final MTGSpanMaster master;
  /**
   * thread of this worker
   */
  private Thread thread;
  /**
   * graphs processed on this worker
   */
  private final Collection<LabeledGraph> graphs = new ArrayList<>();
  /**
   * local vertex label counts
   */
  private final Map<String,Long> vertexLabelSupports = new HashMap<>();
  /**
   * local edge label counts
   */
  private final Map<String,Long> edgeLabelSupports = new HashMap<>();
  /**
   * search space of GSpan graphs on this worker
   */
  private final Collection<GSpanGraph> searchSpace = new ArrayList<>();
  /**
   * index of graphs supporting an edge pattern
   */
  private final NavigableMap<EdgePattern,Map<LabeledGraph,Set<LabeledEdge>>>
    edgePatternIndex = new TreeMap<>();
  /**
   * local copy of global edge label dictionary
   */
  private final Map<String, Long> edgeLabelDictionary = new HashMap<>();
  /**
   * local copy of global vertex label dictionary
   */
  private final Map<String, Long> vertexLabelDictionary = new HashMap<>();

  /**
   * constructor
   * @param MTGSpanMaster master node
   */
  public MTGSpanWorker(MTGSpanMaster MTGSpanMaster) {
    this.master = MTGSpanMaster;
  }
  /**
   * start counting vertex labels in own thread
   */
  public void startVertexLabelCount() {
    thread = new Thread(new MTGSpanVertexLabelCounter(this));
    thread.start();
  }
  /**
   * start counting edge labels in own thread
   */
  public void startEdgeLabelCount() {
    thread = new Thread(new MTGSpanEdgeLabelCounter(this));
    thread.start();
  }
  /**
   * project all local graphs to GSpan graphs using the global dictionaries for
   * vertex and edge labels
   */
  public void indexEdgePatterns() {
    thread = new Thread(new MTGSpanEdgePatternIndexer(this));
    thread.start();
  }

  /**
   * remove globally infrequent edges from local edge index
   * @param infrequentEdgePatterns
   */
  public void processInfrequentEdgePatterns(
    Set<EdgePattern> infrequentEdgePatterns) {
    thread = new Thread(new MTGSpanEdgeIndexRemover(this, Sets.newHashSet
      (infrequentEdgePatterns)));
    thread.start();
  }

  /**
   * returns the support count of all locally occurring edge patterns
   */
  public Map<EdgePattern,Long> getEdgePatternSupports() {
    Map<EdgePattern,Long> edgePatternSupports = new HashMap<>();
    for(Map.Entry<EdgePattern, Map<LabeledGraph, Set<LabeledEdge>>> edgePatternSupporters :
      edgePatternIndex.entrySet()) {
      edgePatternSupports.put(edgePatternSupporters.getKey(),
        Long.valueOf(edgePatternSupporters.getValue().size()));
    }
    return edgePatternSupports;
  }
  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    return thread.toString() + "(" + graphs.size() + " graphs)";
  }
  /**
   * getters and setters
   */
  public Map<String, Long> getEdgeLabelSupports() {
    return edgeLabelSupports;
  }
  public Map<String, Long> getVertexLabelSupports() {
    return vertexLabelSupports;
  }
  public Thread getThread() {
    return thread;
  }
  public Collection<LabeledGraph> getGraphs() {
    return graphs;
  }

  public Collection<GSpanGraph> getSearchSpace() {
    return searchSpace;
  }

  public NavigableMap<EdgePattern, Map<LabeledGraph, Set<LabeledEdge>>> getEdgePatternIndex() {
    return edgePatternIndex;
  }

  //public MTGSpanMaster getMaster() {
  //  return master;
  //}


  public Map<String, Long> getEdgeLabelDictionary() {
    return edgeLabelDictionary;
  }

  public Map<String, Long> getVertexLabelDictionary() {
    return vertexLabelDictionary;
  }
}
