package org.biiig.fsm.gspan;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.biiig.model.LabeledEdge;
import org.biiig.model.LabeledGraph;
import scala.Int;

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

  public boolean isValidForGrowth(GSpanEdge edge) {
    //System.out.println(edge + " ? " + edges);

    // not already mapped and lexicographically greater or equal
    return !contains(edge) && edge.compareTo(edges.get(0)) >= 0;
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

    //return vertices.contains(vertex);
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

    //return vertices.contains(vertex);
  }

  public GSpanVertex getVertex(Integer position) {
    return vertices.get(position);
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
}
