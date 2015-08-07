/*
 * This file is part of Gradoop.
 *
 * Gradoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gradoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gradoop.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gradoop.model.impl.operators;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.gradoop.model.EdgeData;
import org.gradoop.model.GraphData;
import org.gradoop.model.VertexData;
import org.gradoop.model.helper.FlinkConstants;
import org.gradoop.model.helper.KeySelectors;
import org.gradoop.model.impl.LogicalGraph;

public class Overlap<VD extends VertexData, ED extends EdgeData, GD extends
  GraphData> extends
  AbstractBinaryGraphToGraphOperator<VD, ED, GD> {

  @Override
  public String getName() {
    return "Combination";
  }

  @Override
  protected LogicalGraph<VD, ED, GD> executeInternal(LogicalGraph<VD, ED, GD> firstGraph,
    LogicalGraph<VD, ED, GD> secondGraph) {
    final Long newGraphID = FlinkConstants.OVERLAP_GRAPH_ID;

    Graph<Long, VD, ED> graph1 = firstGraph.getGellyGraph();
    Graph<Long, VD, ED> graph2 = secondGraph.getGellyGraph();

    // union vertex sets, group by vertex id, filter vertices where
    // the group contains two vertices and update them with the new graph id
    DataSet<Vertex<Long, VD>> newVertexSet =
      graph1.getVertices().union(graph2.getVertices())
        .groupBy(new KeySelectors.VertexKeySelector<VD>())
        .reduceGroup(new VertexGroupReducer<VD>(2L))
        .map(new VertexToGraphUpdater<VD>(newGraphID));

    DataSet<Edge<Long, ED>> newEdgeSet =
      graph1.getEdges().union(graph2.getEdges())
        .groupBy(new KeySelectors.EdgeKeySelector<ED>())
        .reduceGroup(new EdgeGroupReducer<ED>(2L))
        .map(new EdgeToGraphUpdater<ED>(newGraphID));

    return LogicalGraph.fromGraph(
      Graph.fromDataSet(newVertexSet, newEdgeSet, graph1.getContext()),
      firstGraph.getGraphDataFactory().createGraphData(newGraphID),
      firstGraph.getVertexDataFactory(), firstGraph.getEdgeDataFactory(),
      firstGraph.getGraphDataFactory());
  }
}
