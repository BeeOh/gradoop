///*
// * This file is part of Gradoop.
// *
// * Gradoop is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Gradoop is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with Gradoop.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package org.gradoop.io.reader;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import org.codehaus.jettison.json.JSONArray;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
//import org.gradoop.io.writer.JsonWriter;
//import org.gradoop.model.EdgeData;
//import org.gradoop.model.VertexData;
//import org.gradoop.model.impl.EdgeFactory;
//import org.gradoop.model.impl.VertexFactory;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
///**
// * Creates a vertex from its Json representation.
// */
//public class JsonReader extends SingleVertexReader {
//
//  /**
//   * {@inheritDoc}
//   */
//  @Override
//  public VertexData readVertex(String line) {
//    VertexData v = null;
//    try {
//      JSONObject json = new JSONObject(line);
//      // vertex id
//      Long vertexID = readVertexID(json);
//      // vertex label
//      String label = null;
//      if (json.has(JsonWriter.LABEL)) {
//        label = readLabel(json);
//      }
//      // vertex properties
//      Map<String, Object> properties = null;
//      if (json.has(JsonWriter.PROPERTIES)) {
//        properties = readProperties(json.getJSONObject(JsonWriter
// .PROPERTIES));
//      }
//      // outgoing edges
//      Iterable<EdgeData> outgoingEdges = null;
//      if (json.has(JsonWriter.OUT_EDGES)) {
//        outgoingEdges = readEdges(json.getJSONArray(JsonWriter.OUT_EDGES));
//      }
//      // incoming edges
//      Iterable<EdgeData> incomingEdges = null;
//      if (json.has(JsonWriter.IN_EDGES)) {
//        incomingEdges = readEdges(json.getJSONArray(JsonWriter.IN_EDGES));
//      }
//      // graphs
//      Iterable<Long> graphs = null;
//      if (json.has(JsonWriter.GRAPHS)) {
//        graphs = readGraphs(json.getJSONArray(JsonWriter.GRAPHS));
//      }
//      v = VertexFactory
//        .createDefaultVertex(vertexID, label, properties, outgoingEdges,
//          incomingEdges, graphs);
//    } catch (JSONException e) {
//      e.printStackTrace();
//    }
//    return v;
//  }
//
//  /**
//   * Reads the vertex id from the json object.
//   *
//   * @param json json object
//   * @return vertex id
//   * @throws JSONException
//   */
//  private Long readVertexID(final JSONObject json) throws JSONException {
//    return json.getLong(JsonWriter.VERTEX_ID);
//  }
//
//  /**
//   * Reads the vertex label from the json object.
//   *
//   * @param json json object
//   * @return vertex label
//   * @throws JSONException
//   */
//  private String readLabel(final JSONObject json) throws
//    JSONException {
//    return json.getString(JsonWriter.LABEL);
//  }
//
//  /**
//   * Reads key value pairs from the given json object.
//   *
//   * @param propertiesObject property key value pairs
//   * @return properties
//   * @throws JSONException
//   */
//  private Map<String, Object> readProperties(
//    final JSONObject propertiesObject) throws JSONException {
//    Map<String, Object> properties =
//      Maps.newHashMapWithExpectedSize(propertiesObject.length());
//    Iterator<?> keys = propertiesObject.keys();
//    while (keys.hasNext()) {
//      String key = keys.next().toString();
//      Object o = propertiesObject.get(key);
//      properties.put(key, o);
//    }
//    return properties;
//  }
//
//  /**
//   * Reads edges from json array.
//   *
//   * @param edgeArray json array with edge information.
//   * @return edges
//   * @throws JSONException
//   */
//  private Iterable<EdgeData> readEdges(final JSONArray edgeArray) throws
//    JSONException {
//    List<EdgeData> edgeDatas = Lists.newArrayListWithCapacity(edgeArray
// .length());
//    for (int i = 0; i < edgeArray.length(); i++) {
//      JSONObject edge = edgeArray.getJSONObject(i);
//      String label = edge.getString(JsonWriter.EDGE_LABEL);
//      Long otherID = edge.getLong(JsonWriter.EDGE_OTHER_ID);
//      if (edge.has(JsonWriter.PROPERTIES)) {
//        edgeDatas.add(EdgeFactory.createDefaultEdge(otherID, label, (long) i,
//          readProperties(edge.getJSONObject(JsonWriter.PROPERTIES))));
//      } else {
//        edgeDatas.add(
//          EdgeFactory.createDefaultEdgeWithLabel(otherID, label, (long) i));
//      }
//    }
//    return edgeDatas;
//  }
//
//  /**
//   * Reads graphs from a given json array.
//   *
//   * @param graphArray json array with graphs
//   * @return graphs
//   * @throws JSONException
//   */
//  private Iterable<Long> readGraphs(final JSONArray graphArray) throws
//    JSONException {
//    List<Long> graphs = Lists.newArrayListWithCapacity(graphArray.length());
//    for (int i = 0; i < graphArray.length(); i++) {
//      graphs.add(graphArray.getLong(i));
//    }
//    return graphs;
//  }
//}
