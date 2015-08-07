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

package org.gradoop.model.operators;

import org.gradoop.model.helper.PatternGraph;
import org.gradoop.model.EdgeData;
import org.gradoop.model.GraphData;
import org.gradoop.model.VertexData;
import org.gradoop.model.helper.Predicate;
import org.gradoop.model.helper.UnaryFunction;
import org.gradoop.model.impl.EdgeDataCollection;
import org.gradoop.model.impl.GraphCollection;
import org.gradoop.model.impl.VertexDataCollection;
import org.gradoop.model.impl.LogicalGraph;

/**
 * Describes all operators that can be applied on a single graph inside the
 * EPGM.
 */
public interface LogicalGraphOperators<VD extends VertexData, ED extends
  EdgeData, GD extends GraphData> {

  VertexDataCollection<VD> getVertices();

  EdgeDataCollection<ED> getEdges();

  EdgeDataCollection<ED> getOutgoingEdges(final Long vertexID);

  EdgeDataCollection<ED> getIncomingEdges(final Long vertexID);

  long getVertexCount() throws Exception;

  long getEdgeCount() throws Exception;

  /*
  unary operators take one graph as input and return a single graph or a
  graph collection
   */

  GraphCollection<VD, ED, GD> match(String graphPattern,
    Predicate<PatternGraph> predicateFunc);

  LogicalGraph<VD, ED, GD> project(UnaryFunction<VD, VD> vertexFunction,
    UnaryFunction<ED, ED> edgeFunction);

  <O extends Number> LogicalGraph<VD, ED, GD> aggregate(String propertyKey,
    UnaryFunction<LogicalGraph<VD, ED, GD>, O> aggregateFunc) throws Exception;

  /* Summarization */

  LogicalGraph<VD, ED, GD> summarize(String vertexGroupingKey) throws Exception;

  LogicalGraph<VD, ED, GD> summarize(String vertexGroupingKey,
    String edgeGroupingKey) throws Exception;

  LogicalGraph<VD, ED, GD> summarizeOnVertexLabel() throws Exception;

  LogicalGraph<VD, ED, GD> summarizeOnVertexLabelAndVertexProperty(
    String vertexGroupingKey) throws Exception;

  LogicalGraph<VD, ED, GD> summarizeOnVertexLabelAndEdgeProperty(
    String edgeGroupingKey) throws Exception;

  LogicalGraph<VD, ED, GD> summarizeOnVertexLabel(String vertexGroupingKey,
    String edgeGroupingKey) throws Exception;

  LogicalGraph<VD, ED, GD> summarizeOnVertexAndEdgeLabel() throws Exception;

  LogicalGraph<VD, ED, GD> summarizeOnVertexAndEdgeLabelAndVertexProperty(
    String vertexGroupingKey) throws Exception;

  LogicalGraph<VD, ED, GD> summarizeOnVertexAndEdgeLabelAndEdgeProperty(
    String edgeGroupingKey) throws Exception;

  LogicalGraph<VD, ED, GD> summarizeOnVertexAndEdgeLabel(
    String vertexGroupingKey, String edgeGroupingKey) throws Exception;

  /*
  binary operators take two graphs as input and return a single graph
   */

  LogicalGraph<VD, ED, GD> combine(LogicalGraph<VD, ED, GD> otherGraph);

  LogicalGraph<VD, ED, GD> overlap(LogicalGraph<VD, ED, GD> otherGraph);

  LogicalGraph<VD, ED, GD> exclude(LogicalGraph<VD, ED, GD> otherGraph);

  /*
  auxiliary operators
   */
  LogicalGraph<VD, ED, GD> callForGraph(
    UnaryGraphToGraphOperator<VD, ED, GD> operator) throws Exception;

  LogicalGraph<VD, ED, GD> callForGraph(
    BinaryGraphToGraphOperator<VD, ED, GD> operator,
    LogicalGraph<VD, ED, GD> otherGraph);

  GraphCollection<VD, ED, GD> callForCollection(
    UnaryGraphToCollectionOperator<VD, ED, GD> operator);

  void writeAsJson(final String vertexFile, final String edgeFile,
    final String graphFile) throws Exception;

}