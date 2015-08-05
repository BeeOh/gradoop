package org.gradoop.model.impl;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.flink.graph.Graph;
import org.gradoop.model.EPFlinkTest;
import org.gradoop.model.helper.Predicate;
import org.gradoop.model.store.EPGraphStore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnitParamsRunner.class)
public class EPGraphCollectionSelectionTest extends EPFlinkTest {
  private EPGraphStore graphStore;

  public EPGraphCollectionSelectionTest() {
    this.graphStore = createSocialGraph();
  }

  /* TODO: fix problems with null pointers
     empty GraphCollection if no graph matches predicate
     gellyGraph and subgraphs are both null
   */

  @Test
  @Parameters({"1 2, 1, 4, 6"})
  public void testSelectionVertexCount(String graphIds, Long expectedGraphCount,
    Long expectedVertexCount, Long expectedEdgeCount) throws Exception {
    EPGraphCollection graphColl = graphStore.getCollection();
    List<Long> ids = extractGraphIDs(graphIds);
    graphColl = graphColl.getGraphs(ids);
    graphColl = graphColl.select(new VertexCountSelectionPredicate());
    assertNotNull("graph collection was null", graphColl);
    assertEquals("wrong number of graphs", expectedGraphCount, graphColl.getGraphCount());
    assertEquals("wrong number of vertices", expectedVertexCount,
      (Long) graphColl.getGraph().getVertexCount());
    assertEquals("wrong number of edges", expectedEdgeCount,
      (Long) graphColl.getGraph().getEdgeCount());
  }

  private static class VertexCountSelectionPredicate implements
    Predicate<EPGraph> {
    @Override
    public boolean filter(EPGraph graph) throws Exception {
      Long vertexCount = graph.getVertexCount();
      return (vertexCount > 3);
    }
  }

  @Test
  @Parameters({"0 1 2, 1, 3, 4"})
  public void testSelection(String graphIds, Long expectedGraphCount,
    Long expectedVertexCount, Long expectedEdgeCount) throws Exception {
    EPGraphCollection graphColl = graphStore.getCollection();
    List<Long> ids = extractGraphIDs(graphIds);
    graphColl = graphColl.getGraphs(ids);
    graphColl = graphColl.select(new GraphPropertyPredicate());
    assertNotNull("graph collection was null", graphColl);
    assertEquals("wrong number of graphs", expectedGraphCount, graphColl.getGraphCount());
    assertEquals("wrong number of vertices", expectedVertexCount,
      (Long) graphColl.getGraph().getVertexCount());
    assertEquals("wrong number of edges", expectedEdgeCount,
      (Long) graphColl.getGraph().getEdgeCount());
  }

  private static class GraphPropertyPredicate implements Predicate<EPGraph> {
    @Override
    public boolean filter(EPGraph graph) throws Exception {
      return graph.getProperties().get("interest").equals("Databases");
    }
  }
}
