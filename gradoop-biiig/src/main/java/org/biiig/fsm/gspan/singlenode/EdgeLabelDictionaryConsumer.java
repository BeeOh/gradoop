package org.biiig.fsm.gspan.singlenode;

/**
 *  runnable to copy the global edge label dictionary to the worker
 *
 * Created by p3et on 29.07.15.
 */
public class EdgeLabelDictionaryConsumer extends AbstractRunnable {
  /**
   * constructor
   * @param worker initializing worker
   */
  public EdgeLabelDictionaryConsumer(GSpanWorker worker) {
    super(worker);
  }
  /**
   * clears local dictionary and copy the global one
   */
  @Override
  public void run() {
    worker.getEdgeLabelDictionary().clear();
    worker.getEdgeLabelDictionary().putAll(
      worker.getMaster().getEdgeLabelDictionary());
  }
}
