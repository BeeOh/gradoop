package org.gradoop.model.impl;

import org.gradoop.model.EPFlinkTest;
import org.gradoop.model.store.EPGraphStore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by niklas on 30.07.15.
 */
public class EPGraphCollectionAddGraphTest extends EPFlinkTest {

  private EPGraphStore graphStore;

  public EPGraphCollectionAddGraphTest() {
    this.graphStore = createSocialGraph();
  }

  @Test
  public void testAddGraph() throws Exception {
    EPGraphCollection graphColl = graphStore.getCollection();
    EPGraphCollection newCollection = new EPGraphCollection(null, null, env);
    EPGraph graph0 = graphColl.getGraph(0l);
    EPGraph graph2 = graphColl.getGraph(2l);
    newCollection.addGraph(graph0);
    newCollection.addGraph(graph2);

    assertNotNull("graph collection is null", newCollection);
    assertEquals("wrong number of graphs", 2, newCollection.size());
    assertEquals("vertex set has the wrong size", 5,
      newCollection.getGraph().getVertices().size());
    assertEquals("edge set has the wrong size", 8,
      newCollection.getGraph().getEdges().size());
  }
}
