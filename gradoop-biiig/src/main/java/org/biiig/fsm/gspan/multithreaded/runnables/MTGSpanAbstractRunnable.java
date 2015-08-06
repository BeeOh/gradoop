package org.biiig.fsm.gspan.multithreaded.runnables;

import org.biiig.fsm.gspan.multithreaded.MTGSpanWorker;

/**
 * Created by peet on 15.07.15.
 */
public abstract  class MTGSpanAbstractRunnable implements Runnable {
  /**
   * reference to calling worker
   */
  protected final MTGSpanWorker worker;

  /**
   * constructor
   * @param worker calling worker
   */
  public MTGSpanAbstractRunnable(MTGSpanWorker worker) {
    this.worker = worker;
  }
}
