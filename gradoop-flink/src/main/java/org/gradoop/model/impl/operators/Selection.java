package org.gradoop.model.impl.operators;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.gradoop.model.EdgeData;
import org.gradoop.model.GraphData;
import org.gradoop.model.VertexData;
import org.gradoop.model.helper.Predicate;
import org.gradoop.model.impl.GraphCollection;
import org.gradoop.model.impl.LogicalGraph;
import org.gradoop.model.impl.Subgraph;
import org.gradoop.model.operators.UnaryCollectionToCollectionOperator;

import java.util.HashSet;
import java.util.Set;

/**
 * Returns a collection containing all logical graphs that fulfill a specified
 * predicate on a logical graph. The predicate is set via the constructor.
 *
 * @param <VD> vertex data type
 * @param <ED> edge data type
 * @param <GD> graph data type
 */
public class Selection<VD extends VertexData, ED extends EdgeData, GD extends
  GraphData> implements
  UnaryCollectionToCollectionOperator<VD, ED, GD> {
  /**
   * The predicate defined on all logical graphs.
   */
  private Predicate<LogicalGraph<VD, ED, GD>> predicate;

  /**
   * Creates a new Selection.
   *
   * @param predicate the predicate used to select graphs from the input
   *                  collection
   */
  public Selection(Predicate<LogicalGraph<VD, ED, GD>> predicate) {
    this.predicate = predicate;
  }

  /**
   * Executes the Selection on the GraphCollection.
   *
   * @param collection The GraphCollection on which the selection shall be
   *                   performed.
   * @return A subset of the input,can be equal.
   */
  @Override
  public GraphCollection<VD, ED, GD> execute(
    GraphCollection<VD, ED, GD> collection) throws Exception {
    ExecutionEnvironment env = collection.getGellyGraph().getContext();
    // construct a dataset containing all ids of the subgraphs
    DataSet<Long> subgraphIDs =
      collection.getSubgraphs().map(new SubgraphToIDMapper<GD>());
    GraphCollection<VD, ED, GD> resultCollection =
      new GraphCollection<>(null, null, collection.getVertexDataFactory(),
        collection.getEdgeDataFactory(), collection.getGraphDataFactory(), env);
    if (subgraphIDs.count() == 0) {
      return resultCollection;
    }
    Set<Long> subgraphSet = new HashSet<>(subgraphIDs.collect());
    // for every graph in the collection, test if the predicate is returns
    // true
    for (long id : subgraphSet) {
      LogicalGraph<VD, ED, GD> graph = collection.getGraph(id);

      if (predicate.filter(graph)) {
        // if the predicate is fulfilled, add the graph to the new collection
        resultCollection.addGraph(graph);
      }

    }
    return resultCollection;
  }

  /**
   * extracts the graph id from a Subgraph
   */
  private static class SubgraphToIDMapper<GD extends GraphData> implements
    MapFunction<Subgraph<Long, GD>, Long> {
    @Override
    public Long map(Subgraph<Long, GD> subgraph) throws Exception {
      return subgraph.getId();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return Selection.class.getName();
  }
}

