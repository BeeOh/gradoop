package org.biiig.fsm.gspan.singlenode;

/**
 * runnable to copy the global vertex label dictionary to the worker
 *
 * Created by p3et on 29.07.15.
 */
public class VertexLabelDictionaryConsumer extends AbstractRunnable {
  /**
   * constructor
   * @param worker initializing worker
   */
  public VertexLabelDictionaryConsumer(GSpanWorker worker) {
    super(worker);
  }
  /**
   * clear local dictionary and copy the global one
   */
  @Override
  public void run() {
    worker.getVertexLabelDictionary().clear();
    worker.getVertexLabelDictionary().putAll(
      worker.getMaster().getVertexLabelDictionary());
  }
}
