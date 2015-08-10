package org.biiig.datagen.flink;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.biiig.fsm.common.LabeledGraph;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by peet on 19.06.15.
 * represents the data generator of issue #44
 */
public class FlinkFPCGenerator {
  /**
   * starts data generation for given collection and graph scale factors
   *
   * @param collectionScaleFactor number of generated graphs
   * @param graphScaleFactor size of generated graphs
   * @return the generates flink dataset
   */
  public static DataSet<LabeledGraph> generate(int collectionScaleFactor,
    int graphScaleFactor) {

    // create job list
    Set<FlinkFPCGeneratorJob> jobs = new HashSet<>();

    for (int graphType = 1; graphType <= 10; graphType++) {
      FlinkFPCGeneratorJob job = new FlinkFPCGeneratorJob();
      job.setGraphType(graphType);
      job.setCollectionScaleFactor(collectionScaleFactor);
      job.setGraphScaleFactor(graphScaleFactor);
      jobs.add(job);
    }

    // distribute jobs

    final ExecutionEnvironment env =
      ExecutionEnvironment.getExecutionEnvironment();

    // distribute graph creation
    DataSet<FlinkFPCGeneratorJob> jobDS = env.fromCollection(jobs);
    jobDS = jobDS.rebalance();

    return jobDS.flatMap(new FlinkFPCGeneratorWorker());
  }
}
