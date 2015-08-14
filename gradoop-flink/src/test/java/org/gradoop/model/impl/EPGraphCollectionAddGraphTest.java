package org.gradoop.model.impl;

import org.gradoop.model.FlinkTest;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by niklas on 30.07.15.
 */
public class EPGraphCollectionAddGraphTest extends FlinkTest {

  private EPGMDatabase<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
    graphStore;

  public EPGraphCollectionAddGraphTest() {
    this.graphStore = createSocialGraph();
  }

  @Test
  public void testAddGraph() throws Exception {
    GraphCollection<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
      graphColl = graphStore.getCollection();
    GraphCollection<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
      newCollection =
      new GraphCollection<>(null, null, graphColl.getVertexDataFactory(),
        graphColl.getEdgeDataFactory(), graphColl.getGraphDataFactory(), env);
    LogicalGraph<DefaultVertexData, DefaultEdgeData, DefaultGraphData> graph0 =
      graphColl.getGraph(0l);
    LogicalGraph<DefaultVertexData, DefaultEdgeData, DefaultGraphData> graph2 =
      graphColl.getGraph(2l);
    newCollection.addGraph(graph0);
    newCollection.addGraph(graph2);

    assertNotNull("graph collection is null", newCollection);
    assertEquals("wrong number of graphs", 2, newCollection.size());
    assertEquals("vertex set has the wrong size", 5,
      newCollection.getTotalVertexCount());
    assertEquals("edge set has the wrong size", 8,
      newCollection.getTotalEdgeCount());
  }
}
