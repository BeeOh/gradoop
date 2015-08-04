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
  private final Map<LabeledVertex,List<LabeledEdge>>
    vertexOutgoingEdges = new HashMap<>();
  private final Map<LabeledVertex,List<LabeledEdge>>
    vertexIncomingEdges = new HashMap<>();


  public LabeledVertex newVertex(String label) {
    LabeledVertex vertex = new LabeledVertex(label);
    this.vertices.add(vertex);
    vertexOutgoingEdges.put(vertex,new ArrayList<LabeledEdge>());
    vertexIncomingEdges.put(vertex,new ArrayList<LabeledEdge>());
    return vertex;
  }

  public LabeledEdge newEdge(LabeledVertex sourceVertex, String label,
    LabeledVertex targetVertex) {
    LabeledEdge edge = new LabeledEdge(sourceVertex,label,targetVertex);
    this.edges.add(edge);
    vertexOutgoingEdges.get(sourceVertex).add(edge);
    vertexIncomingEdges.get(targetVertex).add(edge);
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

  public List<LabeledEdge> getEdges(LabeledVertex vertex) {
    List<LabeledEdge> vertexEdges = new LinkedList<>();
    vertexEdges.addAll(vertexOutgoingEdges.get(vertex));
    vertexEdges.addAll(vertexIncomingEdges.get(vertex));
    return vertexEdges;
  }

  public void remove(LabeledVertex vertex) {
    vertexIncomingEdges.remove(vertex);
    vertexOutgoingEdges.remove(vertex);
    vertices.remove(vertex);
  }

  @Override
  public int compareTo(LabeledGraph other) {
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

        // TODO : isomorphism check if invariant comparison results to 0
      }
    }

    return comparison;
  }
}
