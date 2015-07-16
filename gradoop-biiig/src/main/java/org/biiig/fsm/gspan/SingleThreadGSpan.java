package org.biiig.fsm.gspan;

import org.biiig.fsm.gspan.common.EdgePattern;
import org.biiig.fsm.gspan.common.FrequentLabel;
import org.biiig.model.LabeledEdge;
import org.biiig.model.LabeledGraph;
import org.biiig.model.LabeledVertex;

import java.util.*;

/**
 * Created by peet on 09.07.15.
 */
public class SingleThreadGSpan {
  private Map<LabeledEdge, Set<LabeledGraph>> edgeSupporter;

  public Map<LabeledGraph, Float> findFrequentSubgraphs(
    List<LabeledGraph> graphs, float minFrequency) {

    int graphCount = graphs.size();
    int minSupport = Float.valueOf(graphCount * minFrequency).intValue();
    Map<LabeledGraph, Float> frequentSubgraphs = new HashMap<>();

    // find and encode frequent vertex labels
    Map<String, Long> vertexLabelDictionary =
      getFrequentLabelDictionary(getVertexLabelCounts(graphs), minSupport);

    // find and encode frequent edge labels
    Map<String, Long> edgeLabelDictionary = getFrequentLabelDictionary(
      getEdgeLabelCounts(graphs, vertexLabelDictionary), minSupport);

    // generate search space
    // by removing vertices and edges with infrequent labels
    List<LabeledGraph> searchSpace = getSearchSpace(
      graphs, vertexLabelDictionary, edgeLabelDictionary);

    // create edge pattern index
    Map<EdgePattern, Map<LabeledGraph, List<LabeledEdge>>> edgePatternIndex =
      getEdgePatternIndex(searchSpace);

    // remove infrequent edge patterns from index
    Map<EdgePattern, Map<LabeledGraph, List<LabeledEdge>>>
      frequentEdgePatternIndex =
      getFrequentEdgePatternIndex(minSupport, edgePatternIndex);

    // generate set of frequent edge patterns
    NavigableSet<EdgePattern> frequentEdgePatterns = new TreeSet<>();
    frequentEdgePatterns.addAll(frequentEdgePatternIndex.keySet());

    // init dfsCodeTree
    Map<EdgePattern, Map<DfsCode, List<DfsCodeMapper>>> dfsCodeTree =
      initDfsCodeTree(frequentEdgePatternIndex);

    // print DFS code tree
    printDFSCodeTree(dfsCodeTree);

    boolean goOn = true;

    while (goOn){
      goOn = false;

      // for each start edge pattern DFS code subtree
      for(EdgePattern startPattern : frequentEdgePatterns) {

        // for each subtree growth option
        for(EdgePattern growthPattern :
          frequentEdgePatterns.tailSet(startPattern)) {

          // for each current level DFS code
          for(Map.Entry<DfsCode, List<DfsCodeMapper>> dfcCodeMappers :
            dfsCodeTree.get(startPattern).entrySet()) {
            DfsCode dfsCode = dfcCodeMappers.getKey();
            List<DfsCodeMapper> mappers = dfcCodeMappers.getValue();

            System.out.println(dfsCode + " grow by " + growthPattern);

            //List<DfsEdge> growthOptions = dfsCode.growthOptions
            // (growthPattern);

            // for each mapper
            for(DfsCodeMapper mapper : mappers) {

              // get edges of the mapped graph matching the growth pattern
              List<LabeledEdge> matchingEdges = frequentEdgePatternIndex.get
                (growthPattern).get(mapper.getGraph());

              // if there are matching edges
              if(matchingEdges != null){
                for(LabeledEdge matchingEdge : matchingEdges){

                  System.out.println(matchingEdge);

                  // if edge not already mapped
                  if(!mapper.contains(matchingEdge)){
                  }
                }
              }

            }
          }
        }

      }
    }

    return frequentSubgraphs;
  }

  private void printDFSCodeTree(
    Map<EdgePattern, Map<DfsCode, List<DfsCodeMapper>>> dfsCodeTree) {
    for(Map.Entry<EdgePattern,Map<DfsCode,List<DfsCodeMapper>>>
      edgePatternIndexEntry : dfsCodeTree.entrySet()){

      EdgePattern edgePattern = edgePatternIndexEntry.getKey();
      Map<DfsCode,List<DfsCodeMapper>> indexEntry =
        edgePatternIndexEntry.getValue();

      System.out.println(edgePattern);

      for(Map.Entry<DfsCode,List<DfsCodeMapper>> dfsCodeMappers :
        indexEntry.entrySet()){

        DfsCode dfsCode = dfsCodeMappers.getKey();
        List<DfsCodeMapper> mappers = dfsCodeMappers.getValue();

        System.out.println("\t" + dfsCode);

        for(DfsCodeMapper mapper : mappers) {
          System.out.println("\t\t" + mapper);
        }
      }
    }
  }

  private Map<EdgePattern, Map<DfsCode, List<DfsCodeMapper>>>
  initDfsCodeTree(
    Map<EdgePattern, Map<LabeledGraph, List<LabeledEdge>>> frequentEdgePatternIndex) {
    Map<EdgePattern,Map<DfsCode,List<DfsCodeMapper>>> dfsCodeTree =
      new HashMap<>();

    // for each index entry
    for(Map.Entry<EdgePattern, Map<LabeledGraph, List<LabeledEdge>>>
      indexEntry : frequentEdgePatternIndex.entrySet()) {
      EdgePattern edgePattern = indexEntry.getKey();
      Map<LabeledGraph, List<LabeledEdge>> supportMap = indexEntry.getValue();

      // create DFS code for a single-edge graph meeting the edge pattern
      DfsEdge dfsEdge = new DfsEdge(0,1,edgePattern.getMinVertexLabel(),
        edgePattern.isOutgoing(),edgePattern.getEdgeLabel(),edgePattern
        .getMaxVertexLabel());

      DfsCode dfsCode = new DfsCode();
      dfsCode.add(dfsEdge);

      // add DFS code to DFS tree
      Map<DfsCode,List<DfsCodeMapper>> dfsCodeMappers = new HashMap<>();
      List<DfsCodeMapper> mappers = new ArrayList<>();
      dfsCodeMappers.put(dfsCode,mappers);
      dfsCodeTree.put(edgePattern,dfsCodeMappers);

      // for each graph supporting the current edge pattern
      for(Map.Entry<LabeledGraph, List<LabeledEdge>> supportEntry :
        supportMap.entrySet()) {
        LabeledGraph graph = supportEntry.getKey();
        List<LabeledEdge> edges = supportEntry.getValue();

        // map all edges matching the DFS code
        for(LabeledEdge edge : edges) {
          // map current graph to DFS code
          DfsCodeMapper mapper = new DfsCodeMapper(dfsCode,graph);
          mappers.add(mapper);
          mapper.map(dfsEdge,edge);
        }
      }
    } return dfsCodeTree;
  }

  private Map<EdgePattern, Map<LabeledGraph, List<LabeledEdge>>>
  getFrequentEdgePatternIndex(
    int minSupport,
    Map<EdgePattern, Map<LabeledGraph, List<LabeledEdge>>> edgePatternIndex) {
    Map<EdgePattern,Map<LabeledGraph,List<LabeledEdge>>>
      frequentEdgePatternIndex = new HashMap<>();

    for(Map.Entry<EdgePattern, Map<LabeledGraph, List<LabeledEdge>>> entry :
      edgePatternIndex.entrySet()) {

      if(entry.getValue().size() >= minSupport) {
        frequentEdgePatternIndex.put(entry.getKey(),entry.getValue());
      }
    }
    return frequentEdgePatternIndex;
  }

  private Map<EdgePattern, Map<LabeledGraph, List<LabeledEdge>>>
  getEdgePatternIndex(
    List<LabeledGraph> searchSpace) {
    Map<EdgePattern,Map<LabeledGraph,List<LabeledEdge>>>
      edgePatternIndex = new TreeMap<>();

    for(LabeledGraph graph : searchSpace) {
      for(LabeledEdge edge : graph.getEdges()){

        EdgePattern edgePattern = new EdgePattern(edge);

        Map<LabeledGraph,List<LabeledEdge>> supporters =
          edgePatternIndex.get (edgePattern);

        if(supporters == null){
          supporters = new HashMap<>();
          edgePatternIndex.put(edgePattern,supporters);
        }

        List<LabeledEdge> edgeInstances = supporters.get(graph);

        if(edgeInstances == null){
          edgeInstances = new ArrayList<>();
          supporters.put(graph,edgeInstances);
        }

        edgeInstances.add(edge);
      }
    } return edgePatternIndex;
  }


  private Map<String, Long> getVertexLabelCounts(List<LabeledGraph> graphs) {

    // count vertex labels
    Map<String, Long> vertexLabelCounts = new HashMap<>();

    for (LabeledGraph graph : graphs) {

      Set<String> vertexLabels = new HashSet<>();

      for (LabeledVertex vertex : graph.getVertices()) {
        vertexLabels.add(vertex.getLabel());
      }

      for (String vertexLabel : vertexLabels) {
        Long oldCount = vertexLabelCounts.get(vertexLabel);
        Long count = oldCount == null ? 1 : oldCount + 1;
        vertexLabelCounts.put(vertexLabel, count);
      }
    }

    return vertexLabelCounts;
  }

  private Map<String, Long> getEdgeLabelCounts(List<LabeledGraph> graphs,
    Map<String, Long> vertexLabelDictionary) {

    // count labels of edged without infrequent vertex labels
    Map<String, Long> edgeLabelCounts = new HashMap<>();

    for (LabeledGraph graph : graphs) {

      Set<String> edgeLabels = new HashSet<>();

      for (LabeledEdge edge : graph.getEdges()) {
        if (vertexLabelDictionary
          .containsKey(edge.getSourceVertex().getLabel()) &&
          vertexLabelDictionary.containsKey(edge.getTargetVertex().getLabel())) {

          edgeLabels.add(edge.getLabel());
        }
      }


      for (String edgeLabel : edgeLabels) {
        Long oldCount = edgeLabelCounts.get(edgeLabel);
        Long count = oldCount == null ? 1 : oldCount + 1;
        edgeLabelCounts.put(edgeLabel, count);
      }
    }

    return edgeLabelCounts;
  }

  private Map<String, Long> getFrequentLabelDictionary(
    Map<String, Long> labelCounts, int minSupport) {

    List<FrequentLabel> frequentLabels = new ArrayList<>();

    for (Map.Entry<String, Long> labelSupport : labelCounts.entrySet()) {

      String label = labelSupport.getKey();
      Long support = labelSupport.getValue();

      if (support >= minSupport) {
        frequentLabels.add(new FrequentLabel(label, support));
      }
    }

    Collections.sort(frequentLabels);

    // create vertex dictionary
    Map<String, Long> labelDictionary = new Hashtable<>();
    Long labelID = 0l;

    for (FrequentLabel label : frequentLabels) {
      labelID++;
      labelDictionary.put(label.getLabel(), labelID);
    }

    return labelDictionary;
  }

  private List<LabeledGraph> getSearchSpace(List<LabeledGraph> graphs,
    Map<String, Long> vertexLabelDictionary,
    Map<String, Long> edgeLabelDictionary) {
    List<LabeledGraph> searchSpace = new ArrayList<>();

    for (LabeledGraph graph : graphs) {

      LabeledGraph searchGraph = new LabeledGraph();
      Map<LabeledVertex, LabeledVertex> vertexMap = new HashMap<>();


      for (LabeledEdge edge : graph.getEdges()) {

        LabeledVertex sourceVertex = edge.getSourceVertex();
        Long sourceVertexSearchLabel =
          vertexLabelDictionary.get(sourceVertex.getLabel());

        // if source vertex label is frequent
        if (sourceVertexSearchLabel != null) {
          LabeledVertex targetVertex = edge.getTargetVertex();
          Long targetVertexSearchLabel =
            vertexLabelDictionary.get(targetVertex.getLabel());

          // and target vertex label is frequent
          if (targetVertexSearchLabel != null) {
            Long edgeSearchLabel = edgeLabelDictionary.get(edge.getLabel());

            // and edge label is frequent
            if (edgeSearchLabel != null) {

              // map source vertex if not already mapped
              LabeledVertex sourceSearchVertex = vertexMap.get(sourceVertex);
              if (sourceSearchVertex == null) {
                sourceSearchVertex = searchGraph.newVertex(sourceVertexSearchLabel.toString());
                vertexMap.put(sourceVertex, sourceSearchVertex);
              }

              // map target vertex if not already mapped
              LabeledVertex targetSearchVertex = vertexMap.get(targetVertex);
              if (targetSearchVertex == null) {
                targetSearchVertex = searchGraph.newVertex(targetVertexSearchLabel.toString());
                vertexMap.put(targetVertex, targetSearchVertex);
              }

              // create search edge
              LabeledEdge searchEdge = searchGraph
                .newEdge(sourceSearchVertex, edgeSearchLabel.toString(),
                  targetSearchVertex);
            }
          }
        }
      }
      if (!searchGraph.getEdges().isEmpty()) {
        searchSpace.add(searchGraph);
      }
    }
    return searchSpace;
  }

}
