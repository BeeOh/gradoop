package org.biiig.fsm.common;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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
public class LabeledGraph {
  /**
   * set of vertices
   */
  private Set<LabeledVertex> vertices = new HashSet<>();
  /**
   * set of edges
   */
  private Set<LabeledEdge> edges = new HashSet<>();

  // behaviour

  /**
   * creates a new vertex in the graph
   * @param label label of the new vertex
   * @return the new vertex
   */
  public LabeledVertex newVertex(String label) {
    LabeledVertex vertex = new LabeledVertex(label);
    this.vertices.add(vertex);
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

    if (this.compareInvariantsTo(other) == 0) {
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

    Map<LabeledVertex, Pair<Map<LabeledVertex, List<String>>,
          Map<LabeledVertex, List<String>>>> index = getAdajcencyIndex();

    List<String> vertexCodes = new ArrayList<>();

    // for each vertex
    for (LabeledVertex vertex : vertices) {

      Pair<Map<LabeledVertex, List<String>>, Map<LabeledVertex, List<String>>>
        indexEntry = index.get(vertex);

      // if vertex is not connected by any edge
      if (indexEntry == null) {
        // add only vertex label
        vertexCodes.add(vertex.getLabel());
      } else {

        // encode outgoing edge and target vertex labels
        String outgoingEdgesCode =
          encodeVertexEdgeLabels(indexEntry.getKey());

        // encode incoming edge and source vertex labels
        String incomingEdgesCode =
          encodeVertexEdgeLabels(indexEntry.getValue());

        // combine encoded edge labels
        vertexCodes.add(vertex.getLabel() + "(" + outgoingEdgesCode + "," +
          incomingEdgesCode + ")");
      }
    }

    Collections.sort(vertexCodes);

    return StringUtils.join(vertexCodes, ",");
  }
  /**
   helper method of adjacency matrix code generation;
   * @return am map with vertices as keys and a pair as index
   * the pairs key is an outgoing vertex map with target vertex as key and all
   * connecting edge labels as string list, the pairs value is an incoming
   * vertex map with source vertex as key and all connecting edge labels as
   * string list
   */
  private Map<LabeledVertex, Pair<Map<LabeledVertex, List<String>>,
    Map<LabeledVertex, List<String>>>> getAdajcencyIndex() {
    Map<LabeledVertex, Pair<Map<LabeledVertex, List<String>>,
      Map<LabeledVertex, List<String>>>> index = new HashMap<>();

    for (LabeledEdge edge : edges) {

      // source index entry
      LabeledVertex source = edge.getSourceVertex();
      LabeledVertex target = edge.getTargetVertex();

      Map<LabeledVertex, List<String>> outgoingEdgeLabelsMap =
        getIndexEntry(index, source).getKey();

      Map<LabeledVertex, List<String>> incomingEdgeLabelsMap =
        getIndexEntry(index, target).getValue();

      addEdgeAndVertexLabel(outgoingEdgeLabelsMap, target, edge);
      addEdgeAndVertexLabel(incomingEdgeLabelsMap, source, edge);
    }
    return index;
  }
  /**
   * helper method of adjacency matrix code generation;
   * add an vertex as key and the label of tha label of an edge to the value
   * list of the input map; if the vertex is not contained in the key set, a
   * new string list is created
   * @param vertexEdgeLabelsMap input map
   * @param vertex key vertex
   * @param edge edge to extract the label from
   */
  private void addEdgeAndVertexLabel(
    Map<LabeledVertex, List<String>> vertexEdgeLabelsMap, LabeledVertex vertex,
    LabeledEdge edge) {
    List<String> outgoingEdgeLabels = vertexEdgeLabelsMap.get(vertex);

    if (outgoingEdgeLabels == null) {
      outgoingEdgeLabels = new ArrayList<>();
      vertexEdgeLabelsMap.put(vertex, outgoingEdgeLabels);
    }

    outgoingEdgeLabels.add(edge.getLabel());
  }
  /**
   * helper method of adjacency matrix code generation;
   * get the value of a given map (index) for a given vertex; create a new
   * entry if the vertex is not contained in the key set
   * @param index input map
   * @param vertex key vertex
   * @return new or existing index entry
   */
  private Pair<Map<LabeledVertex, List<String>>, Map<LabeledVertex,
    List<String>>> getIndexEntry(
    Map<LabeledVertex, Pair<Map<LabeledVertex, List<String>>,
      Map<LabeledVertex, List<String>>>> index,
    LabeledVertex vertex) {

    Pair<Map<LabeledVertex, List<String>>, Map<LabeledVertex, List<String>>>
      indexEntry = index.get(vertex);

    if (indexEntry == null) {
      indexEntry = new ImmutablePair(new HashMap<>(), new HashMap<>());
      index.put(vertex, indexEntry);
    }

    return indexEntry;
  }
  /**
   * helper method of adjacency matrix code generation
   * @param vertexEdgeLabelsMap map of vertex and connecting edge labels
   * @return a string representing vertex labels and and connecting edge labels
   */
  private String encodeVertexEdgeLabels(
    Map<LabeledVertex, List<String>> vertexEdgeLabelsMap) {

    List<String> vertexConnectionCodes = new ArrayList<>();
    String code;

    if (vertexEdgeLabelsMap.isEmpty()) {
      code = "()";
    } else {

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

      code = "(" + StringUtils.join(vertexConnectionCodes, ",") + ")";
    }

    return code;
  }

  // override methods

  /**
   * comparator, compares invariants but not isomorphism
   * @param other other graph
   * @return comparison result
   */
  public int compareInvariantsTo(LabeledGraph other) {
    int comparison = this.vertices.size() - other.vertices.size();

    if (comparison == 0) {
      comparison = this.edges.size() - other.edges.size();

      if (comparison == 0) {

        LabeledVertexComparator vertexComparator =
          new LabeledVertexComparator();

        // compare vertex sets
        List<LabeledVertex> ownVertices = Lists.newArrayList(this.vertices);
        List<LabeledVertex> otherVertices = Lists.newArrayList(other.vertices);

        Collections.sort(ownVertices, vertexComparator);
        Collections.sort(otherVertices, vertexComparator);

        Iterator<LabeledVertex> ownVertexIterator = ownVertices.iterator();
        Iterator<LabeledVertex> otherVertexIterator = otherVertices.iterator();

        while (comparison == 0 && ownVertexIterator.hasNext()) {
          comparison = vertexComparator.compare(
            ownVertexIterator.next(), otherVertexIterator.next());
        }

        if (comparison == 0) {
          LabeledEdgeComparator edgeComparator = new LabeledEdgeComparator();

          // compare edge sets
          List<LabeledEdge> ownEdges = Lists.newArrayList(this.edges);
          List<LabeledEdge> otherEdges = Lists.newArrayList(other.edges);

          Collections.sort(ownEdges, edgeComparator);
          Collections.sort(otherEdges, edgeComparator);

          Iterator<LabeledEdge> ownEdgeIterator = ownEdges.iterator();
          Iterator<LabeledEdge> otherEdgeIterator = otherEdges.iterator();

          while (comparison == 0 && ownEdgeIterator.hasNext()) {
            comparison = edgeComparator.compare(
              ownEdgeIterator.next(), otherEdgeIterator.next());
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
