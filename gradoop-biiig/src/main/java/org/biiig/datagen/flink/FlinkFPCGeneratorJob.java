package org.biiig.datagen.flink;

/**
 * Created by peet on 19.06.15.
 */
public class FlinkFPCGeneratorJob {
  /**
   * every job is for one of 10 graph types
   */
  private int graphType = 0;
  /**
   * determine the number clones to generate
   */
  private int collectionScaleFactor = 0;
  /**
   * determines the size of generated graphs
   */
  private int graphScaleFactor = 0;

  public int getGraphType() {
    return graphType;
  }

  public void setGraphType(int graphType) {
    this.graphType = graphType;
  }

  public int getCollectionScaleFactor() {
    return collectionScaleFactor;
  }

  public void setCollectionScaleFactor(int collectionScaleFactor) {
    this.collectionScaleFactor = collectionScaleFactor;
  }

  public int getGraphScaleFactor() {
    return graphScaleFactor;
  }

  public void setGraphScaleFactor(int graphScaleFactor) {
    this.graphScaleFactor = graphScaleFactor;
  }
}
