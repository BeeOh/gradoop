package org.biiig.fsm.gspan.singlenode;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.common.LabeledGraph;
import org.biiig.fsm.gspan.common.SearchSpaceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * represents a worker in a single node GSpan environment,
 * every worker runs own threads for all distributed actions
 *
 * Created by p3et on 15.07.15.
 */
public class GSpanWorker {
  /**
   * master node
   */
  private final GSpanMaster master;
  /**
   * worker id
   */
  private final int partition;
  /**
   * thread of this worker
   */
  private Thread thread;
  /**
   * graphs processed by this worker
   */
  private final Collection<LabeledGraph> graphs = new ArrayList<>();
  /**
   * frequent subgraphs created on this worker
   */
  private Map<LabeledGraph, Float> frequentSubgraphs = new HashMap<>();
  /**
   * local vertex label counts
   */
  private final Map<String, Integer> vertexLabelSupports = new HashMap<>();
  /**
   * local copy of global vertex label dictionary
   */
  private final BiMap<String, Integer> vertexLabelDictionary =
    HashBiMap.create();
  /**
   * local edge label counts
   */
  private final Map<String, Integer> edgeLabelSupports = new HashMap<>();
  /**
   * local copy of global edge label dictionary
   */
  private final BiMap<String, Integer> edgeLabelDictionary = HashBiMap.create();
  /**
   * search space of GSpan graphs on this worker
   */
  private List<SearchSpaceItem> searchSpace = new ArrayList<>();
  /**
   *
   */
  private Map<DfsCode, Integer> dfsCodeSupports = new HashMap<>();
  /**
   * collection of globally frequent and locally occurring DFS codes
   */
  private Collection<DfsCode> growableDfsCodes = new ArrayList<>();

  // behaviour

  /**
   * constructor
   * @param master master node
   * @param partition id of the worker
   */
  public GSpanWorker(GSpanMaster master, int partition) {
    this.master = master;
    this.partition = partition;
  }
  /**
   * start counting vertex labels in own thread
   */
  public void countVertexLabelSupport() {
    thread = new Thread(new VertexLabelCounter(this));
    thread.start();
  }
  /**
   * create local copy of global vertex label dictionary
   */
  public void consumeVertexLabelDictionary() {
    thread = new Thread(new VertexLabelDictionaryConsumer(this));
    thread.start();
  }
  /**
   * start counting edge labels in own thread
   */
  public void countEdgeLabels() {
    thread = new Thread(new EdgeLabelCounter(this));
    thread.start();
  }

  /**
   * create local copy of global edge label dictionary
   */
  public void consumeEdgeLabelDictionary() {
    thread = new Thread(new EdgeLabelDictionaryConsumer(this));
    thread.start();
  }
  /**
   * generate local search space
   */
  public void initializeSearchSpace() {
    thread = new Thread(new SearchSpaceInitializer(this));
    thread.start();
  }

  /**
   * start counting minimum DFS codes in own thread
   */
  public void countDfsCodeSupport() {
    thread = new Thread(new DfsCodeCounter(this));
    thread.start();
  }
  /**
   * create local copy of globally frequent DFS codes
   */
  public void consumeFrequentDfsCodes() {
    thread = new Thread(new FrequentDfsCodeConsumer(this));
    thread.start();
  }
  /**
   * grows all local DFS codes by one edge following GSPan growth restrictions
   */
  public void growFrequentDfsCodes() {
    //thread = new Thread(new MTGSpanAdjacencyListBasedDfsCodeGrower(this));
    thread = new Thread(new Miner(this));
    thread.start();
  }
  /**
   * create labelled Graphs from a allocated partition of globally frequent
   * DFS codes
   */
  public void generateGraphsFromFrequentDfsCodes() {
    thread = new Thread(new FrequentSubgraphDecoder(this));
    thread.start();
  }

  // override methods

  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    return "Worker " + this.partition + " (" + graphs.size() + " graphs)";
  }

  // getters and setters

  public Thread getThread() {
    return thread;
  }

  public Collection<LabeledGraph> getGraphs() {
    return graphs;
  }

  public GSpanMaster getMaster() {
    return master;
  }

  public int getPartition() {
    return partition;
  }

  public Map<String, Integer> getEdgeLabelSupports() {
    return edgeLabelSupports;
  }
  public Map<String, Integer> getVertexLabelSupports() {
    return vertexLabelSupports;
  }

  public Collection<DfsCode> getGrowableDfsCodes() {
    return growableDfsCodes;
  }

  public BiMap<String, Integer> getEdgeLabelDictionary() {
    return edgeLabelDictionary;
  }
  public BiMap<String, Integer> getVertexLabelDictionary() {
    return vertexLabelDictionary;
  }

  public Map<LabeledGraph, Float> getFrequentSubgraphs() {
    return frequentSubgraphs;
  }

  public Map<DfsCode, Integer> getDfsCodeSupports() {
    return dfsCodeSupports;
  }

  public List<SearchSpaceItem> getSearchSpace() {
    return searchSpace;
  }


}
