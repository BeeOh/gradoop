package org.biiig.fsm.gspan.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by peet on 26.06.15.
 */
public class DfsCode implements Comparable<DfsCode>, Cloneable {

  private final List<DfsEdge> dfsEdges = new ArrayList<>();

  public List<DfsEdge> getDfsEdges() {
    return dfsEdges;
  }


  public void add(DfsEdge dfsEdge) {
    dfsEdges.add(dfsEdge);
  }

  public DfsEdge getLastDfsEdge() {
    return dfsEdges.get(dfsEdges.size()-1);
  }

  @Override
  public DfsCode clone() {
    DfsCode clone = new DfsCode();
    clone.getDfsEdges().addAll(dfsEdges);
    return clone;
  }

  @Override
  public int compareTo(DfsCode other) {
    int comparison = 0;

    Iterator<DfsEdge> ownIterator = this.dfsEdges.iterator();
    Iterator<DfsEdge> otherIterator = other.dfsEdges.iterator();

    while (comparison == 0 && ownIterator.hasNext() &&
      otherIterator.hasNext()) {
      comparison = ownIterator.next().compareTo(otherIterator.next());
    }

    if (comparison == 0) {
      if (ownIterator.hasNext()) {
        comparison = 1;
      } else if (otherIterator.hasNext()) {
        comparison = -1;
      }
    }
    return comparison;
  }

  @Override
  public boolean equals(Object other){
    boolean equals = false;

    if (other instanceof DfsCode) {
      DfsCode otherDfsCode = (DfsCode) other;
      equals = this.dfsEdges.equals(otherDfsCode.dfsEdges);
    }

    return equals;
  }

  @Override
  public int hashCode(){
    HashCodeBuilder builder = new HashCodeBuilder();

    for (DfsEdge edge : dfsEdges) {
      builder.append(edge.hashCode());
    }

    return builder.hashCode();
  }

  @Override
  public String toString(){
    return "[" + StringUtils.join(dfsEdges,",") +"]";
  }








}
