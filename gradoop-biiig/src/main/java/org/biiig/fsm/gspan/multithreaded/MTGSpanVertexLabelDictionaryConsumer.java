package org.biiig.fsm.gspan.multithreaded;

/**
 * Created by peet on 29.07.15.
 */
public class MTGSpanVertexLabelDictionaryConsumer extends
  MTGSpanAbstractRunnable {
  public MTGSpanVertexLabelDictionaryConsumer(MTGSpanWorker worker) {
    super(worker);
  }

  @Override
  public void run() {
    worker.getVertexLabelDictionary().clear();
    worker.getVertexLabelDictionary().putAll(
      worker.getMaster().getVertexLabelDictionary());
  }
}
