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
 * Created by niklas on 29.07.15.
 */
public class Selection implements UnaryCollectionToCollectionOperator {

  private Predicate<EPGraph> predicate;

  public Selection(Predicate<EPGraph> predicate) {
    this.predicate = predicate;
  }

  @Override
  public EPGraphCollection execute(EPGraphCollection collection) {
    ExecutionEnvironment env = collection.getGellyGraph().getContext();
    DataSet<Long> subgraphIDs = collection.getSubgraphs()
      .map(new MapFunction<Subgraph<Long, EPFlinkGraphData>, Long>() {
        @Override
        public Long map(Subgraph<Long, EPFlinkGraphData> subgraph) throws
          Exception {
          return subgraph.getId();
        }
      });

    EPGraphCollection resultCollection = new EPGraphCollection(null, null, env);
    try {
      Set<Long> subgraphSet = new HashSet<>(subgraphIDs.collect());
      for (long id : subgraphSet) {
        EPGraph graph = collection.getGraph(id);
        if (predicate.filter(graph)) {
          resultCollection.addGraph(graph);
        }
      }
    } catch (Exception e) {
      resultCollection = new EPGraphCollection(null, null, env);
    }
    return resultCollection;
  }

  @Override
  public String getName() {
    return "Selection";
  }
}

