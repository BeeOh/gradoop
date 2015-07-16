package org.biiig.fsm.gspan.multithreaded;

import org.apache.commons.lang3.StringUtils;
import org.biiig.fsm.gspan.common.EdgePattern;
import org.biiig.fsm.gspan.common.FrequentLabel;
import org.biiig.model.LabeledGraph;

import java.util.*;

/**
 * Created by peet on 15.07.15.
 */
public class MTGSpanMaster {

  /**
   * minimal support of frequent subgraphs
   */
  private final Long minSupport;
  /**
   * worker collection
   */
  private final Collection<MTGSpanWorker> workers = new ArrayList<>();
  /**
   * dictionary from labeled vertex labels to GSpan vertex labels
   */
  private final Map<String,Long> vertexLabelDictionary =
    new HashMap<>();
  /**
   * dictionary from labeled edge labels to GSpan edge labels
   */
  private Map<String, Long> edgeLabelDictionary =
    new HashMap<>();
  /**
   * constructor
   * @param minSupport minimal support of frequent subgraphs
   */
  public MTGSpanMaster(Long minSupport){

    this.minSupport = minSupport;
    int numberOfWorkers = Runtime.getRuntime().availableProcessors();

    for(int i=0; i < numberOfWorkers; i++) {
      this.workers.add(new MTGSpanWorker(this));
    }
  }
  /**
   * sub-workflow that distributes graphs equally to workers
   * @param graphs graphs to distribute
   */
  public void distribute(Collection<LabeledGraph> graphs) {

    int graphsPerWorker = graphs.size() / workers.size();
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
   * sub-workflow to generate and broadcast label dictionaries
   */
  public void generateFrequentLabelDictionaries() {
    fillDictionaryFromLabelSupports(vertexLabelDictionary,
      getVertexLabelSupports());
    for(MTGSpanWorker worker : workers) {
      worker.getVertexLabelDictionary().putAll(vertexLabelDictionary);
    }
    fillDictionaryFromLabelSupports(edgeLabelDictionary,
      getEdgeLabelSupports());
    for(MTGSpanWorker worker : workers) {
      worker.getEdgeLabelDictionary().putAll(edgeLabelDictionary);
    }
  }

  /**
   * sub-workflow to identify and prune infrequent edge patterns
   */
  public void indexFrequentEdgePatterns() {
    Map<EdgePattern, Long> globalEdgePatternSupports = getEdgePatternSupports();

    Set<EdgePattern> infrequentEdgePattern =
      getInfrequentEdgePatterns(globalEdgePatternSupports);

    for(MTGSpanWorker worker : workers) {
      worker.processInfrequentEdgePatterns(infrequentEdgePattern);
    }
    for(MTGSpanWorker worker : workers) {
      try {
        worker.getThread().join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println(globalEdgePatternSupports);
    System.out.println(infrequentEdgePattern);
  }
  /**
   * starts vertex label count for each worker and aggregates results
   * @return global vertex label support
   */
  private Map<String, Long> getVertexLabelSupports() {
    for(MTGSpanWorker worker : workers) {
      worker.startVertexLabelCount();
    }
    Map<String,Long> globalVertexLabelSupports = new HashMap<>();

    for(MTGSpanWorker worker : workers) {
      try {
        worker.getThread().join();
        addWorkerLabelSupports(globalVertexLabelSupports,
          worker.getVertexLabelSupports());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return globalVertexLabelSupports;
  }
  /**
   * starts vertex label count for each worker and aggregates results,
   * only considers labels of edges which vertex labels are contained
   * in the vertex label dictionary (i.e., are considered to be frequent)
   * @return global edge label support
   */
  private Map<String, Long> getEdgeLabelSupports() {
    for(MTGSpanWorker worker : workers) {
      worker.startEdgeLabelCount();
    }
    Map<String,Long> globalEdgeLabelSupports = new HashMap<>();

    for(MTGSpanWorker worker : workers) {
      try {
        worker.getThread().join();
        addWorkerLabelSupports(globalEdgeLabelSupports,
          worker.getEdgeLabelSupports());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return  globalEdgeLabelSupports;
  }

  /**
   * starts edge pattern count for each worker and aggregates results,
   * only considers vertex and edge labels which are contained in the
   * correpsponding label dictionaries (i.e., are considered to be frequent)
   * @return global edge pattern support
   */
  private Map<EdgePattern, Long> getEdgePatternSupports() {
    for(MTGSpanWorker worker : workers) {
      worker.indexEdgePatterns();
    }
    Map<EdgePattern,Long> globalEdgePatternSupports = new TreeMap<>();

    for(MTGSpanWorker worker : workers) {
      try {
        worker.getThread().join();

        for(Map.Entry<EdgePattern,Long> workerEdgePatternSupport :
          worker.getEdgePatternSupports().entrySet()) {

          EdgePattern pattern = workerEdgePatternSupport.getKey();
          Long workerSupport = workerEdgePatternSupport.getValue();
          Long globalSupport = globalEdgePatternSupports.get(pattern);
          globalEdgePatternSupports.put(pattern,
            globalSupport == null ? workerSupport : globalSupport + workerSupport);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return globalEdgePatternSupports;
  }
  /**
   * adds worker labels supports to global label supports
   * @param globalLabelSupports global label supports
   * @param workerLabelSupports local label supports
   */
  private void addWorkerLabelSupports(Map<String, Long> globalLabelSupports,
    Map<String, Long> workerLabelSupports) {
    for(Map.Entry<String,Long> workerLabelSupport :
      workerLabelSupports.entrySet()) {

      String label = workerLabelSupport.getKey();
      Long workerSupport = workerLabelSupport.getValue();
      Long globalSupport = globalLabelSupports.get(label);
      globalLabelSupports.put(label,
        globalSupport == null ? workerSupport : globalSupport + workerSupport);
    }
    workerLabelSupports.clear();
  }
  /**
   * generates dictionary entries for frequently supported labels
   * @param dictionary the dictionary to generate entries for
   * @param labelSupports map of labels and support counts
   */
  private void fillDictionaryFromLabelSupports(
    Map<String, Long> dictionary,
    Map<String, Long> labelSupports) {

    NavigableSet<FrequentLabel> frequentLabels = new TreeSet<>();

    for(Map.Entry<String,Long> labelSupport : labelSupports.entrySet()) {
      String label = labelSupport.getKey();
      Long support = labelSupport.getValue();

      if(support >= minSupport){
        frequentLabels.add(
          new FrequentLabel(label, support));
      }
    }

    Long gSpanLabel = 0l;
    for(FrequentLabel frequentLabel : frequentLabels.descendingSet()) {
      gSpanLabel++;
      dictionary.put(frequentLabel.getLabel(),gSpanLabel);
    }
  }

  /**
   *
   * @param globalEdgePatternSupports map of edge patterns and support counts
   * @return set of infrequent edge patterns
   */
  private Set<EdgePattern> getInfrequentEdgePatterns(
    Map<EdgePattern, Long> globalEdgePatternSupports) {
    Set<EdgePattern> infrequentEdgePattern = new HashSet<>();

    for(Map.Entry<EdgePattern,Long> globalEdgePatternSupport
      : globalEdgePatternSupports.entrySet()) {
      if(globalEdgePatternSupport.getValue() < minSupport) {
        infrequentEdgePattern.add(globalEdgePatternSupport.getKey());
      }
    }
    return infrequentEdgePattern;
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
  public Map<String, Long> getVertexLabelDictionary() {
    return vertexLabelDictionary;
  }
  public Map<String, Long> getEdgeLabelDictionary() {
    return edgeLabelDictionary;
  }


  public Collection<MTGSpanWorker> getWorkers() {
    return workers;
  }
}
