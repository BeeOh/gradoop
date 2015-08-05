package org.biiig.fsm.gspan.multithreaded;

import io.netty.util.internal.ConcurrentSet;
import org.apache.commons.lang3.StringUtils;
import org.biiig.fsm.gspan.DfsCode;
import org.biiig.fsm.gspan.common.FrequentLabel;
import org.biiig.model.LabeledGraph;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by peet on 15.07.15.
 */
public class MTGSpanMaster {

  /**
   * minimal support of frequent subgraphs
   */
  private final Integer minSupport;
  /**
   * worker collection
   */
  private final Collection<MTGSpanWorker> workers = new ArrayList<>();

  // aggregation data sets

  /**
   * global supports of vertex labels
   */
  private final Map<String, Integer> vertexLabelSupports = new HashMap<>();
  /**
   * global supports of edge labels
   */
  private final Map<String, Integer> edgeLabelSupports = new HashMap<>();
  /**
   * global supports of DFS codes
   */
  private Map<DfsCode,Integer> dfsCodeSupports = new HashMap<>();

  private Map<LabeledGraph, Float> frequentSubgraphs = new HashMap<>();

  // aggregation results for broadcasting

  /**
   * dictionary from labeled vertex labels to GSpan vertex labels
   */
  private final ConcurrentHashMap<String,Integer> vertexLabelDictionary =
    new ConcurrentHashMap<>();
  /**
   * dictionary from labeled edge labels to GSpan edge labels
   */
  private ConcurrentHashMap<String, Integer> edgeLabelDictionary =
    new ConcurrentHashMap<>();
  /**
   * globally frequent and growable DFS codes
   */
  private ConcurrentSet<DfsCode> growableDfsCodes = new ConcurrentSet<>();
  private int growableDfsCodeCount = 0;

  /**
   * memory for globally frequent DFS codes and support
   */
  private ConcurrentHashMap<Integer,Map<DfsCode,Integer>>
    partitionedFrequentDfsCodeSupports =  new ConcurrentHashMap<>();
  private Integer numberOfGraphs;

  public MTGSpanMaster(Collection<LabeledGraph> graphs, Float threshold){

    this.numberOfGraphs = graphs.size();

    // calculate minimum support from threshold
    Integer minSupport = Float.valueOf(numberOfGraphs * threshold).intValue();

    this.minSupport = minSupport;
    int numberOfWorkers = Runtime.getRuntime().availableProcessors();

    for(int i=0; i < numberOfWorkers; i++) {
      this.workers.add(new MTGSpanWorker(this,i));
      this.partitionedFrequentDfsCodeSupports.put(i,new HashMap<DfsCode,
        Integer>());
    }
  }
  /**
   * sub-workflow that distributes graphs equally to workers
   * @param graphs graphs to distribute
   */
  public void distribute(Collection<LabeledGraph> graphs) {

    int graphsPerWorker = 10;// graphs.size() / workers.size();
    Long graphCount = 0l;

    Iterator<MTGSpanWorker> workerIterator = workers.iterator();
    MTGSpanWorker worker = workerIterator.next();

    for(LabeledGraph graph : graphs) {

      if(graphCount >= graphsPerWorker) {
        worker = workerIterator.next();
      }

      graphCount++;
      worker.getGraphs().add(graph);
    }
  }
  /**
   * sub-workflow to mine frequent subgraphs
   */
  public void mine() {
    // frequent vertex labels
    countVertexLabels();
    aggregateVertexLabelSupport();
    generateVertexLabelDictionary();
    broadcastVertexLabelDictionary();

    System.out.println(vertexLabelDictionary);

    // frequent edge labels
    countEdgeLabels();
    aggregateEdgeLabelSupport();
    generateEdgeLabelDictionary();
    broadcastEdgeLabelDictionary();

    System.out.println(edgeLabelDictionary);

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
    generateGraphsFromFreuqentDfsCodes();
    collecectFrequentSubgraphs();
  }

  private void collecectFrequentSubgraphs() {
    for(MTGSpanWorker worker : workers) {
      frequentSubgraphs.putAll(worker.getFrequentSubgraphs());
    }
  }

  private void countVertexLabels() {
    for(MTGSpanWorker worker : workers) {
      worker.countVertexLabels();
    }
    joinWorkerThreads();
  }

  private void countEdgeLabels() {
    for(MTGSpanWorker worker : workers) {
      worker.countEdgeLabels();
    }
    joinWorkerThreads();
  }

  private void initializeSearchSpace() {
    for(MTGSpanWorker worker : workers) {
      worker.initializeSearchSpace();
    }
    joinWorkerThreads();
    for(MTGSpanWorker worker : workers){
      worker.getGraphs().clear();
    }
  }

  private void aggregateVertexLabelSupport() {
    vertexLabelSupports.clear();

    for(MTGSpanWorker worker : workers) {
      aggregateLabelSupports(vertexLabelSupports,
        worker.getVertexLabelSupports());
    }
  }

  private void aggregateEdgeLabelSupport() {
    edgeLabelSupports.clear();

    for(MTGSpanWorker worker : workers) {
      aggregateLabelSupports(edgeLabelSupports, worker.getEdgeLabelSupports());
    }
  }
  /**
   * adds worker labels supports to global label supports
   * @param globalLabelSupports global label supports
   * @param workerLabelSupports local label supports
   */
  private void aggregateLabelSupports(Map<String, Integer> globalLabelSupports,
    Map<String, Integer> workerLabelSupports) {

    if(globalLabelSupports.isEmpty()){
      globalLabelSupports.putAll(workerLabelSupports);
    } else {
      for(Map.Entry<String,Integer> workerLabelSupport :
        workerLabelSupports.entrySet()) {
        String label = workerLabelSupport.getKey();
        Integer workerSupport = workerLabelSupport.getValue();
        Integer globalSupport = globalLabelSupports.get(label);
        globalLabelSupports.put(label,
          globalSupport == null ? workerSupport : globalSupport + workerSupport);
      }
    }
    workerLabelSupports.clear();
  }

  private void aggregateDfsCodeSupports() {
    dfsCodeSupports.clear();

    // for each worker
    for(MTGSpanWorker worker : workers) {

      if(dfsCodeSupports.isEmpty()) {
        dfsCodeSupports.putAll(worker.getDfsCodeSupports());
      } else {
        for(Map.Entry<DfsCode,Integer> workerDfsCodeSupport : worker
          .getDfsCodeSupports().entrySet()){

          DfsCode dfsCode = workerDfsCodeSupport.getKey();
          Integer workerSupport = workerDfsCodeSupport.getValue();
          Integer globalSupport = dfsCodeSupports.get(dfsCode);

          dfsCodeSupports.put(dfsCode,globalSupport == null ? workerSupport :
            globalSupport + workerSupport);
        }
      }
    }
  }

  private void generateVertexLabelDictionary() {
    generateLabelDictionary(vertexLabelDictionary,vertexLabelSupports);
  }

  private void generateEdgeLabelDictionary() {
    generateLabelDictionary(edgeLabelDictionary,edgeLabelSupports);
  }
  /**
   * generates dictionary entries for frequently supported labels
   * @param dictionary the dictionary to generate entries for
   * @param labelSupports map of labels and support counts
   */
  private void generateLabelDictionary(Map<String, Integer> dictionary,
    Map<String, Integer> labelSupports) {

    dictionary.clear();

    NavigableSet<FrequentLabel> frequentLabels = new TreeSet<>();

    for(Map.Entry<String,Integer> labelSupport : labelSupports.entrySet()) {
      String label = labelSupport.getKey();
      Integer support = labelSupport.getValue();

      if(support >= minSupport){
        frequentLabels.add(
          new FrequentLabel(label, support));
      }
    }

    Integer gSpanLabel = 0;
    for(FrequentLabel frequentLabel : frequentLabels.descendingSet()) {
      gSpanLabel++;
      dictionary.put(frequentLabel.getLabel(),gSpanLabel);
    }
  }

  private void resetGrowableDfsCodes() {
    growableDfsCodes.clear();
    for(Map.Entry<DfsCode,Integer> dfsCodeSupport : dfsCodeSupports.entrySet
      ()) {

      DfsCode dfsCode = dfsCodeSupport.getKey();
      Integer support = dfsCodeSupport.getValue();

      if(support >= minSupport){
        growableDfsCodes.add(dfsCode);
        partitionedFrequentDfsCodeSupports.get(growableDfsCodeCount %
          workers.size()).put(dfsCode, support);
        growableDfsCodeCount++;
      }
    }
  }

  private void generateGraphsFromFreuqentDfsCodes() {
    for(MTGSpanWorker worker : workers) {
      worker.generateGraphsFromFrequentDfsCodes();
    }
    joinWorkerThreads();
  }

  private void broadcastVertexLabelDictionary() {
    for(MTGSpanWorker worker : workers) {
      worker.consumeVertexLabelDictionary();
    }
    joinWorkerThreads();
  }


  private void broadcastEdgeLabelDictionary() {
    for(MTGSpanWorker worker : workers) {
      worker.consumeEdgeLabelDictionary();
    }
    joinWorkerThreads();
  }

  private void broadcastGrowableDfsCodes() {
    for(MTGSpanWorker worker : workers){
      worker.consumeFrequentDfsCodes();
    }
    joinWorkerThreads();
  }


  private void joinWorkerThreads() {
    for(MTGSpanWorker worker : workers) {
      try {
        worker.getThread().join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void growFrequentDfsCodes() {
    for(MTGSpanWorker worker : workers){
      worker.growFrequentDfsCodes();
    }
    joinWorkerThreads();
  }

  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString(){
    return StringUtils.join(workers,",");
  }
  /**
   * getters and setters
   */
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

  public void printFrequentSubgraphs() {
    for(MTGSpanWorker worker : workers) {
      for(Map.Entry<LabeledGraph,Float> entry : worker.getFrequentSubgraphs()
        .entrySet()) {
        System.out.println(entry.getValue() + "\t" + entry.getKey());
      }
    }
  }

  public Map<LabeledGraph, Float> getFrequentSubgraphs() {
    return frequentSubgraphs;
  }

  public void printfrequentDfsCodes() {
    for(Map<DfsCode,Integer> frequentDfsCodeSupports :
      partitionedFrequentDfsCodeSupports
      .values()) {
      for(Map.Entry<DfsCode,Integer> entry : frequentDfsCodeSupports.entrySet()) {
        System.out.println(entry.getValue() + "\t" + entry.getKey());
      }
    }
  }
}
