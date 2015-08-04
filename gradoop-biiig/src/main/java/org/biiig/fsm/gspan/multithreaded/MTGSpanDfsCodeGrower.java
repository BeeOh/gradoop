package org.biiig.fsm.gspan.multithreaded;

import org.biiig.fsm.gspan.DfsCode;
import org.biiig.fsm.gspan.DfsCodeMapper;
import org.biiig.fsm.gspan.DfsEdge;
import org.biiig.fsm.gspan.GSpanEdge;
import org.biiig.fsm.gspan.GSpanGraph;
import org.biiig.fsm.gspan.GSpanVertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by peet on 20.07.15.
 */
public class MTGSpanDfsCodeGrower extends MTGSpanRunnable {
  public MTGSpanDfsCodeGrower(MTGSpanWorker worker) {
    super(worker);
  }

  @Override
  public void run() {

    // select mappers of globally frequent DFS codes

    Map<DfsCode,Collection<DfsCodeMapper>> growableDfsCodeMappersMap =
      new HashMap();

    for(Map.Entry<DfsCode,Collection<DfsCodeMapper>> dfsCodeMappers : worker
      .getDfsCodeMappersMap().entrySet()) {

      DfsCode dfsCode = dfsCodeMappers.getKey();

      if(worker.getGrowableDfsCodes().contains(dfsCode)) {
        Collection<DfsCodeMapper> mappers = dfsCodeMappers.getValue();
        Collection<DfsCodeMapper> growableMappers = new ArrayList<>();

        for(DfsCodeMapper mapper : mappers) {
          if(mapper.getEdges().size() < mapper.getGraph().getEdges().size()) {
            growableMappers.add(mapper);
          }
        }

        if(!growableMappers.isEmpty()) {
          growableDfsCodeMappersMap.put(dfsCode, growableMappers);
        }
      }
    }

    // drop mappers of infrequent DFS codes
    worker.getDfsCodeMappersMap().clear();

    // grow frequent DFS codes and generate new mappers

    // for each globally frequent DFS code
    for(Map.Entry<DfsCode,Collection<DfsCodeMapper>> dfsCodeMappers :
      growableDfsCodeMappersMap.entrySet()) {

      DfsCode parentDfsCode = dfsCodeMappers.getKey();


      // for each mapper grow new DFS codes
      for(DfsCodeMapper parentMapper : dfsCodeMappers.getValue()) {
        GSpanGraph graph = parentMapper.getGraph();
        GSpanVertex rightmostVertex = parentMapper.getRightmostVertex();
        Integer rightmostPosition = parentMapper.getRightmostVertexPosition();

        // backward edges from rightmost vertex to value vertex
        Map<GSpanEdge,GSpanVertex> backwardEdges = new HashMap<>();
        // forward edges from value vertex to other vertex
        Map<GSpanEdge,GSpanVertex> forwardEdges = new HashMap<>();

        // all growth edges from rightmost vertex
        for(GSpanEdge edge : graph.getAdjacencyList(rightmostVertex)) {
          if(parentMapper.isValidForGrowth(edge)) {

            GSpanVertex otherVertex = edge.getOtherVertex(rightmostVertex);

            // backward edge
            if(parentMapper.contains(otherVertex)) {
              if(parentMapper.growsBackwardToMinimalCodeBy(edge, otherVertex)){
                backwardEdges.put(edge,otherVertex);
              }
              // forward edge
            } else {
              forwardEdges.put(edge,rightmostVertex);
            }
          }
        }

        // forward growth edges from rightmost tail
        for(Integer position : parentDfsCode.getRightmostTailPositions()) {
          GSpanVertex vertex = parentMapper.getVertex(position);

          for(GSpanEdge edge : graph.getAdjacencyList(vertex)) {
            if(parentMapper.isValidForGrowth(edge) && !parentMapper.contains
              (edge.getOtherVertex(vertex))) {
              forwardEdges.put(edge, vertex);
            }
          }
        }


        // generate new DFS codes for backward edges

        for(Map.Entry<GSpanEdge,GSpanVertex> backwardEdge
          : backwardEdges.entrySet()) {

          GSpanEdge edge = backwardEdge.getKey();
          GSpanVertex fromVertex = rightmostVertex;
          GSpanVertex toVertex = backwardEdge.getValue();

          Integer fromPosition = rightmostPosition;
          Integer toPosition = parentMapper.positionOf(toVertex);

          Boolean outgoing = edge.getSourceVertex() == fromVertex;

          DfsEdge dfsEdge = new DfsEdge(fromPosition,toPosition,fromVertex
            .getLabel(),outgoing,edge.getLabel(),toVertex.getLabel());

          DfsCode childDfsCode = parentDfsCode.clone();
          childDfsCode.add(dfsEdge);

          DfsCodeMapper childMapper = growDfsCodeMapper(parentMapper, edge);

          addMapper(childDfsCode, childMapper);
          addSupporter(childDfsCode, graph);
        }

        for(Map.Entry<GSpanEdge,GSpanVertex> forwardEdge
          : forwardEdges.entrySet()) {

          GSpanEdge edge = forwardEdge.getKey();
          GSpanVertex fromVertex = forwardEdge.getValue();
          GSpanVertex toVertex = edge.getOtherVertex(fromVertex);

          Integer fromPosition = parentMapper.positionOf(fromVertex);
          Integer toPosition = rightmostPosition + 1;

          Boolean outgoing = edge.getSourceVertex() == fromVertex;

          DfsEdge dfsEdge = new DfsEdge(fromPosition,toPosition,fromVertex
            .getLabel(),outgoing,edge.getLabel(),toVertex.getLabel());

          DfsCode childDfsCode = parentDfsCode.clone();
          childDfsCode.add(dfsEdge);

          DfsCodeMapper childMapper = growDfsCodeMapper(parentMapper, edge);
          childMapper.add(toVertex);

          addMapper(childDfsCode, childMapper);
          addSupporter(childDfsCode, graph);
        }
      }
    }
  }

  private DfsCodeMapper growDfsCodeMapper(DfsCodeMapper parentMapper,
    GSpanEdge edge) {

    DfsCodeMapper childMapper = parentMapper.clone();
    childMapper.add(edge);
    return childMapper;
  }

  private void addMapper(DfsCode dfsCode, DfsCodeMapper dfsCodeMapper) {
    Collection<DfsCodeMapper> mappers =
      worker.getDfsCodeMappersMap().get(dfsCode);

    if (mappers == null) {
      mappers = new ArrayList<>();
      worker.getDfsCodeMappersMap().put(dfsCode,mappers);
    } mappers.add(dfsCodeMapper);
  }

  private void addSupporter(DfsCode dfsCode, GSpanGraph graph) {
    Set<GSpanGraph> supporters =
      worker.getDfsCodeSupportersMap().get(dfsCode);

    if (supporters == null) {
      supporters = new HashSet<>();
      worker.getDfsCodeSupportersMap().put(dfsCode,supporters);
    }

    supporters.add(graph);
  }
}
