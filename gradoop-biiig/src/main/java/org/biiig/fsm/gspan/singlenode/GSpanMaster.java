package org.biiig.fsm.gspan.singlenode;

import io.netty.util.internal.ConcurrentSet;
import org.apache.commons.lang3.StringUtils;
import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.LabelSupport;
import org.biiig.fsm.common.LabeledGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by p3et on 15.07.15.
 */
public class GSpanMaster {
  /**
   * search space size
   */
  private Integer numberOfGraphs;
  /**
   * minimal support of frequent subgraphs
   */
  private Integer minSupport;
  /**
   * worker collection
   */
  private final Collection<GSpanWorker> workers = new ArrayList<>();
  /**
   * global supports of vertex labels
   */
  private final Map<String, Integer> vertexLabelSupports = new HashMap<>();
  /**
   * dictionary from labeled vertex labels to GSpan vertex labels
   */
  private final ConcurrentHashMap<String, Integer> vertexLabelDictionary =
    new ConcurrentHashMap<>();
  /**
   * global supports of edge labels
   */
  private final Map<String, Integer> edgeLabelSupports = new HashMap<>();
  /**
   * dictionary from labeled edge labels to GSpan edge labels
   */
  private ConcurrentHashMap<String, Integer> edgeLabelDictionary =
    new ConcurrentHashMap<>();
  /**
   * global supports of DFS codes
   */
  private Map<DfsCode, Integer> dfsCodeSupports = new HashMap<>();
  /**
   * globally frequent and growable DFS codes
   */
  private ConcurrentSet<DfsCode> growableDfsCodes = new ConcurrentSet<>();
  /**
   * global counter of frequent DFS codes for equal distribution of frequent
   * subgraph decoding
   */
  private int frequentDfsCodeCount = 0;
  /**
   * memory for globally frequent DFS codes and support
   */
  private ConcurrentHashMap<Integer, Map<DfsCode, Integer>>
  partitionedFrequentDfsCodeSupports = new ConcurrentHashMap<>();
  /**
   * FSM result
   */
  private Map<LabeledGraph, Float> frequentSubgraphs = new HashMap<>();

  // behaviour

  /**
   * constructor, creates workers according to the number of available CPU cores
   */
  public GSpanMaster() {
    int numberOfWorkers = Runtime.getRuntime().availableProcessors();

    for (int i = 0; i < numberOfWorkers; i++) {
      this.workers.add(new GSpanWorker(this, i));
      this.partitionedFrequentDfsCodeSupports.put(i, new HashMap<DfsCode,
        Integer>());
    }
  }
  /**
   * workflow representing the GSpan algorithm
   * @param threshold minimum support frequency
   */
  public void mine(float threshold) {

    setNumberOfGraphsAndMinSupport(threshold);

    // frequent vertex labels
    countVertexLabelSupport();
    aggregateVertexLabelSupport();
    generateVertexLabelDictionary();
    broadcastVertexLabelDictionary();

    // frequent edge labels
    countEdgeLabelSupport();
    aggregateEdgeLabelSupport();
    generateEdgeLabelDictionary();
    broadcastEdgeLabelDictionary();

    // single edge DFS codes
    initializeSearchSpace();
    aggregateDfsCodeSupports();
    resetGrowableDfsCodes();

    // grow frequent DFS codes
    while (!growableDfsCodes.isEmpty()) {
      broadcastGrowableDfsCodes();
      growFrequentDfsCodes();
      aggregateDfsCodeSupports();
      resetGrowableDfsCodes();
    }

    // generate graph from frequent DFS codes
    generateGraphsFromFrequentDfsCodes();
    collectFrequentSubgraphs();
  }
  /**
   * encapsulation of field initializers
   * @param threshold minimum support frequency
   */
  private void setNumberOfGraphsAndMinSupport(float threshold) {
    numberOfGraphs = 0;

    for (GSpanWorker worker : workers) {
      numberOfGraphs += worker.getGraphs().size();
    }
    minSupport = Float.valueOf(numberOfGraphs * threshold).intValue();
  }
  /**
   * starts counting of vertex label support on all workers
   */
  private void countVertexLabelSupport() {
    for (GSpanWorker worker : workers) {
      worker.countVertexLabelSupport();
    }
    joinWorkerThreads();
  }

  /**
   * starts counting edge label support on all workers
   */
  private void countEdgeLabelSupport() {
    for (GSpanWorker worker : workers) {
      worker.countEdgeLabels();
    }
    joinWorkerThreads();
  }
  /**
   * starts aggregation of global vertex label supports
   */
  private void aggregateVertexLabelSupport() {
    vertexLabelSupports.clear();

    for (GSpanWorker worker : workers) {
      aggregateLabelSupports(vertexLabelSupports,
        worker.getVertexLabelSupports());
    }
  }
  /**
   * starts aggregation of global edge label supports
   */
  private void aggregateEdgeLabelSupport() {
    edgeLabelSupports.clear();

    for (GSpanWorker worker : workers) {
      aggregateLabelSupports(edgeLabelSupports, worker.getEdgeLabelSupports());
    }
  }
  /**
   * reusable encapsulation for aggregation of vertex and edge label supports;
   * adds worker labels supports to global label supports
   * @param globalLabelSupports global label supports
   * @param workerLabelSupports local label supports
   */
  private void aggregateLabelSupports(Map<String, Integer> globalLabelSupports,
    Map<String, Integer> workerLabelSupports) {

    if (globalLabelSupports.isEmpty()) {
      globalLabelSupports.putAll(workerLabelSupports);
    } else {
      for (Map.Entry<String, Integer> workerLabelSupport :
        workerLabelSupports.entrySet()) {
        String label = workerLabelSupport.getKey();
        Integer workerSupport = workerLabelSupport.getValue();
        Integer globalSupport = globalLabelSupports.get(label);
        globalLabelSupports.put(label,
          globalSupport == null ?
            workerSupport : globalSupport + workerSupport);
      }
    }
    workerLabelSupports.clear();
  }
  /**
   * generates vertex label dictionary from global vertex label supports
   */
  private void generateVertexLabelDictionary() {
    generateLabelDictionary(vertexLabelDictionary, vertexLabelSupports);
  }
  /**
   * generates edge label dictionary from global edge label supports
   */
  private void generateEdgeLabelDictionary() {
    generateLabelDictionary(edgeLabelDictionary, edgeLabelSupports);
  }
  /**
   * reusable encapsulation for vertex and edge label dictionary generation;
   * generates dictionary entries for frequently supported labels
   * @param dictionary the dictionary to generate entries for
   * @param labelSupports map of labels and support counts
   */
  private void generateLabelDictionary(Map<String, Integer> dictionary,
    Map<String, Integer> labelSupports) {

    dictionary.clear();

    NavigableSet<LabelSupport> frequentLabels = new TreeSet<>();

    for (Map.Entry<String, Integer> labelSupport : labelSupports.entrySet()) {
      String label = labelSupport.getKey();
      Integer support = labelSupport.getValue();

      if (support >= minSupport) {
        frequentLabels.add(
          new LabelSupport(label, support));
      }
    }

    Integer gSpanLabel = 0;
    for (LabelSupport labelSupport : frequentLabels.descendingSet()) {
      gSpanLabel++;
      dictionary.put(labelSupport.getLabel(), gSpanLabel);
    }
  }
  /**
   * triggers copying of the global vertex label dictionary on all workers
   */
  private void broadcastVertexLabelDictionary() {
    for (GSpanWorker worker : workers) {
      worker.consumeVertexLabelDictionary();
    }
    joinWorkerThreads();
  }
  /**
   * triggers copying of the global edge label dictionary on all workers
   */
  private void broadcastEdgeLabelDictionary() {
    for (GSpanWorker worker : workers) {
      worker.consumeEdgeLabelDictionary();
    }
    joinWorkerThreads();
  }
  /**
   * starts encoding labelled graphs into GSpan graphs on all workers;
   * references to labelled graphs will be removed to save RAM
   */
  private void initializeSearchSpace() {
    for (GSpanWorker worker : workers) {
      worker.initializeSearchSpace();
    }
    joinWorkerThreads();
    for (GSpanWorker worker : workers) {
      worker.getGraphs().clear();
    }
  }
  /**
   * starts aggregation of global DFS code supports
   */
  private void aggregateDfsCodeSupports() {
    dfsCodeSupports.clear();

    // for each worker
    for (GSpanWorker worker : workers) {
      for (Map.Entry<DfsCode, Integer> workerDfsCodeSupport : worker
        .getDfsCodeSupports().entrySet()) {

        DfsCode dfsCode = workerDfsCodeSupport.getKey().clone();
        Integer workerSupport = workerDfsCodeSupport.getValue();
        Integer globalSupport = dfsCodeSupports.get(dfsCode);

        dfsCodeSupports.put(dfsCode, globalSupport == null ? workerSupport :
          globalSupport + workerSupport);
      }
    }
  }
  /**
   * determines growable DFS codes
   * (growable = globally frequent and generated during last iteration)
   */
  private void resetGrowableDfsCodes() {
    growableDfsCodes.clear();
    for (Map.Entry<DfsCode, Integer> dfsCodeSupport :
      dfsCodeSupports.entrySet()) {

      DfsCode dfsCode = dfsCodeSupport.getKey();
      Integer support = dfsCodeSupport.getValue();

      if (support >= minSupport) {
        growableDfsCodes.add(dfsCode);
        partitionedFrequentDfsCodeSupports.get(frequentDfsCodeCount %
          workers.size()).put(dfsCode, support);
        frequentDfsCodeCount++;
      }
    }
  }
  /**
   * triggers copying of growable DFS codes to all workers
   */
  private void broadcastGrowableDfsCodes() {
    for (GSpanWorker worker : workers) {
      worker.consumeFrequentDfsCodes();
    }
    joinWorkerThreads();
  }
  /**
   * triggers DFS code growth on all workers
   */
  private void growFrequentDfsCodes() {
    for (GSpanWorker worker : workers) {
      worker.growFrequentDfsCodes();
    }
    joinWorkerThreads();
  }
  /**
   * triggers graph decoding from globally frequent DFS codes on all workers
   */
  private void generateGraphsFromFrequentDfsCodes() {
    for (GSpanWorker worker : workers) {
      worker.generateGraphsFromFrequentDfsCodes();
    }
    joinWorkerThreads();
  }
  /**
   * collects references to all decoded frequent subgraphs
   */
  private void collectFrequentSubgraphs() {
    for (GSpanWorker worker : workers) {
      frequentSubgraphs.putAll(worker.getFrequentSubgraphs());
    }
  }

  /**
   * reusable joining of threads on all workers
   */
  private void joinWorkerThreads() {
    for (GSpanWorker worker : workers) {
      try {
        worker.getThread().join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // override methods

  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    return StringUtils.join(workers, ",");
  }

  // getters and setters

  public Collection<GSpanWorker> getWorkers() {
    return workers;
  }

  public Map<String, Integer> getVertexLabelDictionary() {
    return vertexLabelDictionary;
  }
  public Map<String, Integer> getEdgeLabelDictionary() {
    return edgeLabelDictionary;
  }
  public Collection<DfsCode> getGrowableDfsCodes() {
    return growableDfsCodes;
  }

  public ConcurrentHashMap<Integer, Map<DfsCode, Integer>>
  getPartitionedFrequentDfsCodeSupports() {
    return partitionedFrequentDfsCodeSupports;
  }

  public Integer getNumberOfGraphs() {
    return numberOfGraphs;
  }

  public Map<LabeledGraph, Float> getFrequentSubgraphs() {
    return frequentSubgraphs;
  }
}
