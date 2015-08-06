package org.biiig.model;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by peet on 25.06.15.
 */
public class LabeledGraph implements Comparable<LabeledGraph> {

  private final List<LabeledVertex> vertices = new ArrayList<>();
  private final List<LabeledEdge> edges = new ArrayList<>();
  private final Map<LabeledVertex,List<LabeledEdge>> vertexOutgoingEdgesMap = new HashMap<>();
  private final Map<LabeledVertex,List<LabeledEdge>> vertexIncomingEdgesMap = new HashMap<>();


  public LabeledVertex newVertex(String label) {
    LabeledVertex vertex = new LabeledVertex(label);
    this.vertices.add(vertex);
    vertexOutgoingEdgesMap.put(vertex, new ArrayList<LabeledEdge>());
    vertexIncomingEdgesMap.put(vertex, new ArrayList<LabeledEdge>());
    return vertex;
  }

  public LabeledEdge newEdge(LabeledVertex sourceVertex, String label,
    LabeledVertex targetVertex) {
    LabeledEdge edge = new LabeledEdge(sourceVertex,label,targetVertex);
    this.edges.add(edge);
    vertexOutgoingEdgesMap.get(sourceVertex).add(edge);
    vertexIncomingEdgesMap.get(targetVertex).add(edge);
    return edge;
  }

  public String toString() {
    List<String> parts = new ArrayList<>();

    for(LabeledEdge edge : edges) {
      parts.add(
        "("
          + vertices.indexOf(edge.getSourceVertex())
          + ":"
          + edge.getSourceVertex().getLabel()
          + ")-"
          + edge.getLabel()
          + "->("
          + vertices.indexOf(edge.getTargetVertex())
          + ":"
          + edge.getTargetVertex().getLabel()
        + ")"
      );
    }
    return "<" + StringUtils.join(parts,",") + ">";
  }

  public List<LabeledVertex> getVertices() {
    return vertices;
  }

  public List<LabeledEdge> getEdges() {
    return edges;
  }

  @Override
  public int compareTo(LabeledGraph other) {
    // compares invariants only

    int comparison = this.vertices.size() - other.vertices.size();

    if(comparison == 0) {
      comparison = this.edges.size() - other.edges.size();

      if(comparison == 0) {
        // compare vertex sets
        List<LabeledVertex> ownVertices = Lists.newArrayList(this.vertices);
        List<LabeledVertex> otherVertices = Lists.newArrayList(other.vertices);

        Collections.sort(ownVertices);
        Collections.sort(otherVertices);

        Iterator<LabeledVertex> ownVertexIterator = ownVertices.iterator();
        Iterator<LabeledVertex> otherVertexIterator = otherVertices.iterator();

        while(comparison == 0 && ownVertexIterator.hasNext()) {
          comparison = ownVertexIterator.next().compareTo(
            otherVertexIterator.next());
        }

        if(comparison == 0) {
          // compare edge sets
          List<LabeledEdge> ownEdges = Lists.newArrayList(this.edges);
          List<LabeledEdge> otherEdges = Lists.newArrayList(other.edges);

          Collections.sort(ownEdges);
          Collections.sort(otherEdges);

          Iterator<LabeledEdge> ownEdgeIterator = ownEdges.iterator();
          Iterator<LabeledEdge> otherEdgeIterator = otherEdges.iterator();

          while(comparison == 0 && ownEdgeIterator.hasNext()) {
            comparison = ownEdgeIterator.next().compareTo(
              otherEdgeIterator.next());
          }
        }
      }
    }

    return comparison;
  }

  public boolean isIsomorphicTo(LabeledGraph other) {

    boolean isIsomorphic = false;

    if(this.compareTo(other) == 0) {
      isIsomorphic = this.getAdjacencyMatrixCode().compareTo(
        other.getAdjacencyMatrixCode()) == 0;
    }

    return isIsomorphic;
  }

  public String getAdjacencyMatrixCode() {
    List<String> vertexCodes = new ArrayList<>();

    // for each vertex
    for(LabeledVertex vertex : vertices) {

      // encode outgoing edges
      Map<LabeledVertex,List<String>> targetVertexEdgeLabels = new HashMap<>();

      for(LabeledEdge edge : vertexOutgoingEdgesMap.get(vertex)) {

        LabeledVertex targetVertex = edge.getTargetVertex();
        List<String> edgeLabels = targetVertexEdgeLabels.get(targetVertex);

        if(edgeLabels == null) {
          edgeLabels = new ArrayList<>();
          targetVertexEdgeLabels.put(targetVertex, edgeLabels);
        }

        edgeLabels.add(edge.getLabel());
      }

      String outgoingEdgesCode = getVertexConnectionsCode(
        targetVertexEdgeLabels);

      // encode incoming edges
      Map<LabeledVertex,List<String>> sourceVertexEdgeLabels = new HashMap<>();

      for(LabeledEdge edge : vertexIncomingEdgesMap.get(vertex)) {

        LabeledVertex sourceVertex = edge.getSourceVertex();
        List<String> edgeLabels = sourceVertexEdgeLabels.get(sourceVertex);

        if(edgeLabels == null) {
          edgeLabels = new ArrayList<>();
          sourceVertexEdgeLabels.put(sourceVertex, edgeLabels);
        }

        edgeLabels.add(edge.getLabel());
      }

      String incomingEdgesCode = getVertexConnectionsCode
        (sourceVertexEdgeLabels);

      vertexCodes.add(vertex.getLabel() + "(" + outgoingEdgesCode + "," +
        incomingEdgesCode + ")" );
    }

    Collections.sort(vertexCodes);

    return StringUtils.join(vertexCodes,",");
  }

  private String getVertexConnectionsCode(
    Map<LabeledVertex, List<String>> vertexEdgeLabelsMap) {
    List<String> vertexConnectionCodes = new ArrayList<>();

    for(Map.Entry<LabeledVertex,List<String>> vertexEdgeLabels :
      vertexEdgeLabelsMap.entrySet()) {

      String vertexLabel = vertexEdgeLabels.getKey().getLabel();
      List<String> edgeLabels = vertexEdgeLabels.getValue();
      Collections.sort(edgeLabels);

      // vertexLabel(edgeLabelA,..,edgeLabelZ)
      vertexConnectionCodes.add(
        vertexLabel + "(" + StringUtils.join(edgeLabels, ",") + ")");
    }

    Collections.sort(vertexConnectionCodes);

    return "(" + StringUtils.join(vertexConnectionCodes,",") + ")";
  }


}
