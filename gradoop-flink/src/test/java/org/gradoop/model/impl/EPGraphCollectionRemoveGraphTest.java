package org.gradoop.model.impl;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.gradoop.model.FlinkTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnitParamsRunner.class)
public class EPGraphCollectionRemoveGraphTest extends FlinkTest {

  private EPGMDatabase<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
    graphStore;

  public EPGraphCollectionRemoveGraphTest() {
    this.graphStore = createSocialGraph();
  }

  @Test
  @Parameters({
    "0 1 2, 0, 2, 1, 3, 4", "2 3, 2, 2, 1, 3, 4", "0 1 2, 3, 5, 3, 6, 10"
  })
  public void testRemoveGraph(String collIDs, Long firstGraph, Long secondGraph,
    long expectedCollSize, long expectedVertexCount,
    long expectedEdgeCount) throws Exception {
    GraphCollection<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
      graphColl = graphStore.getCollection();
    GraphCollection<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
      collection = graphColl.getGraphs(extractGraphIDs(collIDs));
    collection.removeGraph(firstGraph);
    collection.removeGraph(secondGraph);

    assertNotNull("graph collection is null", collection);
    assertEquals("wrong number of graphs", expectedCollSize, collection.size());
    assertEquals("vertex set has the wrong size", expectedVertexCount,
      collection.getTotalVertexCount());
    assertEquals("edge set has the wrong size", expectedEdgeCount,
      collection.getTotalEdgeCount());
  }
}
