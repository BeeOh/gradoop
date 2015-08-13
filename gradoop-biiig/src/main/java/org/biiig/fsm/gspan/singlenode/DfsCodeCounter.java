package org.biiig.fsm.gspan.singlenode;

import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.SearchSpaceItem;

/**
 * runnable to count DFS code support on a the worker
 *
 * Created by p3et on 12.08.15.
 */
public class DfsCodeCounter extends AbstractRunnable {
  /**
   * constructor
   * @param worker calling worker
   */
  public DfsCodeCounter(GSpanWorker worker) {
    super(worker);
  }
  /**
   * count local DFS code support
   */
  @Override
  public void run() {
    for (SearchSpaceItem item : worker.getSearchSpace()) {
      for (DfsCode code : item.getMinimumDfsCodes()) {
        Integer oldSupport = worker.getDfsCodeSupports().get(code);
        worker.getDfsCodeSupports().put(
          code, oldSupport == null ? 1 : oldSupport + 1);
      }
    }
  }
}
