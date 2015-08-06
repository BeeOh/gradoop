package org.biiig.fsm.gspan.common;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

/**
 * Created by peet on 07.07.15.
 */
public class DfsCodeMapper implements Cloneable{

  private final GSpanGraph graph;
  private final ArrayList<GSpanEdge> edges = new ArrayList<>();
  private final ArrayList<GSpanVertex> vertices = new ArrayList<>();

  public DfsCodeMapper(GSpanGraph graph) {
    this.graph = graph;
  }


  public String toString() {
    return edges.toString();
    //return StringUtils.join(edges,",");
    //return String.valueOf(edges.size());
  }

  public GSpanGraph getGraph() {
    return graph;
  }

  public void add(GSpanEdge edge) {
    this.edges.add(edge);
  }

  public void add(GSpanVertex vertex) {
    this.vertices.add(vertex);
  }

  public GSpanVertex getRightmostVertex() {
    return vertices.get(getRightmostVertexPosition());
  }


  public boolean contains(GSpanVertex vertex) {

    boolean contains = false;

    for (GSpanVertex mappedVertex : vertices) {
      if(vertex == mappedVertex) {
        contains = true;
        break;
      }
    }

    return contains;
  }

  public boolean contains(GSpanEdge edge) {

    boolean contains = false;

    for (GSpanEdge mappedEdge : edges) {
      if(edge == mappedEdge) {
        contains = true;
        break;
      }
    }

    return contains;
  }


  public DfsCodeMapper clone() {
    DfsCodeMapper clone = new DfsCodeMapper(graph);
    clone.getVertices().addAll(vertices);
    clone.getEdges().addAll(edges);
    return clone;
  }

  public ArrayList<GSpanEdge> getEdges() {
    return edges;
  }

  public ArrayList<GSpanVertex> getVertices() {
    return vertices;
  }

  public Integer getRightmostVertexPosition() {
    return vertices.size()-1;
  }

  public Integer positionOf(GSpanVertex vertex) {
    return vertices.indexOf(vertex);
  }

  public Integer getCoverage() {

    List<Integer> edgeHashCodes = new LinkedList<>();

    for(GSpanEdge edge : edges) {
      edgeHashCodes.add(edge.hashCode());
    }

    Collections.sort(edgeHashCodes);

    HashCodeBuilder builder = new HashCodeBuilder();
    builder.append(edgeHashCodes);

    return builder.hashCode();
  }

  public GSpanEdge getFirstEdge() {
    return edges.get(0);
  }

}
