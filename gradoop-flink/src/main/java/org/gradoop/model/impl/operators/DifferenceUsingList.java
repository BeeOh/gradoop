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

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.graph.Vertex;
import org.gradoop.model.EdgeData;
import org.gradoop.model.GraphData;
import org.gradoop.model.VertexData;
import org.gradoop.model.impl.Subgraph;

import java.util.List;

/**
 * Returns a collection with all logical graphs that are contained in the
 * first input collection but not in the second.
 * Graph equality is based on their respective identifiers.
 * <p>
 * This operator implementation requires that a list of subgraph identifiers
 * in the resulting graph collections fits into the workers main memory.
 *
 * @param <VD> vertex data type
 * @param <ED> edge data type
 * @param <GD> graph data type
 */
public class DifferenceUsingList<VD extends VertexData, ED extends EdgeData,
  GD extends GraphData> extends
  Difference<VD, ED, GD> {

  /**
   * Computes the resulting vertices by collecting a list of resulting
   * subgraphs and checking if the vertex is contained in that list.
   *
   * @param newSubgraphs graph dataset of the resulting graph collection
   * @return vertex set of the resulting graph collection
   * @throws Exception
   */
  @Override
  protected DataSet<Vertex<Long, VD>> computeNewVertices(
    DataSet<Subgraph<Long, GD>> newSubgraphs) throws Exception {
    final List<Long> identifiers =
      newSubgraphs.map(new MapFunction<Subgraph<Long, GD>, Long>() {
        @Override
        public Long map(Subgraph<Long, GD> subgraph) throws Exception {
          return subgraph.getId();
        }
      }).collect();

    return firstGraph.getVertices()
      .filter(new FilterFunction<Vertex<Long, VD>>() {

        @Override
        public boolean filter(Vertex<Long, VD> vertex) throws Exception {
          boolean vertexInGraph = false;
          for (Long id : identifiers) {
            if (vertex.getValue().getGraphs().contains(id)) {
              vertexInGraph = true;
              break;
            }
          }
          return vertexInGraph;
        }
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return DifferenceUsingList.class.getName();
  }
}
