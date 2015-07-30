package org.biiig.fsm.gspan.multithreaded;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.biiig.fsm.gspan.DfsCode;
import org.biiig.fsm.gspan.DfsCodeMapper;
import org.biiig.fsm.gspan.GSpanGraph;
import org.biiig.model.LabeledGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
   * worker id
   */
  private final int partition;
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
  private final Map<String,Integer> vertexLabelSupports = new HashMap<>();
  /**
   * local edge label counts
   */
  private final Map<String,Integer> edgeLabelSupports = new HashMap<>();
  /**
   * search space of GSpan graphs on this worker
   */
  private Map<DfsCode,Collection<DfsCodeMapper>> dfsCodeMappersMap =
    new TreeMap<>();
  /**
   * DFS codes and local graphs supporting the code
   */
  private Map<DfsCode,Set<GSpanGraph>> dfsCodeSupportersMap = new HashMap<>();
  /**
   * collection of globally frequent and locally occurring DFS codes
   */
  private Collection<DfsCode> growableDfsCodes = new ArrayList<>();
  /**
   * local copy of global edge label dictionary
   */
  private final BiMap<String, Integer> edgeLabelDictionary = HashBiMap.create();
  /**
   * local copy of global vertex label dictionary
   */
  private final BiMap<String, Integer> vertexLabelDictionary = HashBiMap.create();


  private Map<LabeledGraph, Float> frequentSubgraphs = new HashMap<>();

  /**
   * constructor
   * @param MTGSpanMaster master node
   * @param partition
   */
  public MTGSpanWorker(MTGSpanMaster MTGSpanMaster, int partition) {
    this.master = MTGSpanMaster;
    this.partition = partition;
  }
  /**
   * start counting vertex labels in own thread
   */
  public void countVertexLabels() {
    thread = new Thread(new MTGSpanVertexLabelCounter(this));
    thread.start();
  }
  /**
   * start counting edge labels in own thread
   */
  public void countEdgeLabels() {
    thread = new Thread(new MTGSpanEdgeLabelCounter(this));
    thread.start();
  }

  /**
   * generates local search space based on index
   */
  public void initializeSearchSpace() {
    thread = new Thread(new MTGSpanSearchSpaceInitializer(this));
    thread.start();
  }

  /**
   * grows all local DFS codes by one edge following GSPan growth restrictions
   */
  public void growFrequentDfsCodes() {
    thread = new Thread(new MTGSpanDfsCodeGrower(this));
    thread.start();
  }


  public void consumeVertexLabelDictionary() {
    thread = new Thread(new MTGSpanVertexLabelDictionaryConsumer(this));
    thread.start();
  }

  public void consumeEdgeLabelDictionary() {
    thread = new Thread(new MTGSpanEdgeLabelDictionaryConsumer(this));
    thread.start();
  }

  public Map<DfsCode, Integer> getDfsCodeSupports() {
    Map<DfsCode,Integer> dfsCodeSupports = new HashMap<>();

    for(Map.Entry<DfsCode,Set<GSpanGraph>> dfsCodeSupporters :
      dfsCodeSupportersMap.entrySet()) {
      dfsCodeSupports.put(dfsCodeSupporters.getKey(),
        dfsCodeSupporters.getValue().size());
    }

    return dfsCodeSupports;
  }

  public void generateGraphsFromFrequentDfsCodes() {
    thread = new Thread(new MTSpanFrequentSubgraphGenerator(this));
    thread.start();
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
  public Map<String, Integer> getEdgeLabelSupports() {
    return edgeLabelSupports;
  }
  public Map<String, Integer> getVertexLabelSupports() {
    return vertexLabelSupports;
  }
  public Thread getThread() {
    return thread;
  }

  public Collection<LabeledGraph> getGraphs() {
    return graphs;
  }

  public Map<DfsCode, Collection<DfsCodeMapper>> getDfsCodeMappersMap() {
    return dfsCodeMappersMap;
  }
  public BiMap<String, Integer> getEdgeLabelDictionary() {
    return edgeLabelDictionary;
  }
  public BiMap<String, Integer> getVertexLabelDictionary() {
    return vertexLabelDictionary;
  }

  public Collection<DfsCode> getGrowableDfsCodes() {
    return growableDfsCodes;
  }

  public void consumeFrequentDfsCodes() {
    thread = new Thread(new MTGSpanFrequentDfsCodeConsumer(this));
    thread.start();
  }

  public MTGSpanMaster getMaster() {
    return master;
  }

  public Map<DfsCode, Set<GSpanGraph>> getDfsCodeSupportersMap() {
    return dfsCodeSupportersMap;
  }

  public int getParition() {
    return partition;
  }

  public Map<LabeledGraph, Float> getFrequentSubgraphs() {
    return frequentSubgraphs;
  }
}
