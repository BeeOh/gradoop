package org.biiig.fsm.gspan.multithreaded;

/**
 * Created by peet on 15.07.15.
 */
public abstract  class MTGSSpanRunnable implements Runnable {
  /**
   * reference to calling worker
   */
  protected final MTGSpanWorker worker;

  /**
   * constructor
   * @param worker calling worker
   */
  public MTGSSpanRunnable(MTGSpanWorker worker) {
    this.worker = worker;
  }
}
