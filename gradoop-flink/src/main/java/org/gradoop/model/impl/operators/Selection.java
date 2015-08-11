package org.gradoop.model.impl.operators;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.gradoop.model.helper.Predicate;
import org.gradoop.model.impl.EPFlinkGraphData;
import org.gradoop.model.impl.EPGraph;
import org.gradoop.model.impl.EPGraphCollection;
import org.gradoop.model.impl.Subgraph;
import org.gradoop.model.operators.UnaryCollectionToCollectionOperator;

import java.util.HashSet;
import java.util.Set;

/**
 * Returns a collection containing all logical graphs that fulfill a specified
 * predicate on a logical graph. The predicate is set via the constructor.
 */
public class Selection implements UnaryCollectionToCollectionOperator {
  /**
   * The predicate defined on all logical graphs.
   */
  private Predicate<EPGraph> predicate;

  /**
   * Creates a new Selection.
   *
   * @param predicate the predicate used to select graphs from the input
   *                  collection
   */
  public Selection(Predicate<EPGraph> predicate) {
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
  public EPGraphCollection execute(EPGraphCollection collection) {
    ExecutionEnvironment env = collection.getGellyGraph().getContext();
    //construct a dataset containing all ids of the subgraphs
    DataSet<Long> subgraphIDs =
      collection.getSubgraphs().map(new SubgraphToIDMapper());
    EPGraphCollection resultCollection = new EPGraphCollection(null, null, env);
    try {
      Set<Long> subgraphSet = new HashSet<>(subgraphIDs.collect());
      //for every graph in the collection, test if the predicate is returns true
      for (long id : subgraphSet) {
        EPGraph graph = collection.getGraph(id);
        if (predicate.filter(graph)) {
          //if the predicate is fullfilled, add the graph to the new collection
          resultCollection.addGraph(graph);
        }
      }
    } catch (Exception e) {
      resultCollection = new EPGraphCollection(null, null, env);
    }
    return resultCollection;
  }

  /**
   * extracts the graph id from a Subgraph
   */
  private static class SubgraphToIDMapper implements
    MapFunction<Subgraph<Long, EPFlinkGraphData>, Long> {
    @Override
    public Long map(Subgraph<Long, EPFlinkGraphData> subgraph) throws
      Exception {
      return subgraph.getId();
    }
  }

  @Override
  public String getName() {
    return "Selection";
  }
}

