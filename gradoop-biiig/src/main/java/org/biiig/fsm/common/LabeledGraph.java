package org.biiig.fsm.common;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Transitional POJO representing a string-labelled graph
 *
 * Created by p3et on 25.06.15.
 */
public class LabeledGraph implements Comparable<LabeledGraph> {
  /**
   * set of vertices
   */
  private final Set<LabeledVertex> vertices = new HashSet<>();
  /**
   * set of edges
   */
  private final Set<LabeledEdge> edges = new HashSet<>();
  /**
   * outgoing vertex adjacency lists
   */
  private final Map<LabeledVertex, List<LabeledEdge>> vertexOutgoingEdgesMap =
    new HashMap<>();
  /**
   * incoming vertex adjacency lists
   */
  private final Map<LabeledVertex, List<LabeledEdge>> vertexIncomingEdgesMap =
    new HashMap<>();

  // behaviour

  /**
   * creates a new vertex in the graph
   * @param label label of the new vertex
   * @return the new vertex
   */
  public LabeledVertex newVertex(String label) {
    LabeledVertex vertex = new LabeledVertex(label);
    this.vertices.add(vertex);
    vertexOutgoingEdgesMap.put(vertex, new ArrayList<LabeledEdge>());
    vertexIncomingEdgesMap.put(vertex, new ArrayList<LabeledEdge>());
    return vertex;
  }
  /**
   * creates a new edge in the graph
   * @param source source vertex  of the new edge
   * @param label label of the new edge
   * @param target target vertex of the new edge
   * @return the new edge
   */
  public LabeledEdge newEdge(
    LabeledVertex source, String label, LabeledVertex target) {

    LabeledEdge edge = new LabeledEdge(source, label, target);
    this.edges.add(edge);
    vertexOutgoingEdgesMap.get(source).add(edge);
    vertexIncomingEdgesMap.get(target).add(edge);
    return edge;
  }

  // convenience methods

  /**
   * checks isomorphism to other graph
   * @param other other graph
   * @return true, if isomorphic
   */
  public boolean isIsomorphicTo(LabeledGraph other) {

    boolean isIsomorphic = false;

    if (this.compareTo(other) == 0) {
      isIsomorphic = this.getAdjacencyMatrixCode()
        .compareTo(other.getAdjacencyMatrixCode()) == 0;
    }

    return isIsomorphic;
  }
  /**
   * generates string representation of the adjacency matrix
   * @return string representation of the adjacency matrix
   */
  public String getAdjacencyMatrixCode() {
    List<String> vertexCodes = new ArrayList<>();

    // for each vertex
    for (LabeledVertex vertex : vertices) {

      // encode outgoing edges
      Map<LabeledVertex, List<String>> targetVertexEdgeLabels = new HashMap<>();

      for (LabeledEdge edge : vertexOutgoingEdgesMap.get(vertex)) {

        LabeledVertex targetVertex = edge.getTargetVertex();
        List<String> edgeLabels = targetVertexEdgeLabels.get(targetVertex);

        if (edgeLabels == null) {
          edgeLabels = new ArrayList<>();
          targetVertexEdgeLabels.put(targetVertex, edgeLabels);
        }

        edgeLabels.add(edge.getLabel());
      }

      String outgoingEdgesCode =
        getVertexConnectionsCode(targetVertexEdgeLabels);

      // encode incoming edges
      Map<LabeledVertex, List<String>> sourceVertexEdgeLabels = new HashMap<>();

      for (LabeledEdge edge : vertexIncomingEdgesMap.get(vertex)) {

        LabeledVertex sourceVertex = edge.getSourceVertex();
        List<String> edgeLabels = sourceVertexEdgeLabels.get(sourceVertex);

        if (edgeLabels == null) {
          edgeLabels = new ArrayList<>();
          sourceVertexEdgeLabels.put(sourceVertex, edgeLabels);
        }

        edgeLabels.add(edge.getLabel());
      }

      String incomingEdgesCode =
        getVertexConnectionsCode(sourceVertexEdgeLabels);

      vertexCodes.add(vertex.getLabel() + "(" + outgoingEdgesCode + "," +
        incomingEdgesCode + ")");
    }

    Collections.sort(vertexCodes);

    return StringUtils.join(vertexCodes, ",");
  }

  /**
   * helper method of adjacency matrix code generation
   * @param vertexEdgeLabelsMap map of vertex and connecting edge labels
   * @return a string representing vertex labels and and connecting edge labels
   */
  private String getVertexConnectionsCode(
    Map<LabeledVertex, List<String>> vertexEdgeLabelsMap) {
    List<String> vertexConnectionCodes = new ArrayList<>();

    for (Map.Entry<LabeledVertex, List<String>> vertexEdgeLabels :
      vertexEdgeLabelsMap.entrySet()) {

      String vertexLabel = vertexEdgeLabels.getKey().getLabel();
      List<String> edgeLabels = vertexEdgeLabels.getValue();
      Collections.sort(edgeLabels);

      // vertexLabel(edgeLabelA,..,edgeLabelZ)
      vertexConnectionCodes.add(
        vertexLabel + "(" + StringUtils.join(edgeLabels, ",") + ")");
    }

    Collections.sort(vertexConnectionCodes);

    return "(" + StringUtils.join(vertexConnectionCodes, ",") + ")";
  }

  // override methods

  /**
   * comparator, compares invariants but not isomorphism
   * @param other other graph
   * @return comparison result
   */
  @Override
  public int compareTo(LabeledGraph other) {
    int comparison = this.vertices.size() - other.vertices.size();

    if (comparison == 0) {
      comparison = this.edges.size() - other.edges.size();

      if (comparison == 0) {
        // compare vertex sets
        List<LabeledVertex> ownVertices = Lists.newArrayList(this.vertices);
        List<LabeledVertex> otherVertices = Lists.newArrayList(other.vertices);

        Collections.sort(ownVertices);
        Collections.sort(otherVertices);

        Iterator<LabeledVertex> ownVertexIterator = ownVertices.iterator();
        Iterator<LabeledVertex> otherVertexIterator = otherVertices.iterator();

        while (comparison == 0 && ownVertexIterator.hasNext()) {
          comparison = ownVertexIterator.next().compareTo(
            otherVertexIterator.next());
        }

        if (comparison == 0) {
          // compare edge sets
          List<LabeledEdge> ownEdges = Lists.newArrayList(this.edges);
          List<LabeledEdge> otherEdges = Lists.newArrayList(other.edges);

          Collections.sort(ownEdges);
          Collections.sort(otherEdges);

          Iterator<LabeledEdge> ownEdgeIterator = ownEdges.iterator();
          Iterator<LabeledEdge> otherEdgeIterator = otherEdges.iterator();

          while (comparison == 0 && ownEdgeIterator.hasNext()) {
            comparison = ownEdgeIterator.next().compareTo(
              otherEdgeIterator.next());
          }
        }
      }
    }

    return comparison;
  }
  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    return "<{" + StringUtils.join(vertices, ",") + "},{" +
      StringUtils.join(edges, ",") + "}>";
  }

  // getters and setters

  public Set<LabeledVertex> getVertices() {
    return vertices;
  }

  public Set<LabeledEdge> getEdges() {
    return edges;
  }
}
