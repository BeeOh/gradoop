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

import org.apache.flink.api.java.ExecutionEnvironment;
import org.gradoop.HBaseTest;
import org.gradoop.storage.EPGMStore;
import org.gradoop.storage.PersistentEdgeData;
import org.gradoop.storage.PersistentGraphData;
import org.gradoop.storage.PersistentVertexData;
import org.gradoop.storage.hbase.DefaultPersistentEdgeDataFactory;
import org.gradoop.storage.hbase.DefaultPersistentGraphDataFactory;
import org.gradoop.storage.hbase.DefaultPersistentVertexDataFactory;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class EPGMDatabaseHBaseTest extends HBaseTest {

  /**
   * Writes persistent data using the {@link EPGMStore} and reads it via the
   * {@link EPGMDatabase}.
   *
   * @throws Exception
   */
  @Test
  public void readFromHBaseTest() throws Exception {
    EPGMStore<DefaultVertexData, DefaultEdgeData, DefaultGraphData> epgmStore =
      createEmptyEPGMStore();

    for (PersistentGraphData graphData : createPersistentSocialGraphData()) {
      epgmStore.writeGraphData(graphData);
    }
    for (PersistentVertexData<DefaultEdgeData> vertexData :
      createPersistentSocialVertexData()) {
      epgmStore.writeVertexData(vertexData);
    }
    for (PersistentEdgeData<DefaultVertexData> edgeData :
      createPersistentSocialEdgeData()) {
      epgmStore.writeEdgeData(edgeData);
    }

    epgmStore.flush();

    EPGMDatabase<DefaultVertexData, DefaultEdgeData, DefaultGraphData>
      epgmDatabase = EPGMDatabase
      .fromHBase(epgmStore, ExecutionEnvironment.getExecutionEnvironment());

    assertEquals("wrong graph count", 4,
      epgmDatabase.getCollection().getGraphCount());
    assertEquals("wrong vertex count", 11,
      epgmDatabase.getDatabaseGraph().getVertexCount());
    assertEquals("wrong edge count", 24,
      epgmDatabase.getDatabaseGraph().getEdgeCount());

    epgmStore.close();
  }

  /**
   * Reads data from JSON to {@link EPGMDatabase}, writes it to HBase, reads
   * it from {@link EPGMStore} and validates the counts.
   *
   * @throws Exception
   */
  @Test
  public void writeToHBaseTest() throws Exception {
    // create empty EPGM store
    EPGMStore<DefaultVertexData, DefaultEdgeData, DefaultGraphData> epgmStore =
      createEmptyEPGMStore();

    // read test data from json into EPGM database
    String vertexFile =
      EPGMDatabaseJSONTest.class.getResource("/sna_nodes").getFile();
    String edgeFile =
      EPGMDatabaseJSONTest.class.getResource("/sna_edges").getFile();
    String graphFile =
      EPGMDatabaseJSONTest.class.getResource("/sna_graphs").getFile();

    EPGMDatabase<DefaultVertexData, DefaultEdgeData, DefaultGraphData> graphDB =
      EPGMDatabase.fromJsonFile(vertexFile, edgeFile, graphFile,
        ExecutionEnvironment.getExecutionEnvironment());

    // write EPGM database to HBase
    graphDB.writeToHBase(epgmStore, new DefaultPersistentVertexDataFactory(),
      new DefaultPersistentEdgeDataFactory(),
      new DefaultPersistentGraphDataFactory());

    epgmStore.flush();

    // check graph count
    int cnt = 0;
    for (Iterator<DefaultGraphData> graphDataIterator =
         epgmStore.getGraphSpace(); graphDataIterator.hasNext(); ) {
      cnt++;
    }
    assertEquals("wrong graph count", 4, cnt);

    // check edge count
    cnt = 0;
    for (Iterator<DefaultEdgeData> edgeDataIterator =
         epgmStore.getEdgeSpace(); edgeDataIterator.hasNext(); ) {
      cnt++;
    }
    assertEquals("wrong edge count", 24, cnt);

    // check vertex count
    cnt = 0;
    for (Iterator<DefaultVertexData> vertexDataIterator =
         epgmStore.getVertexSpace(); vertexDataIterator.hasNext(); ) {
      cnt++;
    }
    assertEquals("wrong vertex count", 11, cnt);

    epgmStore.close();
  }
}
