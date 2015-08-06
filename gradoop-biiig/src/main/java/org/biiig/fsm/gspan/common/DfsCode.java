package org.biiig.fsm.gspan.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by peet on 26.06.15.
 */
public class DfsCode implements Comparable<DfsCode>, Cloneable {

  private final ArrayList<DfsEdge> edges = new ArrayList<>();

  public List<DfsEdge> getEdges() {
    return edges;
  }

  public String toString(){
    return "[" + StringUtils.join(edges,",") +"]";
  }


  public void add(DfsEdge dfsEdge) {
    edges.add(dfsEdge);
  }

  public int positionOf(DfsEdge dfsEdge) {
    int position = -1;

    for(DfsEdge existingDfsEdge : edges) {
      if(existingDfsEdge.compareTo(dfsEdge) == 0) {
        position = edges.indexOf(existingDfsEdge);
        break;
      }
    }

    return position;
  }

  @Override
  public int compareTo(DfsCode other) {
    int comparison = 0;

    Iterator<DfsEdge> ownIterator = this.edges.iterator();
    Iterator<DfsEdge> otherIterator = other.edges.iterator();

    while(comparison == 0 && ownIterator.hasNext() && otherIterator.hasNext()) {
      comparison = ownIterator.next().compareTo(otherIterator.next());
    }

    if(comparison == 0) {
      if(ownIterator.hasNext()) {
        comparison = 1;
      } else if (otherIterator.hasNext()) {
        comparison = -1;
      }
    }

    return comparison;
  }

  public Collection<Integer> getRightmostTailPositions() {
    Collection<Integer> rightmostTailPositions = new ArrayList<>();

    DfsEdge lastEdge = edges.get(edges.size()-1);
    int lastFromPosition = lastEdge.getFromPosition();
    rightmostTailPositions.add(lastFromPosition);

    for(int index = edges.size()-2; index >= 0; index--) {
      DfsEdge edge = edges.get(index);
      if (edge.getToPosition() == lastFromPosition){
        lastFromPosition = edge.getFromPosition();
        rightmostTailPositions.add(lastFromPosition);
      }
    }

    return rightmostTailPositions;
  }

  public DfsCode clone() {
    DfsCode clone = new DfsCode();
    clone.getEdges().addAll(edges);
    return clone;
  }

  @Override
  public int hashCode(){
    HashCodeBuilder builder = new HashCodeBuilder();

    for (DfsEdge edge : edges) {
      builder.append(edge.hashCode());
    }

    return builder.hashCode();
  }

  @Override
  public boolean equals(Object other){
    boolean equals = false;

    if (other instanceof DfsCode) {
      DfsCode otherDfsCode = (DfsCode) other;
      equals = this.edges.equals(otherDfsCode.edges);
    }

    return equals;
  }

  public int getRightmostVertexPosition() {
    return edges.get(edges.size() - 1).getToPosition();
  }
}
