package org.biiig.fsm.gspan.multithreaded.runnables;

import org.biiig.fsm.gspan.common.DfsCode;
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

    for(DfsCode dfsCode : worker.getMaster().getGrowableDfsCodes()) {
      dfsCode = dfsCode.clone();
      if(worker.getDfsCodeSupporterMappersMap().containsKey(dfsCode))
      worker.getGrowableDfsCodes().add(dfsCode);
    }
  }
}
