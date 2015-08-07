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

package org.gradoop.model.impl;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.gradoop.io.json.JsonWriter;
import org.gradoop.model.*;
import org.gradoop.model.helper.PatternGraph;
import org.gradoop.model.helper.Predicate;
import org.gradoop.model.helper.Subgraph;
import org.gradoop.model.helper.UnaryFunction;
import org.gradoop.model.impl.operators.Aggregation;
import org.gradoop.model.impl.operators.Combination;
import org.gradoop.model.impl.operators.Exclusion;
import org.gradoop.model.impl.operators.Overlap;
import org.gradoop.model.impl.operators.SummarizationJoin;
import org.gradoop.model.operators.BinaryGraphToGraphOperator;
import org.gradoop.model.operators.LogicalGraphOperators;
import org.gradoop.model.operators.UnaryGraphToCollectionOperator;
import org.gradoop.model.operators.UnaryGraphToGraphOperator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * Represents a single graph inside the EPGM. Holds information about the
 * graph (label, properties) and offers unary, binary and auxiliary operators.
 *
 * @author Martin Junghanns
 * @author Niklas Teichmann
 */
public class LogicalGraph<VD extends VertexData, ED extends EdgeData, GD extends
  GraphData> implements LogicalGraphOperators<VD, ED, GD>, Identifiable, Attributed, Labeled {

  private final VertexDataFactory<VD> vertexDataFactory;

  private final EdgeDataFactory<ED> edgeDataFactory;

  private final GraphDataFactory<GD> graphDataFactory;

  private final ExecutionEnvironment env;

  /**
   * Gelly graph that encapsulates the vertex and edge datasets associated
   * with that EPGraph.
   */
  private Graph<Long, VD, ED> graph;
  /**
   * Graph payload.
   */
  private GD graphData;

  private LogicalGraph(Graph<Long, VD, ED> graph, GD graphData,
    VertexDataFactory<VD> vertexDataFactory,
    EdgeDataFactory<ED> edgeDataFactory, GraphDataFactory<GD> graphDataFactory,
    ExecutionEnvironment env) {
    this.graph = graph;
    this.graphData = graphData;
    this.vertexDataFactory = vertexDataFactory;
    this.edgeDataFactory = edgeDataFactory;
    this.graphDataFactory = graphDataFactory;
    this.env = env;
  }

  public VertexDataFactory<VD> getVertexDataFactory() {
    return vertexDataFactory;
  }

  public EdgeDataFactory<ED> getEdgeDataFactory() {
    return edgeDataFactory;
  }

  public GraphDataFactory<GD> getGraphDataFactory() {
    return graphDataFactory;
  }

  public static <VD extends VertexData, ED extends EdgeData, GD extends
    GraphData> LogicalGraph<VD, ED, GD> fromGraph(
    Graph<Long, VD, ED> graph, GD graphData,
    VertexDataFactory<VD> vertexDataFactory,
    EdgeDataFactory<ED> edgeDataFactory,
    GraphDataFactory<GD> graphDataFactory) {
    return new LogicalGraph<>(graph, graphData, vertexDataFactory, edgeDataFactory,
      graphDataFactory, graph.getContext());
  }

  public Graph<Long, VD, ED> getGellyGraph() {
    return this.graph;
  }

  @Override
  public VertexDataCollection<VD> getVertices() {
    return new VertexDataCollection<>(graph.getVertices());
  }

  @Override
  public EdgeDataCollection<ED> getEdges() {
    return new EdgeDataCollection<>(graph.getEdges());
  }

  @Override
  public EdgeDataCollection<ED> getOutgoingEdges(final Long vertexID) {
    DataSet<Edge<Long, ED>> outgoingEdges =
      this.graph.getEdges().filter(new FilterFunction<Edge<Long, ED>>() {
        @Override
        public boolean filter(Edge<Long, ED> edgeTuple) throws Exception {
          return edgeTuple.getValue().getSourceVertexId().equals(vertexID);
        }
      });
    return new EdgeDataCollection<>(outgoingEdges);
  }

  @Override
  public EdgeDataCollection<ED> getIncomingEdges(final Long vertexID) {
    return new EdgeDataCollection<>(
      this.graph.getEdges().filter(new FilterFunction<Edge<Long, ED>>() {
        @Override
        public boolean filter(Edge<Long, ED> edgeTuple) throws Exception {
          return edgeTuple.getValue().getTargetVertexId().equals(vertexID);
        }
      }));
  }

  @Override
  public long getVertexCount() throws Exception {
    return this.graph.numberOfVertices();
  }

  @Override
  public long getEdgeCount() throws Exception {
    return this.graph.numberOfEdges();
  }

  @Override
  public GraphCollection<VD, ED, GD> match(String graphPattern,
    Predicate<PatternGraph> predicateFunc) {
    throw new NotImplementedException();
  }

  @Override
  public LogicalGraph<VD, ED, GD> project(UnaryFunction<VD, VD> vertexFunction,
    UnaryFunction<ED, ED> edgeFunction) {
    throw new NotImplementedException();
  }

  @Override
  public <O extends Number> LogicalGraph<VD, ED, GD> aggregate(String propertyKey,
    UnaryFunction<LogicalGraph<VD, ED, GD>, O> aggregateFunc) throws Exception {
    return callForGraph(new Aggregation<>(propertyKey, aggregateFunc));
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarize(String vertexGroupingKey) throws
    Exception {
    return summarize(vertexGroupingKey, null);
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarize(String vertexGroupingKey,
    String edgeGroupingKey) throws Exception {
    return callForGraph(
      new SummarizationJoin<VD, ED, GD>(vertexGroupingKey, edgeGroupingKey,
        false, false));
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarizeOnVertexLabel() throws Exception {
    return summarizeOnVertexLabel(null, null);
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarizeOnVertexLabelAndVertexProperty(
    String vertexGroupingKey) throws Exception {
    return summarizeOnVertexLabel(vertexGroupingKey, null);
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarizeOnVertexLabelAndEdgeProperty(
    String edgeGroupingKey) throws Exception {
    return summarizeOnVertexLabel(null, edgeGroupingKey);
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarizeOnVertexLabel(String vertexGroupingKey,
    String edgeGroupingKey) throws Exception {
    return callForGraph(
      new SummarizationJoin<VD, ED, GD>(vertexGroupingKey, edgeGroupingKey,
        true, false));
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarizeOnVertexAndEdgeLabel() throws Exception {
    return summarizeOnVertexAndEdgeLabel(null, null);
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarizeOnVertexAndEdgeLabelAndVertexProperty(
    String vertexGroupingKey) throws Exception {
    return summarizeOnVertexAndEdgeLabel(vertexGroupingKey, null);
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarizeOnVertexAndEdgeLabelAndEdgeProperty(
    String edgeGroupingKey) throws Exception {
    return summarizeOnVertexAndEdgeLabel(null, edgeGroupingKey);
  }

  @Override
  public LogicalGraph<VD, ED, GD> summarizeOnVertexAndEdgeLabel(
    String vertexGroupingKey, String edgeGroupingKey) throws Exception {
    return callForGraph(
      new SummarizationJoin<VD, ED, GD>(vertexGroupingKey, edgeGroupingKey,
        true, true));
  }

  @Override
  public LogicalGraph<VD, ED, GD> combine(LogicalGraph<VD, ED, GD> otherGraph) {
    return callForGraph(new Combination<VD, ED, GD>(), otherGraph);
  }

  @Override
  public LogicalGraph<VD, ED, GD> overlap(LogicalGraph<VD, ED, GD> otherGraph) {
    return callForGraph(new Overlap<VD, ED, GD>(), otherGraph);
  }

  @Override
  public LogicalGraph<VD, ED, GD> exclude(LogicalGraph<VD, ED, GD> otherGraph) {
    return callForGraph(new Exclusion<VD, ED, GD>(), otherGraph);
  }

  @Override
  public LogicalGraph<VD, ED, GD> callForGraph(
    UnaryGraphToGraphOperator<VD, ED, GD> operator) throws Exception {
    return operator.execute(this);
  }

  @Override
  public LogicalGraph<VD, ED, GD> callForGraph(
    BinaryGraphToGraphOperator<VD, ED, GD> operator,
    LogicalGraph<VD, ED, GD> otherGraph) {
    return operator.execute(this, otherGraph);
  }

  @Override
  public GraphCollection<VD, ED, GD> callForCollection(
    UnaryGraphToCollectionOperator<VD, ED, GD> operator) {
    return operator.execute(this);
  }

  @Override
  public void writeAsJson(String vertexFile, String edgeFile,
    String graphFile) throws Exception {
    this.getGellyGraph().getVertices().writeAsFormattedText(vertexFile,
      new JsonWriter.VertexTextFormatter<VD>()).getDataSet().collect();
    this.getGellyGraph().getEdges()
      .writeAsFormattedText(edgeFile, new JsonWriter.EdgeTextFormatter<ED>())
      .getDataSet().collect();
    env.fromElements(new Subgraph<>(graphData.getId(), graphData))
      .writeAsFormattedText(graphFile, new JsonWriter.GraphTextFormatter<GD>())
      .getDataSet().collect();
  }

  @Override
  public Map<String, Object> getProperties() {
    return graphData.getProperties();
  }

  @Override
  public Iterable<String> getPropertyKeys() {
    return graphData.getPropertyKeys();
  }

  @Override
  public Object getProperty(String key) {
    return graphData.getProperty(key);
  }

  @Override
  public <T> T getProperty(String key, Class<T> type) {
    return graphData.getProperty(key, type);
  }

  @Override
  public void setProperties(Map<String, Object> properties) {
    graphData.setProperties(properties);
  }

  @Override
  public void setProperty(String key, Object value) {
    graphData.setProperty(key, value);
  }

  @Override
  public int getPropertyCount() {
    return graphData.getPropertyCount();
  }

  @Override
  public Long getId() {
    return graphData.getId();
  }

  @Override
  public void setId(Long id) {
    graphData.setId(id);
  }

  @Override
  public String getLabel() {
    return graphData.getLabel();
  }

  @Override
  public void setLabel(String label) {
    graphData.setLabel(label);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(String.format("EPGraph{%n"));
    sb.append(
      String.format("%-10d %s %n", graphData.getId(), graphData.toString()));
    try {
      sb.append(String.format("Vertices:%n"));
      for (VD v : this.getVertices().collect()) {
        sb.append(String.format("%-10d %s %n", v.getId(), v));
      }
      sb.append(String.format("Edges:%n"));
      for (ED e : this.getEdges().collect()) {
        sb.append(String.format("%-10d %s %n", e.getId(), e));
      }
      sb.append('}');
    } catch (Exception e) {
      sb.append(e);
    }
    return sb.toString();
  }


}
