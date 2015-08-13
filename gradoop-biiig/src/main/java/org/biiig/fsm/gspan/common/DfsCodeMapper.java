package org.biiig.fsm.gspan.common;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by p3et on 07.07.15.
 *
 * fat model of the mapping between DFS codes and instances in graphs, i.e.,
 * discovery positions (times) of vertices and correspondences in between DFS
 * edges and GSpan graph edges
 */
public class DfsCodeMapper implements Cloneable {
  /**
   * graph embedding a DFS code instance
   */
  private final GSpanGraph graph;
  /**
   * DFS code
   */
  private final DfsCode dfsCode;
  /**
   * list of references to vertices of the graph where the index is the
   * discovery position (time); index is the actual mapping information
   */
  private final ArrayList<GSpanVertex> mappedVertices = new ArrayList<>();
  /**
   * list of references to edges of the graph where the index is the
   * discovery position (time); index is the actual mapping information
   */
  private final ArrayList<GSpanEdge> mappedEdges = new ArrayList<>();

  // behaviour

  /**
   * constructor
   * @param dfsCode DFS code
   * @param graph the graph embedding a DFS code instance
   */
  public DfsCodeMapper(DfsCode dfsCode, GSpanGraph graph) {
    this.dfsCode = dfsCode;
    this.graph = graph;
  }
  /**
   * adds a new vertex to the mapping with index i_max + 1
   * @param vertex added vertex
   */
  public void map(GSpanVertex vertex) {
    this.mappedVertices.add(vertex);
  }
  /**
   * adds a new DFS edge <=> GSpan edge mapping
   * @param dfsEdge DFS edge
   * @param edge GSpan edge
   */
  public void map(DfsEdge dfsEdge, GSpanEdge edge) {
    dfsCode.add(dfsEdge);
    mappedEdges.add(edge);
  }

  // convenience methods

  /**
   * checks, if a vertex is already mapped
   * @param vertex vertex to check containment
   * @return true, if vertex is mapped
   */
  public boolean contains(GSpanVertex vertex) {
    return mappedVertices.contains(vertex);
  }
  /**
   * checks, if an edge is already mapped
   * @param edge edge to check containment
   * @return true, if edge is mapped
   */
  public boolean contains(GSpanEdge edge) {
    return mappedEdges.contains(edge);
  }
  /**
   * generates a hash code aggregating all the graphs already mapped edges
   * @return hash code
   */
  public Integer getCoverage() {

    List<Integer> edgeHashCodes = new LinkedList<>();

    for (GSpanEdge edge : mappedEdges) {
      edgeHashCodes.add(edge.hashCode());
    }

    Collections.sort(edgeHashCodes);

    HashCodeBuilder builder = new HashCodeBuilder();
    builder.append(edgeHashCodes);

    return builder.hashCode();
  }
  /**
   * returns the first mapped edge
   * @return first mapped edge
   */
  public GSpanEdge getFirstMappedEdge() {
    return mappedEdges.get(0);
  }

  /**
   * returns the vertex mapped to the rightmost position of the DFS code
   * @return rightmost vertex
   */
  public GSpanVertex getRightmostVertex() {
    return mappedVertices.get(mappedVertices.size() - 1);
  }
  /**
   * returns the mapped discovery position (time) of a vertex in a DFS code
   * @param vertex affected vertex
   * @return discovery position (time)
   */
  public Integer positionOf(GSpanVertex vertex) {
    return mappedVertices.indexOf(vertex);
  }

  // override methods

  /**
   * clone method
   * @return a clone
   */
  @Override
  public DfsCodeMapper clone() {
    DfsCodeMapper clone = new DfsCodeMapper(dfsCode.clone(), graph);
    clone.getMappedVertices().addAll(mappedVertices);
    clone.getMappedEdges().addAll(mappedEdges);
    return clone;
  }
  /**
   * to string method
   * @return string representation
   */
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append("--- Mapper ---");

    Iterator<DfsEdge> dfsEdgeIterator = dfsCode.getDfsEdges().iterator();

    for (GSpanEdge edge : mappedEdges) {
      buffer.append("\n" + dfsEdgeIterator.next() + " : " + edge.hashCode());
    }

    return buffer.toString();
  }

  // getters and setters

  public GSpanGraph getGraph() {
    return graph;
  }

  public ArrayList<GSpanVertex> getMappedVertices() {
    return mappedVertices;
  }

  public ArrayList<GSpanEdge> getMappedEdges() {
    return mappedEdges;
  }

  public DfsCode getDfsCode() {
    return dfsCode;
  }
}
