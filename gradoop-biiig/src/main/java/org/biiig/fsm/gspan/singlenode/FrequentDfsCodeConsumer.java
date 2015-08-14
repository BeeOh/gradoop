package org.biiig.fsm.gspan.singlenode;

import org.biiig.fsm.gspan.common.DfsCode;

/**
 * runnable to copy globally frequent DFS codes to the worker
 *
 * Created by p3et on 27.07.15.
 */
public class FrequentDfsCodeConsumer extends AbstractRunnable {
  /**
   * constructor
   * @param worker initializing worker
   */
  public FrequentDfsCodeConsumer(GSpanWorker worker) {
    super(worker);
  }
  /**
   * clear last rounds data and copy global one by cloning elements for
   * thread safety; copy only those supported on the worker
   */
  @Override
  public void run() {
    worker.getGrowableDfsCodes().clear();

    for (DfsCode code : worker.getMaster().getGrowableDfsCodes()) {
      code = code.newChild();
      if (worker.getDfsCodeSupports().containsKey(code)) {
        worker.getGrowableDfsCodes().add(code);
      }
    }

    worker.getDfsCodeSupports().clear();
  }
}
