package org.biiig.fsm.gspan.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by p3et on 06.08.15.
 *
 * grows a DFS code and a given mapper by a given edge,
 * if edge is potentially valid for growth (pre-growth pruning only)
 */
public class DfsCodeGrower {

  /**
   * resulting DFS code - graph mapper
   */
  private DfsCodeMapper grownMapper = null;

  /**
   * start growth validation and generate new code and mapper, if valid
   * @param mapper initial mapper
   * @param edge growth candidate edge
   * @return  true, if edge was valid for growth and thus a new code and a new
   *          mapper were generated
   */
  public boolean grow(DfsCodeMapper mapper, GSpanEdge edge) {

    // 1st pruning step :
    // edge is lexicographically greater or equal to the root edge and
    // edge was not already mapped
    if (edge.compareTo(mapper.getFirstMappedEdge()) >= 0 &&
      !mapper.contains(edge)) {

      // load data objects necessary for further pruning decisions
      List<GSpanVertex> rightmostPath = getRightMostTail(mapper);
      GSpanVertex rightmostVertex = mapper.getRightmostVertex();

      GSpanVertex sourceVertex = edge.getSourceVertex();
      GSpanVertex targetVertex = edge.getTargetVertex();

      DfsEdge dfsEdge = null;
      GSpanVertex newVertex = null;

      // 2nd pruning step :
      // ensure GSpan growth rules

      // if source vertex is rightmost vertex, backward growth is possible
      if (sourceVertex == rightmostVertex) {

        // rightmost position
        Integer sourceVertexPosition = mapper.getMappedVertices().size() - 1;
        Integer targetVertexPosition = mapper.positionOf(targetVertex);

        // if target vertex is not mapped (forward growth)
        if (targetVertexPosition < 0) {
          // report target vertex as unmapped vertex
          newVertex = targetVertex;
          // set new position by adding one to rightmost position
          targetVertexPosition = sourceVertexPosition + 1;
        }

        // create new DFS edge as growth candidate
        dfsEdge = new DfsEdge(
          sourceVertexPosition, sourceVertex.getLabel(),
          true, edge.getLabel(),
          targetVertexPosition, targetVertex.getLabel()
        );

        // if target vertex is rightmost vertex, backward growth is possible
      } else if (targetVertex == rightmostVertex) {

        // rightmost position
        Integer targetVertexPosition = mapper.getMappedVertices().size() - 1;
        Integer sourceVertexPosition = mapper.positionOf(sourceVertex);

        // if source vertex is not mapped (forward growth)
        if (sourceVertexPosition < 0) {
          // report source vertex as unmapped vertex
          newVertex = sourceVertex;
          // set new position by adding one to rightmost position
          sourceVertexPosition = targetVertexPosition + 1;
        }

        // create new DFS edge as growth candidate
        dfsEdge = new DfsEdge(
          targetVertexPosition, targetVertex.getLabel(), false, edge.getLabel(),
          sourceVertexPosition, sourceVertex.getLabel()
        );

        // if source vertex is on rightmost path, forward growth is possible
      } else if (rightmostPath.contains(sourceVertex) &&
        !mapper.contains(targetVertex)) {

        Integer sourceVertexPosition = mapper.positionOf(sourceVertex);
        // one position after rightmost position
        Integer targetVertexPosition = mapper.getMappedVertices().size();

        // report target vertex as unmapped vertex
        newVertex = targetVertex;

        // create new DFS edge as growth candidate
        dfsEdge = new DfsEdge(
          sourceVertexPosition, sourceVertex.getLabel(), true, edge.getLabel(),
          targetVertexPosition, targetVertex.getLabel()
        );

        // grow forward from target vertex
      } else if (rightmostPath.contains(targetVertex) &&
        !mapper.contains(sourceVertex)) {

        Integer targetVertexPosition = mapper.positionOf(targetVertex);
        // one position after rightmost position
        Integer sourceVertexPosition = mapper.getMappedVertices().size();

        // report source vertex as unmapped vertex
        newVertex = sourceVertex;

        // create new DFS edge as growth candidate
        dfsEdge = new DfsEdge(
          targetVertexPosition, targetVertex.getLabel(), false, edge.getLabel(),
          sourceVertexPosition, sourceVertex.getLabel()
        );
      }

      // if a dfsEdge was instantiated, edge valid for growth
      if (dfsEdge != null) {

         // add edge to mapper
        grownMapper = mapper.clone();

        if (newVertex != null) {
          grownMapper.map(newVertex);
        }

        grownMapper.map(dfsEdge, edge);
      }
    }
    return grownMapper != null;
  }
  /**
   * encapsulation of rightmost tail determination, e.g., the shortest path
   * between rightmost and root vertex, but without rightmost vertex; tail is
   * used instead of path as rightmost vertex is checked separately and thus
   * fewer comparisons are necessary when checking containment
   *
   * @param mapper mapper to GSPan graph
   * @return List of vertices in inverse order of discovery position (time)
   * without rightmost vertex; DFS-code tree root vertex will be last entry
   */
  private List<GSpanVertex> getRightMostTail(DfsCodeMapper mapper) {

    DfsCode dfsCode = mapper.getDfsCode();
    List<GSpanVertex> rightMostTail = new ArrayList<>();

    // get last discovery position of vertex before rightmost vertex
    int lastFromPosition = dfsCode.getLastDfsEdge().getFromPosition();

    // and add corresponding vertex to path
    rightMostTail.add(mapper.getMappedVertices().get(lastFromPosition));

    // inverse for each DFS edge of the code
    for (int index = dfsCode.getDfsEdges().size() - 1; index >= 0; index--) {
      DfsEdge dfsEdge = dfsCode.getDfsEdges().get(index);

      // if edge ends in last start position (e.g., in the first round, if
      // edge ends one edge before rightmost vertex)
      if (dfsEdge.getToPosition() == lastFromPosition) {
        lastFromPosition = dfsEdge.getFromPosition();
        rightMostTail.add(mapper.getMappedVertices().get(lastFromPosition));
      }
    }

    return rightMostTail;
  }

  public DfsCodeMapper getGrownMapper() {
    return grownMapper;
  }
}
