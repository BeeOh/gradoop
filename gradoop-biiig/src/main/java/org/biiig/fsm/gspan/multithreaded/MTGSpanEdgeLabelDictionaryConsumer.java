package org.biiig.fsm.gspan.multithreaded;

/**
 * Created by peet on 29.07.15.
 */
public class MTGSpanEdgeLabelDictionaryConsumer extends MTGSpanRunnable {
  public MTGSpanEdgeLabelDictionaryConsumer(MTGSpanWorker worker) {
    super(worker);
  }

  @Override
  public void run() {
    worker.getEdgeLabelDictionary().clear();
    worker.getEdgeLabelDictionary().putAll(
      worker.getMaster().getEdgeLabelDictionary());
  }
}
