/*
 * This file is part of Gradoop.
 *
 *     Gradoop is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Gradoop.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gradoop.model.operators;

import org.gradoop.model.EdgeData;
import org.gradoop.model.GraphData;
import org.gradoop.model.VertexData;
import org.gradoop.model.helper.Order;
import org.gradoop.model.helper.Predicate;
import org.gradoop.model.impl.GraphCollection;
import org.gradoop.model.impl.LogicalGraph;

import java.util.List;

/**
 * Defines operators that can be called on a collection of logical graphs.
 *
 * @author Martin Junghanns
 */
public interface GraphCollectionOperators<VD extends VertexData, ED extends
  EdgeData, GD extends GraphData> extends CollectionOperators<GD> {

  /**
   * Get graph from collection by identifier.
   *
   * @param graphID graph identifier
   * @return logical graph with given id
   * @throws Exception
   */
  LogicalGraph<VD, ED, GD> getGraph(final Long graphID) throws Exception;

  GraphCollection<VD, ED, GD> getGraphs(final Long... identifiers) throws
    Exception;

  GraphCollection<VD, ED, GD> getGraphs(List<Long> identifiers) throws
    Exception;

  long getGraphCount() throws Exception;

  /**
   * Filter containing graphs based on their properties.
   *
   * @param predicateFunction predicate function for graph data
   * @return collection with logical graphs that fulfil the predicate
   * @throws Exception
   */
  GraphCollection<VD, ED, GD> filter(Predicate<GD> predicateFunction) throws
    Exception;

  /*
  collection operators
   */

  GraphCollection<VD, ED, GD> select(
    Predicate<LogicalGraph<VD, ED, GD>> predicateFunction) throws Exception;

  GraphCollection<VD, ED, GD> union(
    GraphCollection<VD, ED, GD> otherCollection) throws Exception;

  GraphCollection<VD, ED, GD> intersect(
    GraphCollection<VD, ED, GD> otherCollection) throws Exception;

  GraphCollection<VD, ED, GD> intersectWithSmall(
    GraphCollection<VD, ED, GD> otherCollection) throws Exception;

  GraphCollection<VD, ED, GD> difference(
    GraphCollection<VD, ED, GD> otherCollection) throws Exception;

  GraphCollection<VD, ED, GD> differenceWithSmallResult(
    GraphCollection<VD, ED, GD> otherCollection) throws Exception;

  GraphCollection<VD, ED, GD> distinct();

  GraphCollection<VD, ED, GD> sortBy(String propertyKey, Order order);

  GraphCollection<VD, ED, GD> top(int limit);

  /*
  auxiliary operators
   */

  GraphCollection<VD, ED, GD> apply(UnaryGraphToGraphOperator<VD, ED, GD> op);

  LogicalGraph<VD, ED, GD> reduce(BinaryGraphToGraphOperator<VD, ED, GD> op);

  GraphCollection<VD, ED, GD> callForCollection(
    UnaryCollectionToCollectionOperator<VD, ED, GD> op);

  GraphCollection<VD, ED, GD> callForCollection(
    BinaryCollectionToCollectionOperator<VD, ED, GD> op,
    GraphCollection<VD, ED, GD> otherCollection) throws Exception;

  LogicalGraph<VD, ED, GD> callForGraph(
    UnaryCollectionToGraphOperator<VD, ED, GD> op);

  void writeAsJson(final String vertexFile, final String edgeFile,
    final String graphFile) throws Exception;


}
