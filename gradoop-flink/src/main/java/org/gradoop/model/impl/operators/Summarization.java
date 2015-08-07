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

import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.operators.SortedGrouping;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.util.Collector;
import org.gradoop.model.EdgeData;
import org.gradoop.model.EdgeDataFactory;
import org.gradoop.model.GraphData;
import org.gradoop.model.GraphDataFactory;
import org.gradoop.model.VertexData;
import org.gradoop.model.VertexDataFactory;
import org.gradoop.model.helper.FlinkConstants;
import org.gradoop.model.helper.KeySelectors;
import org.gradoop.model.impl.LogicalGraph;
import org.gradoop.model.operators.UnaryGraphToGraphOperator;

/**
 * The summarization operator determines a structural grouping of similar
 * vertices and edges to condense a graph and thus help to uncover insights
 * about patterns hidden in the graph.
 * <p>
 * The graph summarization operator represents every vertex group by a single
 * vertex in the summarized graph; edges between vertices in the summary graph
 * represent a group of edges between the vertex group members of the
 * original graph. Summarization is defined by specifying grouping keys for
 * vertices and edges, respectively, similarly as for GROUP BY in SQL.
 * <p>
 * Consider the following example:
 * <p>
 * Input graph:
 * <p>
 * Vertices:<br>
 * (0, "Person", {city: L})<br>
 * (1, "Person", {city: L})<br>
 * (2, "Person", {city: D})<br>
 * (3, "Person", {city: D})<br>
 * <p>
 * Edges:{(0,1), (1,0), (1,2), (2,1), (2,3), (3,2)}
 * <p>
 * Output graph (summarized on vertex property "city"):
 * <p>
 * Vertices:<br>
 * (0, "Person", {city: L, count: 2})
 * (2, "Person", {city: D, count: 2})
 * <p>
 * Edges:<br>
 * ((0, 0), {count: 2}) // 2 intra-edges in L<br>
 * ((2, 2), {count: 2}) // 2 intra-edges in L<br>
 * ((0, 2), {count: 1}) // 1 inter-edge from L to D<br>
 * ((2, 0), {count: 1}) // 1 inter-edge from D to L<br>
 * <p>
 * In addition to vertex properties, summarization is also possible on edge
 * properties, vertex- and edge labels as well as combinations of those.
 *
 * @author Martin Junghanns
 */
public abstract class Summarization<VD extends VertexData, ED extends
  EdgeData, GD extends GraphData> implements
  UnaryGraphToGraphOperator<VD, ED, GD> {
  /**
   * Used to represent vertices that do not have the vertex grouping property.
   */
  public static final String NULL_VALUE = "__NULL";

  private static final String COUNT_PROPERTY_KEY = "count";

  private final String vertexGroupingKey;

  private final String edgeGroupingKey;

  private final boolean useVertexLabels;

  private final boolean useEdgeLabels;

  protected GraphDataFactory<GD> graphDataFactory;

  protected VertexDataFactory<VD> vertexDataFactory;

  protected EdgeDataFactory<ED> edgeDataFactory;

  Summarization(String vertexGroupingKey, String edgeGroupingKey,
    boolean useVertexLabels, boolean useEdgeLabels) {
    this.vertexGroupingKey = vertexGroupingKey;
    this.edgeGroupingKey = edgeGroupingKey;
    this.useVertexLabels = useVertexLabels;
    this.useEdgeLabels = useEdgeLabels;
  }

  @Override
  public LogicalGraph<VD, ED, GD> execute(LogicalGraph<VD, ED, GD> graph) {
    LogicalGraph<VD, ED, GD> result;
    Graph<Long, VD, ED> gellyGraph;

    vertexDataFactory = graph.getVertexDataFactory();
    edgeDataFactory = graph.getEdgeDataFactory();
    graphDataFactory = graph.getGraphDataFactory();

    if (!useVertexProperty() &&
      !useEdgeProperty() && !useVertexLabels() && !useEdgeLabels()) {
      // graphs stays unchanged
      result = graph;
    } else {
      GD GD = createNewGraphData();
      gellyGraph = summarizeInternal(graph.getGellyGraph());
      result = LogicalGraph
        .fromGraph(gellyGraph, GD, graph.getVertexDataFactory(),
          graph.getEdgeDataFactory(), graph.getGraphDataFactory());
    }
    return result;
  }

  protected boolean useVertexProperty() {
    return vertexGroupingKey != null && !"".equals(vertexGroupingKey);
  }

  protected String getVertexGroupingKey() {
    return vertexGroupingKey;
  }

  protected boolean useVertexLabels() {
    return useVertexLabels;
  }

  protected boolean useEdgeProperty() {
    return edgeGroupingKey != null && !"".equals(edgeGroupingKey);
  }

  protected String getEdgeGroupingKey() {
    return edgeGroupingKey;
  }

  protected boolean useEdgeLabels() {
    return useEdgeLabels;
  }

  protected SortedGrouping<Vertex<Long, VD>> groupAndSortVertices(
    Graph<Long, VD, ED> graph) {
    return graph.getVertices()
      // group vertices by the given property
      .groupBy(new VertexGroupingKeySelector<VD>(getVertexGroupingKey(),
        useVertexLabels()))
        // sort the group (smallest id is group representative)
      .sortGroup(new KeySelectors.VertexKeySelector<VD>(), Order.ASCENDING);
  }

  protected DataSet<Vertex<Long, VD>> buildSummarizedVertices(
    SortedGrouping<Vertex<Long, VD>> groupedSortedVertices) {
    return groupedSortedVertices.reduceGroup(
      new VertexGroupSummarizer<>(getVertexGroupingKey(), useVertexLabels(),
        vertexDataFactory));
  }

  protected UnsortedGrouping<Tuple5<Long, Long, Long, String, String>>
  groupEdges(
    DataSet<Tuple5<Long, Long, Long, String, String>> edges) {
    UnsortedGrouping<Tuple5<Long, Long, Long, String, String>> groupedEdges;
    if (useEdgeProperty() && useEdgeLabels()) {
      groupedEdges = edges.groupBy(1, 2, 3, 4);
    } else if (useEdgeLabels()) {
      groupedEdges = edges.groupBy(1, 2, 3);
    } else if (useEdgeProperty()) {
      groupedEdges = edges.groupBy(1, 2, 4);
    } else {
      groupedEdges = edges.groupBy(1, 2);
    }
    return groupedEdges;
  }

  private GD createNewGraphData() {
    return graphDataFactory.createGraphData(FlinkConstants.SUMMARIZE_GRAPH_ID);
  }

  protected abstract Graph<Long, VD, ED> summarizeInternal(
    Graph<Long, VD, ED> graph);

  /**
   * Selects the key to group vertices.
   */
  protected static class VertexGroupingKeySelector<VD extends VertexData>
    implements
    KeySelector<Vertex<Long, VD>, String> {

    private String groupPropertyKey;
    private boolean useLabel;

    public VertexGroupingKeySelector(String groupPropertyKey,
      boolean useLabel) {
      this.groupPropertyKey = groupPropertyKey;
      this.useLabel = useLabel;
    }

    @Override
    public String getKey(Vertex<Long, VD> v) throws Exception {
      String label = v.getValue().getLabel();
      String groupingValue = null;
      boolean useProperty =
        groupPropertyKey != null && !"".equals(groupPropertyKey);
      boolean hasProperty =
        useProperty && (v.getValue().getProperty(groupPropertyKey) != null);

      if (useLabel && useProperty && hasProperty) {
        groupingValue = String.format("%s_%s", label,
          v.getValue().getProperty(groupPropertyKey).toString());
      } else if (useLabel && useProperty) {
        groupingValue = String.format("%s_%s", label, NULL_VALUE);
      } else if (useLabel) {
        groupingValue = label;
      } else if (useProperty && hasProperty) {
        groupingValue = v.getValue().getProperty(groupPropertyKey).toString();
      } else if (useProperty) {
        groupingValue = NULL_VALUE;
      }

      return groupingValue;
    }
  }

  /**
   * Creates a summarized vertex from a group of vertices.
   */
  protected static class VertexGroupSummarizer<VD extends VertexData> implements
    GroupReduceFunction<Vertex<Long, VD>, Vertex<Long, VD>>,
    ResultTypeQueryable<Vertex<Long, VD>> {

    private final VertexDataFactory<VD> vertexDataFactory;
    private String groupPropertyKey;
    private boolean useLabel;

    public VertexGroupSummarizer(String groupPropertyKey, boolean useLabel,
      VertexDataFactory<VD> vertexDataFactory) {
      this.groupPropertyKey = groupPropertyKey;
      this.useLabel = useLabel;
      this.vertexDataFactory = vertexDataFactory;
    }

    @Override
    public void reduce(Iterable<Vertex<Long, VD>> vertices,
      Collector<Vertex<Long, VD>> collector) throws Exception {
      int groupCount = 0;
      Long newVertexID = 0L;
      String groupLabel = null;
      String groupValue = null;
      boolean initialized = false;
      for (Vertex<Long, VD> v : vertices) {
        groupCount++;
        if (!initialized) {
          // will be the minimum vertex id in the group
          newVertexID = v.getId();
          // get label if necessary
          groupLabel = useLabel ? v.getValue().getLabel() :
            FlinkConstants.DEFAULT_VERTEX_LABEL;
          // get group value if necessary
          if (storeGroupProperty()) {
            groupValue = getGroupProperty(v);
          }
          initialized = true;
        }
      }

      VD newVertexData =
        vertexDataFactory.createVertexData(newVertexID, groupLabel);
      if (storeGroupProperty()) {
        newVertexData.setProperty(groupPropertyKey, groupValue);
      }
      newVertexData.setProperty(COUNT_PROPERTY_KEY, groupCount);
      newVertexData.addGraph(FlinkConstants.SUMMARIZE_GRAPH_ID);

      collector.collect(new Vertex<>(newVertexID, newVertexData));
    }

    private boolean storeGroupProperty() {
      return groupPropertyKey != null && !"".equals(groupPropertyKey);
    }

    private String getGroupProperty(Vertex<Long, VD> v) {
      if (v.getValue().getProperty(groupPropertyKey) != null) {
        return v.getValue().getProperty(groupPropertyKey).toString();
      } else {
        return NULL_VALUE;
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public TypeInformation<Vertex<Long, VD>> getProducedType() {
      return new TupleTypeInfo(Vertex.class, BasicTypeInfo.LONG_TYPE_INFO,
        TypeExtractor.createTypeInfo(vertexDataFactory.getType()));
    }
  }

  /**
   * Creates a summarized edge from a group of edges including a edge
   * grouping value.
   */
  protected static class EdgeGroupSummarizer<ED extends EdgeData> implements
    GroupReduceFunction<Tuple5<Long, Long, Long, String, String>, Edge<Long,
      ED>>,
    ResultTypeQueryable<Edge<Long, ED>> {

    private final EdgeDataFactory<ED> edgeDataFactory;
    private String groupPropertyKey;
    private boolean useLabel;

    public EdgeGroupSummarizer(String groupPropertyKey, boolean useLabel,
      EdgeDataFactory<ED> edgeDataFactory) {
      this.groupPropertyKey = groupPropertyKey;
      this.useLabel = useLabel;
      this.edgeDataFactory = edgeDataFactory;
    }

    @Override
    public void reduce(Iterable<Tuple5<Long, Long, Long, String, String>> edges,
      Collector<Edge<Long, ED>> collector) throws Exception {
      int edgeCount = 0;
      boolean initialized = false;
      // new edge id will be the first edge id in the group (which is sorted)
      Long newEdgeID = null;
      Long newSourceVertex = null;
      Long newTargetVertex = null;
      String edgeLabel = FlinkConstants.DEFAULT_EDGE_LABEL;
      String edgeGroupingValue = null;

      for (Tuple5<Long, Long, Long, String, String> e : edges) {
        edgeCount++;
        if (!initialized) {
          newEdgeID = e.f0;
          newSourceVertex = e.f1;
          newTargetVertex = e.f2;
          if (useLabel) {
            edgeLabel = e.f3;
          }
          edgeGroupingValue = e.f4;
          initialized = true;
        }
      }

      ED newEdgeData = edgeDataFactory
        .createEdgeData(newEdgeID, edgeLabel, newSourceVertex, newTargetVertex);

      if (storeGroupProperty()) {
        newEdgeData.setProperty(groupPropertyKey, edgeGroupingValue);
      }
      newEdgeData.setProperty(COUNT_PROPERTY_KEY, edgeCount);
      newEdgeData.addGraph(FlinkConstants.SUMMARIZE_GRAPH_ID);
      collector
        .collect(new Edge<>(newSourceVertex, newTargetVertex, newEdgeData));
    }

    private boolean storeGroupProperty() {
      return groupPropertyKey != null && !"".equals(groupPropertyKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TypeInformation<Edge<Long, ED>> getProducedType() {
      return new TupleTypeInfo(Edge.class, BasicTypeInfo.LONG_TYPE_INFO,
        BasicTypeInfo.LONG_TYPE_INFO,
        TypeExtractor.createTypeInfo(edgeDataFactory.getType()));
    }
  }
}
