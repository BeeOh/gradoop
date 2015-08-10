package org.biiig.datagen;

import org.apache.flink.api.java.DataSet;
import org.biiig.datagen.flink.FlinkFPCGenerator;
import org.biiig.fsm.common.LabeledGraph;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class PatternCollectionGeneratorTest{
  @Test
  public void generate() throws Exception {
    DataSet<LabeledGraph> graphs = FlinkFPCGenerator.generate(2, 2);
    //graphs.print();
    assertThat(graphs.count(), Is.is(20l));
  }
}