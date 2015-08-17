//package org.gradoop.io.reader;
//
//import com.google.common.collect.Lists;
//import org.gradoop.GConstants;
//import org.gradoop.GradoopClusterTest;
//import org.gradoop.model.EdgeData;
//import org.gradoop.model.VertexData;
//import org.gradoop.storage.GraphStore;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
///**
// * Tests for {@link org.gradoop.io.reader.RDFReader}.
// */
//public class RDFReaderTest extends GradoopClusterTest {
//  private static final Long lgdID = 7821830309733859244L;
//  private static final Long geoID = 7282190339445886145L;
//  private static final Long dbpID = 7459480252541644492L;
//  private static final Long hedbpID = -7523405104137954346L;
//  private static final String lgd =
//    "http://linkedgeodata.org/triplify/node240111242";
//  private static final String geo = "http://sws.geonames.org/2879139/";
//  private static final String dbp = "http://dbpedia.org/resource/Leipzig";
//  private static final String hedbp = "http://he.dbpedia.org/resource/" +
//    "\u05DC\u05D9\u05D9\u05E4\u05E6\u05D9\u05D2";
//  private static final String eLabel = "http://www.w3.org/2002/07/owl#sameAs";
//  private static final String post = "http://dbpedia.org/ontology/postalCode";
//  private static final String pop =
//    "http://dbpedia.org/ontology/populationTotal";
//  private static final String postVal = "04001-04357";
//  private static final int popVal = 539348;
//
//  private static final String[] RDF_NTRIPLES =
//    new String[]{"<http://linkedgeodata.org/triplify/node240111242> " +
//                   "<http://www.w3.org/2002/07/owl#sameAs> " +
//                   "<http://sws.geonames.org/2879139/> .",
//                 "<http://dbpedia.org/resource/Leipzig> " +
//                   "<http://www.w3.org/2002/07/owl#sameAs> " +
//                   "<http://sws.geonames.org/2879139/> .",
//                 "<http://dbpedia.org/resource/Leipzig> " +
//                   "<http://dbpedia.org/ontology/postalCode> " +
//                   "\"04001-04357\" .",
//                 "<http://dbpedia.org/resource/Leipzig> " +
//                   "<http://dbpedia.org/ontology/populationTotal> " +
//                   "\"539348\"^^<http://www.w3.org/2001/XMLSchema#integer> .",
//                 "<http://dbpedia.org/resource/Leipzig> " +
//                   "<http://www.w3.org/2002/07/owl#sameAs>	" +
//                   "<http://he.dbpedia.org/resource/" + "" +
//                   "\u05DC\u05D9\u05D9\u05E4\u05E6\u05D9\u05D2> ."};
//
//  protected List<VertexData> createVerticesFromRDF() {
//    VertexLineReader reader = new RDFReader();
//    List<VertexData> vertices = Lists.newArrayList();
//
//    for (String line : RDF_NTRIPLES) {
//      for (VertexData v : reader.readVertexList(line)) {
//        vertices.add(v);
//      }
//    }
//    return vertices;
//  }
//
//  protected void checkLabel(VertexData vertexData, String label) {
//    assertEquals(label, vertexData.getLabel());
//  }
//
//  public void validateDetails(GraphStore graphStore) throws
//    InterruptedException, IOException, ClassNotFoundException {
//    Iterator<VertexData> vertices = graphStore.getVertexSpace(GConstants
//      .DEFAULT_TABLE_VERTICES);
//    while (vertices.hasNext()) {
//      VertexData v = vertices.next();
//      long id = v.getId();
//      if (id == lgdID) {
//        checkLabel(v, lgd);
//        for (EdgeData e : v.getOutgoingEdges()) {
//          assertEquals(e.getOtherID(), geoID);
//          assertEquals(e.getLabel(), eLabel);
//        }
//      } else if (id == geoID) {
//        checkLabel(v, geo);
//        for (EdgeData e : v.getIncomingEdges()) {
//          assertTrue((long) e.getOtherID() == lgdID
//            || (long) e.getOtherID() == dbpID);
//          assertEquals(e.getLabel(), eLabel);
//        }
//      } else if (id == dbpID) {
//        checkLabel(v, dbp);
//        if (v.getOutgoingDegree() < 1) {
//          for (String prop : v.getPropertyKeys()) {
//            if (prop.equals(post)) {
//              assertEquals(v.getProperty(prop), postVal);
//            } else if (prop.equals(pop)) {
//              assertEquals(v.getProperty(prop), popVal);
//            }
//          }
//        } else {
//          for (EdgeData e : v.getOutgoingEdges()) {
//            assertTrue((long) e.getOtherID() == geoID
//              || (long) e.getOtherID() == hedbpID);
//            assertEquals(e.getLabel(), eLabel);
//          }
//        }
//      } else if (id == hedbpID) {
//        checkLabel(v, hedbp);
//        for (EdgeData e : v.getIncomingEdges()) {
//          assertEquals(e.getOtherID(), dbpID);
//          assertEquals(e.getLabel(), eLabel);
//        }
//      } else {
//        assertTrue("wrong triple contained", false);
//      }
//    }
//  }
//
//  @Test
//  public void loadRDFToHBaseTest() throws IOException, ClassNotFoundException,
//    InterruptedException {
//    GraphStore graphStore = createEmptyGraphStore();
//    VertexLineReader rdfReader = new RDFReader();
//    for (String line : RDF_NTRIPLES) {
//      for (VertexData v : rdfReader.readVertexList(line)) {
//        graphStore.writeVertexData(v);
//      }
//    }
//
//    validateRDFGraph(graphStore);
//    validateDetails(graphStore);
//    graphStore.close();
//  }
//
//  private void validateRDFGraph(GraphStore graphStore) {
//    validateVertex(graphStore.readVertexData(lgdID), 0, 1, 0);
//    validateVertex(graphStore.readVertexData(geoID), 0, 0, 2);
//    validateVertex(graphStore.readVertexData(dbpID), 2, 2, 0);
//    validateVertex(graphStore.readVertexData(hedbpID), 0, 0, 1);
//  }
//
//  private void validateVertex(VertexData vertexData, int expectedPropertyCount,
//    int expectedOutEdgesCount, int expectedInEdgesCount) {
//    assertEquals(expectedPropertyCount, vertexData.getPropertyCount());
//    List<EdgeData> outgoingEdgeDatas = Lists.newArrayList(vertexData.getOutgoingEdges());
//    assertEquals(expectedOutEdgesCount, outgoingEdgeDatas.size());
//    List<EdgeData> incomingEdgeDatas = Lists.newArrayList(vertexData.getIncomingEdges());
//    assertEquals(expectedInEdgesCount, incomingEdgeDatas.size());
//  }
//
//  @Test
//  public void countGeneratedVertices() {
//    assertEquals(8, createVerticesFromRDF().size());
//  }
//
//  @Test
//  public void countRDFNTriplesTest() {
//    assertEquals(5, RDF_NTRIPLES.length);
//  }
//
//}