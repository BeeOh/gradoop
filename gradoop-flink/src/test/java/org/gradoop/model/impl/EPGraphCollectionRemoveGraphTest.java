package org.gradoop.model.impl;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.gradoop.model.EPFlinkTest;
import org.gradoop.model.store.EPGraphStore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by niklas on 30.07.15.
 */

@RunWith(JUnitParamsRunner.class)
public class EPGraphCollectionRemoveGraphTest extends EPFlinkTest {

  private EPGraphStore graphStore;

  public EPGraphCollectionRemoveGraphTest() {
    this.graphStore = createSocialGraph();
  }

  @Test
  @Parameters({"0, 2, 2, 4, 7", "2, 2, 3, 7, 11", "3, 5, 3, 6, 10"})
  public void testRemoveGraph(Long firstGraph, Long secondGraph, long
    expectedCollSize,
    long expectedVertexCount, long expectedEdgeCount) throws Exception {
    EPGraphCollection graphColl = graphStore.getCollection();
    graphColl.removeGraph(firstGraph);
    graphColl.removeGraph(secondGraph);

    assertNotNull("graph collection is null", graphColl);
    assertEquals("wrong number of graphs", expectedCollSize, graphColl.size());
    assertEquals("vertex set has the wrong size", expectedVertexCount,
      graphColl.getGraph().getVertices().size());
    assertEquals("edge set has the wrong size", expectedEdgeCount,
      graphColl.getGraph().getEdges().size());
  }
}
