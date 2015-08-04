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

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.gradoop.model.helper.FlinkConstants;
import org.gradoop.model.impl.EPFlinkEdgeData;
import org.gradoop.model.impl.EPFlinkGraphData;
import org.gradoop.model.impl.EPFlinkVertexData;
import org.gradoop.model.impl.EPGraph;

import static org.gradoop.model.impl.EPGraph.*;

public class Exclusion extends AbstractBinaryGraphToGraphOperator {
  @Override
  public String getName() {
    return "Exclusion";
  }

  @Override
  protected EPGraph executeInternal(EPGraph firstGraph, EPGraph secondGraph) {
    final Long newGraphID = FlinkConstants.EXCLUDE_GRAPH_ID;

    Graph<Long, EPFlinkVertexData, EPFlinkEdgeData> graph1 =
      firstGraph.getGellyGraph();
    Graph<Long, EPFlinkVertexData, EPFlinkEdgeData> graph2 =
      secondGraph.getGellyGraph();

    // union vertex sets, group by vertex id, filter vertices where the group
    // contains exactly one vertex which belongs to the graph, the operator is
    // called on
    DataSet<Vertex<Long, EPFlinkVertexData>> newVertexSet =
      graph1.getVertices().union(graph2.getVertices()).groupBy(VERTEX_ID)
        .reduceGroup(
          new VertexGroupReducer(1L, firstGraph.getId(), secondGraph.getId()))
        .map(new VertexToGraphUpdater(newGraphID));

    JoinFunction<Edge<Long, EPFlinkEdgeData>, Vertex<Long,
      EPFlinkVertexData>, Edge<Long, EPFlinkEdgeData>>
      joinFunc =
      new JoinFunction<Edge<Long, EPFlinkEdgeData>, Vertex<Long,
        EPFlinkVertexData>, Edge<Long, EPFlinkEdgeData>>() {
        @Override
        public Edge<Long, EPFlinkEdgeData> join(
          Edge<Long, EPFlinkEdgeData> leftTuple,
          Vertex<Long, EPFlinkVertexData> rightTuple) throws Exception {
          return leftTuple;
        }
      };

    // In exclude(), we are only interested in edges that connect vertices
    // that are in the exclusion of the vertex sets. Thus, we join the edges
    // from the left graph with the new vertex set using source and target ids.
    DataSet<Edge<Long, EPFlinkEdgeData>> newEdgeSet =
      graph1.getEdges().join(newVertexSet).where(SOURCE_VERTEX_ID)
        .equalTo(VERTEX_ID).with(joinFunc).join(newVertexSet)
        .where(TARGET_VERTEX_ID).equalTo(VERTEX_ID).with(joinFunc)
        .map(new EdgeToGraphUpdater(newGraphID));

    return EPGraph.fromGraph(
      Graph.fromDataSet(newVertexSet, newEdgeSet, graph1.getContext()),
      new EPFlinkGraphData(newGraphID, FlinkConstants.DEFAULT_GRAPH_LABEL));
  }
}