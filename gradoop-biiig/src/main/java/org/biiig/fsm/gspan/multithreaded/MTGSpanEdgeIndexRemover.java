package org.biiig.fsm.gspan.multithreaded;

import org.biiig.fsm.gspan.common.EdgePattern;

import java.util.HashSet;

/**
 * Created by peet on 16.07.15.
 */
public class MTGSpanEdgeIndexRemover extends MTGSSpanRunnable {

  /**
   * set of edge patterns to remove from index
   */
  private final HashSet<EdgePattern> edgePatterns;

  /**
   * constructor
   * @param worker the calling worker
   * @param edgePatterns set of edge patterns to remove from index
   */
  public MTGSpanEdgeIndexRemover(MTGSpanWorker worker,
    HashSet<EdgePattern> edgePatterns) {
    super(worker);
    this.edgePatterns = edgePatterns;
  }

  /**
   * remove edge patterns from index
   */
  @Override
  public void run() {
    for(EdgePattern edgePattern : edgePatterns) {
      worker.getEdgePatternIndex().remove(edgePattern);
    }
  }
}
