package org.biiig.fsm.gspan.multithreaded.runnables;

import org.biiig.fsm.gspan.multithreaded.MTGSpanWorker;

/**
 * Created by peet on 27.07.15.
 */
public class MTGSpanFrequentDfsCodeConsumer extends MTGSpanAbstractRunnable {
  public MTGSpanFrequentDfsCodeConsumer(MTGSpanWorker worker) {
    super(worker);
  }

  @Override
  public void run() {
    worker.getGrowableDfsCodes().clear();
    worker.getGrowableDfsCodes().addAll(
      worker.getMaster().getGrowableDfsCodes());
  }
}
