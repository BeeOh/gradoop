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
//import org.gradoop.GConstants;
//import org.gradoop.model.EdgeData;
//import org.gradoop.model.VertexData;
//import org.gradoop.model.impl.EdgeFactory;
//import org.gradoop.model.impl.VertexFactory;
//import org.gradoop.storage.exceptions.UnsupportedTypeException;
//
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Pattern;
//
///**
// * An example for a {@link org.gradoop.io.reader.VertexLineReader} that
// reads a
// * vertex input line given in the following format:
// * <p/>
// * {@code <vertex-id>|[<label-list>]|<properties>|[<out-edges>]|[<in-edges>]|
// * [<graph-list>]}
// * <p/>
// * {@code <vertex-id> ::= <unique long value>}<br/>
// * {@code <label-list> ::= <label>[ <label-list>]}<br/>
// * {@code <properties> ::= <property count>[ <property-list>]}<br/>
// * {@code <property-list> :== <property>[ <property-list>]}*<br/>
// * {@code <property> :== <property-key> <property-type> <property-value>}<br/>
// * {@code <property-type> :== 0 | 1 | 2 | 3 | 4 | 5 }<br/>
// * {@code <out-edges> :== <edge>[,<out-edges>]}<br/>
// * {@code <in-edges> :== <edge>[,<in-edges>}<br/>
// * {@code <edge> :== <edge-identifier> <properties>}<br/>
// * {@code <edge-identifier> :== <edge-label>.<vertex-id>.<index>}<br/>
// * {@code <graph-list> :== <graph-id>[ <graph-list>]}
// * <p/>
// * 0 - boolean<br/>
// * 1 - int<br/>
// * 2 - long<br/>
// * 3 - float<br/>
// * 4 - double<br/>
// * 5 - String
// * <p/>
// * Example:
// * <p/>
// * 0|A|3 k1 5 v1 k2 5 v2 k3 5 v3|a.1.0 1 k1 5 v1|b.1.0 1 k1 5 v1|1 0
// */
//public class EPGVertexReader implements VertexLineReader {
//
//  /**
//   * Separates a line into tokens.
//   */
//  private static final Pattern LINE_TOKEN_SEPARATOR = Pattern.compile("\\|");
//  /**
//   * Separates a value token into single values (e.g. properties).
//   */
//  private static final Pattern VALUE_TOKEN_SEPARATOR = Pattern.compile(" ");
//  /**
//   * Separates a list token into single values (e.g. edges).
//   */
//  private static final Pattern LIST_TOKEN_SEPARATOR = Pattern.compile(",");
//  /**
//   * Separates an edge-identifier into its tokens.
//   */
//  private static final Pattern EDGE_KEY_TOKEN_SEPARATOR =
//    Pattern.compile("\\.");
//
//  /**
//   * {@inheritDoc}
//   */
//  @Override
//  public VertexData readVertex(final String line) {
//    // 0|A|3 k1 5 v1 k2 5 v2 k3 5 v3|a.1.0 1 k1 5 v1|b.1.0 1 k1 5 v1|1 0
//    String[] lineTokens = getLineTokens(line);
//
//    Long vertexID = readVertexID(lineTokens[0]);
//    String label = readLabel(lineTokens[1]);
//    Map<String, Object> properties = readProperties(lineTokens[2]);
//    Set<Long> outEdges = readEdges(lineTokens[3]);
//    Iterable<EdgeData> inEdges = readEdges(lineTokens[4]);
//    Iterable<Long> graphs = readGraphs(lineTokens[5]);
//
//    return VertexFactory
//      .createDefaultVertex(vertexID, label, properties, outEdges, inEdges,
//        graphs);
//  }
//
//  /**
//   * {@inheritDoc}
//   */
//  @Override
//  public List<VertexData> readVertexList(String line) {
//    return null;
//  }
//
//  /**
//   * {@inheritDoc}
//   */
//  @Override
//  public boolean supportsVertexLists() {
//    return false;
//  }
//
//  /**
//   * Separates the whole line using {@code LINE_TOKEN_SEPARATOR}.
//   *
//   * @param line single input line
//   * @return token array
//   */
//  private String[] getLineTokens(final String line) {
//    return LINE_TOKEN_SEPARATOR.split(line);
//  }
//
//  /**
//   * Reads the vertex id from the given token.
//   *
//   * @param token vertex id token
//   * @return vertex id
//   */
//  private Long readVertexID(final String token) {
//    return Long.parseLong(token);
//  }
//
//  /**
//   * Reads the labels by splitting the token using {@code
//   * VALUE_TOKEN_SEPARATOR}.
//   *
//   * @param token string including labels
//   * @return label list
//   */
//  private String readLabel(final String token) {
//    return token;
//  }
//
//  /**
//   * Reads the properties into a map. Uses the property-type to correctly
// decode
//   * the property-value.
//   *
//   * @param token string including properties
//   * @return key-value-map
//   */
//  private Map<String, Object> readProperties(final String token) {
//    String[] valueTokens = VALUE_TOKEN_SEPARATOR.split(token);
//    int propertyCount = Integer.parseInt(valueTokens[0]);
//    Map<String, Object> properties = null;
//    if (propertyCount > 0) {
//      properties = Maps.newHashMapWithExpectedSize(propertyCount);
//      for (int i = 1; i < valueTokens.length; i += 3) {
//        properties.put(valueTokens[i],
//          decodeValue(valueTokens[i + 1], valueTokens[i + 2]));
//      }
//    }
//    return properties;
//  }
//
//  /**
//   * Reads edges from a given string by splitting with
//   * {@code LIST_TOKEN_SEPARATOR}.
//   *
//   * @param token string including edges
//   * @return edge list
//   */
//  private Iterable<EdgeData> readEdges(final String token) {
//    List<EdgeData> edgeDatas = null;
//    if (!"".equals(token)) {
//      final String[] edgeStrings = LIST_TOKEN_SEPARATOR.split(token);
//      edgeDatas = Lists.newArrayListWithCapacity(edgeStrings.length);
//      for (String edgeString : edgeStrings) {
//        int propStartIdx = edgeString.indexOf(VALUE_TOKEN_SEPARATOR
// .toString());
//        // parse edge key
//        String edgeKey = edgeString.substring(0, propStartIdx);
//        String[] edgeKeyTokens = EDGE_KEY_TOKEN_SEPARATOR.split(edgeKey);
//        String edgeLabel = edgeKeyTokens[0];
//        Long otherID = Long.valueOf(edgeKeyTokens[1]);
//        Long edgeIndex = Long.valueOf(edgeKeyTokens[2]);
//        // parse edge properties
//        Map<String, Object> edgeProperties =
//          readProperties(edgeString.substring(propStartIdx + 1));
//        edgeDatas.add(EdgeFactory
//          .createDefaultEdge(otherID, edgeLabel, edgeIndex, edgeProperties));
//      }
//    }
//    return edgeDatas;
//  }
//
//  /**
//   * Reads graph identifiers from a given string by splitting with {@code
//   * VALUE_TOKEN_SEPARATOR}.
//   *
//   * @param lineToken string including graph identifiers
//   * @return graph list
//   */
//  private Iterable<Long> readGraphs(String lineToken) {
//    String[] valueTokens = VALUE_TOKEN_SEPARATOR.split(lineToken);
//    int graphCount = Integer.parseInt(valueTokens[0]);
//    List<Long> graphs = Lists.newArrayListWithCapacity(graphCount);
//    for (int i = 1; i < valueTokens.length; i++) {
//      graphs.add(Long.parseLong(valueTokens[i]));
//    }
//    return graphs;
//  }
//
//  /**
//   * Parses a property-value by using its property-type.
//   *
//   * @param type  property-type
//   * @param value property-value
//   * @return parsed value
//   */
//  private Object decodeValue(String type, String value) {
//    Object o;
//    switch (Byte.parseByte(type)) {
//    case GConstants.TYPE_BOOLEAN:
//      o = Boolean.valueOf(value);
//      break;
//    case GConstants.TYPE_INTEGER:
//      o = Integer.valueOf(value);
//      break;
//    case GConstants.TYPE_LONG:
//      o = Long.valueOf(value);
//      break;
//    case GConstants.TYPE_FLOAT:
//      o = Long.valueOf(value);
//      break;
//    case GConstants.TYPE_DOUBLE:
//      o = Double.valueOf(value);
//      break;
//    case GConstants.TYPE_STRING:
//      o = value;
//      break;
//    default:
//      throw new UnsupportedTypeException(
//        type + " not supported by this reader");
//    }
//    return o;
//  }
//}
