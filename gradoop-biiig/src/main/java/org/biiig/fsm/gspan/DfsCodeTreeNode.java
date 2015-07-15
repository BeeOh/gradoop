package org.biiig.fsm.gspan;

import org.biiig.model.LabeledVertex;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by peet on 07.07.15.
 */
public class DfsCodeTreeNode implements Comparable<DfsCodeTreeNode> {

  private int level;
  private final DfsCodeTreeNode parent;
  private final LinkedList<DfsCodeTreeNode> children = new LinkedList<>();
  private final DfsCode dfsCode;
  private List<LabeledVertex> rightmostPath;

  public DfsCodeTreeNode(DfsCodeTreeNode parent, DfsCode dfsCode) {
    this.parent = parent;
    this.dfsCode = dfsCode;

    if(parent == null) {
      level = 0;
    } else {
      level = parent.level + 1;
    }
  }

  public DfsCodeTreeNode newChild(DfsCode dfsCode) {
    DfsCodeTreeNode child = new DfsCodeTreeNode(this,dfsCode);
    children.add(child);
    //Collections.sort(children);
    return child;
  }

  public boolean isRoot(){
    return parent == null;
  }

  @Override
  public int compareTo(DfsCodeTreeNode other) {
    //return this.dfsCode.compareTo(other.dfsCode);

    return 0;
  }

  public String toString(){
    StringBuffer buffer = new StringBuffer();

    for(int i=0;i<level;i++) {
      buffer.append("  ");
    }

    buffer.append(this.dfsCode.toString());
    buffer.append("\n");

    for(DfsCodeTreeNode child : children) {
      buffer.append(child.toString());
    }

    return buffer.toString();
  }

  public DfsCode getDfsCode() {
    return dfsCode;
  }

  public LinkedList<DfsCodeTreeNode> getChildren() {
    return children;
  }

  public DfsCodeTreeNode getParent() {
    return parent;
  }
}
