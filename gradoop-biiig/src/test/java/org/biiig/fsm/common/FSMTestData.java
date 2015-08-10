package org.biiig.fsm.common;

import org.biiig.fsm.common.LabeledGraph;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by peet on 10.08.15.
 */
public class FSMTestData {
  private final Collection<LabeledGraph> searchSpace = new ArrayList<>();
  private final Collection<LabeledGraph> expectedResult = new ArrayList<>();
  private final String description;
  private final float threshold;

  public FSMTestData(String description, float threshold) {
    this.description = description;
    this.threshold = threshold;
  }

  public LabeledGraph newSearchGraph() {
    LabeledGraph searchGraph = new LabeledGraph();
    searchSpace.add(searchGraph);
    return searchGraph;
  }

  public LabeledGraph newExpectation() {
    LabeledGraph expectation = new LabeledGraph();
    expectedResult.add(expectation);
    return expectation;
  }

  public Collection<LabeledGraph> getExpectedResult() {
    return expectedResult;
  }

  public String getDescription() {
    return description;
  }

  public Collection<LabeledGraph> getSearchSpace() {
    return searchSpace;
  }

  public float getThreshold() {
    return threshold;
  }
}
