package org.biiig.fsm.gspan;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by peet on 26.06.15.
 */
public class DfsCode implements Comparable<DfsCode> {

  private final List<DfsEdge> dfsEdges = new ArrayList<>();

  public List<DfsEdge> getDfsEdges() {
    return dfsEdges;
  }

  public String toString(){
    return StringUtils.join(dfsEdges,",");
  }


  public void add(DfsEdge dfsEdge) {
    dfsEdges.add(dfsEdge);
  }

  public int positionOf(DfsEdge dfsEdge) {
    int position = -1;

    for(DfsEdge existingDfsEdge : dfsEdges) {
      if(existingDfsEdge.compareTo(dfsEdge) == 0) {
        position = dfsEdges.indexOf(existingDfsEdge);
        break;
      }
    }

    return position;
  }

  @Override
  public int compareTo(DfsCode other) {
    int comparison = 0;

    Iterator<DfsEdge> ownIterator = this.dfsEdges.iterator();
    Iterator<DfsEdge> otherIterator = other.dfsEdges.iterator();

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
}
