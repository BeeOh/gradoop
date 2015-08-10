package org.biiig.fsm.gspan.singlenode;

/**
 * Created by peet on 15.07.15.
 */
public abstract  class AbstractRunnable implements Runnable {
  /**
   * reference to calling worker
   */
  protected final GSpanWorker worker;

  /**
   * constructor
   * @param worker calling worker
   */
  public AbstractRunnable(GSpanWorker worker) {
    this.worker = worker;
  }
}
