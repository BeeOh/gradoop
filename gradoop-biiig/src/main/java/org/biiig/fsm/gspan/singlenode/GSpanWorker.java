package org.biiig.fsm.gspan.singlenode;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.DfsCodeMapper;
import org.biiig.fsm.gspan.common.GSpanGraph;
import org.biiig.fsm.common.LabeledGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
  private Map<DfsCode, Map<GSpanGraph,
    Collection<DfsCodeMapper>>> dfsCodeSupporterMappersMap = new TreeMap<>();
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

  // convenience methods

  /**
   * determine the local support of DFS codes
   * @return map of locally occurring DFS codes and support count
   */
  public Map<DfsCode, Integer> getDfsCodeSupports() {
    Map<DfsCode, Integer> dfsCodeSupports = new HashMap<>();

    for (Map.Entry<DfsCode, Map<GSpanGraph, Collection<DfsCodeMapper>>>
      dfsCodeSupporterMappers : dfsCodeSupporterMappersMap.entrySet()) {
      dfsCodeSupports.put(dfsCodeSupporterMappers.getKey(),
        dfsCodeSupporterMappers.getValue().size());
    }

    return dfsCodeSupports;
  }

  // override methods

  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    return thread.toString() + "(" + graphs.size() + " graphs)";
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

  public Map<DfsCode, Map<GSpanGraph, Collection<DfsCodeMapper>>>
  getDfsCodeSupporterMappersMap() {
    return dfsCodeSupporterMappersMap;
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









}
