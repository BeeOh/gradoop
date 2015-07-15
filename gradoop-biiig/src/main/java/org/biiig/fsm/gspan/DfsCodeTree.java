package org.biiig.fsm.gspan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peet on 07.07.15.
 */
public class DfsCodeTree {

  private final DfsCodeTreeNode root;

  public DfsCodeTree() {
    root = new DfsCodeTreeNode(null, new DfsCode());
  }

  public String toString() {
    return root.toString();
  }

  public DfsCodeTreeNode getRoot() {
    return root;
  }

  public List<List<DfsCode>> getLevels() {

    List<List<DfsCode>> levels = new ArrayList<>();

    List<DfsCodeTreeNode> currentNodes = new ArrayList<>();
    currentNodes.add(root);

    while (!currentNodes.isEmpty()) {
      List<DfsCode> levelCodes = new ArrayList<>();
      List<DfsCodeTreeNode> nextNodes = new ArrayList<>();

      for(DfsCodeTreeNode currentNode : currentNodes) {
        levelCodes.add(currentNode.getDfsCode());
        nextNodes.addAll(currentNode.getChildren());
      }

      currentNodes = nextNodes;

      levels.add(levelCodes);
    }

    return levels;
  }
}
