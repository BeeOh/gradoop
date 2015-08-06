package org.biiig.fsm.gspan.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peet on 06.08.15.
 */
public class DfsCodeGrower {

  private DfsCode grownDfsCode = null;
  private DfsCodeMapper grownMapper = null;

  public boolean grow(DfsCode dfsCode, DfsCodeMapper mapper, GSpanEdge edge) {

    // if edge neither pruned by lexicographic order nor already mapped
    if(edge.compareTo(mapper.getFirstEdge()) >= 0 && !mapper.contains(edge)) {

      // load data objects necessary for DFS code growth decisions
      List<GSpanVertex> rightmostPath = getRightMostPath(dfsCode, mapper);
      GSpanVertex rightmostVertex = mapper.getRightmostVertex();

      GSpanVertex sourceVertex = edge.getSourceVertex();
      GSpanVertex targetVertex = edge.getTargetVertex();

      DfsEdge dfsEdge = null;
      GSpanVertex newVertex = null;

      // grow forward or backward from source vertex
      if(sourceVertex == rightmostVertex) {

        // rightmost position
        Integer sourceVertexPosition = mapper.getVertices().size()-1;
        Integer targetVertexPosition = mapper.positionOf(targetVertex);

        //  forward
        if(targetVertexPosition < 0) {
          newVertex = targetVertex;
          // one position after rightmost position
          targetVertexPosition = sourceVertexPosition + 1;
        }

        dfsEdge = new DfsEdge(
          sourceVertexPosition,
          targetVertexPosition,
          sourceVertex.getLabel(),
          true,
          edge.getLabel(),
          targetVertex.getLabel()
        );

        // grow forward or backward from target vertex
      } else if (targetVertex == rightmostVertex) {

        // rightmost position
        Integer targetVertexPosition = mapper.getVertices().size()-1;
        Integer sourceVertexPosition = mapper.positionOf(sourceVertex);

        // forward
        if(sourceVertexPosition < 0) {
          newVertex = targetVertex;
          // one position after rightmost position
          sourceVertexPosition = targetVertexPosition + 1;
        }

        dfsEdge = new DfsEdge(
          targetVertexPosition,
          sourceVertexPosition,
          targetVertex.getLabel(),
          false,
          edge.getLabel(),
          sourceVertex.getLabel()
        );

        // grow forward from source vertex
      } else if (rightmostPath.contains(sourceVertex)
        && !mapper.contains(targetVertex)) {

        Integer sourceVertexPosition = mapper.positionOf(sourceVertex);
        // one position after rightmost position
        Integer targetVertexPosition = mapper.getVertices().size();
        newVertex = targetVertex;

        dfsEdge = new DfsEdge(
          sourceVertexPosition,
          targetVertexPosition,
          sourceVertex.getLabel(),
          true,
          edge.getLabel(),
          targetVertex.getLabel()
        );

        // grow forward from target vertex
      } else if (rightmostPath.contains(targetVertex)
        && !mapper.contains(sourceVertex)) {

        Integer targetVertexPosition = mapper.positionOf(targetVertex);
        // one position after rightmost position
        Integer sourceVertexPosition = mapper.getVertices().size();

        dfsEdge = new DfsEdge(
          targetVertexPosition,
          sourceVertexPosition,
          targetVertex.getLabel(),
          false,
          edge.getLabel(),
          sourceVertex.getLabel()
        );

      }


      // edge valid for growth
      if(dfsEdge != null) {
        grownDfsCode = dfsCode.clone();
        grownDfsCode.add(dfsEdge);

        grownMapper = mapper.clone();
        grownMapper.add(edge);

        if(newVertex != null) {
          grownMapper.add(newVertex);
        }

      }
    }
    return grownDfsCode != null;
  }

  private List<GSpanVertex> getRightMostPath(DfsCode dfsCode,
    DfsCodeMapper mapper) {
    List<GSpanVertex> rightMostPath = new ArrayList<>();


    DfsEdge lastDfsEdge = dfsCode.getLastDfsEdge();
    int lastFromPosition = lastDfsEdge.getFromPosition();

    rightMostPath.add(mapper.getRightmostVertex());
    rightMostPath.add(mapper.getVertices().get(lastFromPosition));

    for(int index = dfsCode.getDfsEdges().size()-1; index >= 0; index--) {
      DfsEdge dfsEdge = dfsCode.getDfsEdges().get(index);

      if (dfsEdge.getToPosition() == lastFromPosition){
        lastFromPosition = dfsEdge.getFromPosition();
        rightMostPath.add(mapper.getVertices().get(lastFromPosition));
      }
    }

    return rightMostPath;
  }

  public DfsCode getGrownDfsCode() {
    return grownDfsCode;
  }

  public DfsCodeMapper getGrownMapper() {
    return grownMapper;
  }
}
