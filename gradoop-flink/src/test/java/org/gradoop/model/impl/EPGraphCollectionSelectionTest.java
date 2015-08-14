package org.gradoop.model.impl;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.gradoop.model.FlinkTest;
import org.gradoop.model.helper.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnitParamsRunner.class)
public class EPGraphCollectionSelectionTest extends FlinkTest {
  private EPGMDatabase<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
    graphStore;

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
    GraphCollection<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
      graphColl = graphStore.getCollection();
    List<Long> ids = extractGraphIDs(graphIds);
    graphColl = graphColl.getGraphs(ids);
    graphColl = graphColl.select(new VertexCountSelectionPredicate());
    assertNotNull("graph collection was null", graphColl);
    assertEquals("wrong number of graphs", expectedGraphCount,
      (Long) graphColl.getGraphCount());
    assertEquals("wrong number of vertices", expectedVertexCount,
      (Long) graphColl.getTotalVertexCount());
    assertEquals("wrong number of edges", expectedEdgeCount,
      (Long) graphColl.getTotalEdgeCount());
  }

  private static class VertexCountSelectionPredicate implements
    Predicate<LogicalGraph<DefaultVertexData, DefaultEdgeData,
      DefaultGraphData>> {
    @Override
    public boolean filter(LogicalGraph graph) throws Exception {
      Long vertexCount = graph.getVertexCount();
      return (vertexCount > 3);
    }
  }

  @Test
  @Parameters({"0 1 2, 1, 3, 4"})
  public void testSelection(String graphIds, Long expectedGraphCount,
    Long expectedVertexCount, Long expectedEdgeCount) throws Exception {
    GraphCollection<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
      graphColl = graphStore.getCollection();
    List<Long> ids = extractGraphIDs(graphIds);
    graphColl = graphColl.getGraphs(ids);
    graphColl = graphColl.select(new GraphPropertyPredicate());
    assertNotNull("graph collection was null", graphColl);
    assertEquals("wrong number of graphs", expectedGraphCount,
      (Long) graphColl.getGraphCount());
    assertEquals("wrong number of vertices", expectedVertexCount,
      (Long) graphColl.getTotalVertexCount());
    assertEquals("wrong number of edges", expectedEdgeCount,
      (Long) graphColl.getTotalEdgeCount());
  }

  private static class GraphPropertyPredicate implements
    Predicate<LogicalGraph<DefaultVertexData, DefaultEdgeData,
      DefaultGraphData>> {
    @Override
    public boolean filter(LogicalGraph graph) throws Exception {
      return graph.getProperties().get("interest").equals("Databases");
    }
  }
}
