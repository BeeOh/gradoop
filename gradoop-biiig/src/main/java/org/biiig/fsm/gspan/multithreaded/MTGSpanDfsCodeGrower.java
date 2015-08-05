package org.biiig.fsm.gspan.multithreaded;

import org.biiig.fsm.gspan.DfsCode;
import org.biiig.fsm.gspan.DfsCodeMapper;
import org.biiig.fsm.gspan.DfsEdge;
import org.biiig.fsm.gspan.GSpanEdge;
import org.biiig.fsm.gspan.GSpanGraph;
import org.biiig.fsm.gspan.GSpanVertex;

import java.util.*;

/**
 * Created by peet on 20.07.15.
 */
public class MTGSpanDfsCodeGrower extends MTGSpanAbstractDfsEncoder {


  public MTGSpanDfsCodeGrower(MTGSpanWorker worker) {
    super(worker);
  }

  @Override
  public void run() {

    Map<GSpanGraph,Map<Integer,Map<DfsCode,List<DfsCodeMapper>>>>
      supporterCoverageDfsCodeMappers = new HashMap<>();

    // for each local DFS code
    for(Map.Entry<DfsCode, Map<GSpanGraph, Collection<DfsCodeMapper>>>
      dfsCodeSupporterMappersEntry : worker.getDfsCodeSupporterMappersMap().entrySet()) {

      DfsCode parentDfsCode = dfsCodeSupporterMappersEntry.getKey();

      // if globally frequent
      if(worker.getGrowableDfsCodes().contains(parentDfsCode)) {

        // for each supporting graph
        for(Map.Entry<GSpanGraph, Collection<DfsCodeMapper>>
          supporterMappersEntry :
          dfsCodeSupporterMappersEntry.getValue().entrySet()) {

          GSpanGraph supporter = supporterMappersEntry.getKey();

          Map<Integer, Map<DfsCode, List<DfsCodeMapper>>> coverageDfsCodeMappers =
            supporterCoverageDfsCodeMappers.get(supporter);

          if(coverageDfsCodeMappers == null) {
            coverageDfsCodeMappers = new HashMap<>();
            supporterCoverageDfsCodeMappers.put(supporter,
              coverageDfsCodeMappers);
          }

          // for each mapper
          for(DfsCodeMapper parentMapper : supporterMappersEntry.getValue()) {
            GSpanVertex rightmostVertex = parentMapper.getRightmostVertex();
            Integer rightmostPosition = parentMapper.getRightmostVertexPosition();

            // backward edges from rightmost vertex to value vertex
            Map<GSpanEdge,GSpanVertex> backwardEdges = new HashMap<>();
            // forward edges from value vertex to other vertex
            Map<GSpanEdge,GSpanVertex> forwardEdges = new HashMap<>();

            // all growth edges from rightmost vertex
            for(GSpanEdge edge : supporter.getAdjacencyList(rightmostVertex)) {
              if(parentMapper.isValidForGrowth(edge)) {

                GSpanVertex otherVertex = edge.getOtherVertex(rightmostVertex);

                // backward edge
                if(parentMapper.contains(otherVertex)) {
                  //if(parentMapper.growsBackwardToMinimalCodeBy(edge,
                  // otherVertex)){
                  backwardEdges.put(edge, otherVertex);
                  //}
                  // forward edge
                } else {
                  forwardEdges.put(edge, rightmostVertex);
                }
              }
            }

            // forward growth edges from rightmost tail
            for(Integer position : parentDfsCode.getRightmostTailPositions()) {
              GSpanVertex vertex = parentMapper.getVertex(position);

              for(GSpanEdge edge : supporter.getAdjacencyList(vertex)) {
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

              addCoverageDfsCodeMapper(coverageDfsCodeMappers, childDfsCode,
                childMapper);
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

              // add DFS code coverage mapper

              addCoverageDfsCodeMapper(coverageDfsCodeMappers, childDfsCode,
                childMapper);

            }
          }
        }
      }
    }

    worker.getDfsCodeSupporterMappersMap().clear();

    // post growth pruning
    for(Map.Entry<GSpanGraph,Map<Integer,Map<DfsCode,List<DfsCodeMapper>>>>
      supporterCoverageDfsCodeMappersEntry : supporterCoverageDfsCodeMappers
      .entrySet()) {

      GSpanGraph supporter = supporterCoverageDfsCodeMappersEntry.getKey();

      for(Map<DfsCode,List<DfsCodeMapper>> dfsCodeMappers :
        supporterCoverageDfsCodeMappersEntry.getValue().values()) {

        DfsCode minDfsCode = null;
        DfsCodeMapper minDfsCodeMapper = null;

        for(DfsCode dfsCode : dfsCodeMappers.keySet()) {
          if(minDfsCode == null || dfsCode.compareTo(minDfsCode) < 0) {
            minDfsCode = dfsCode;
            minDfsCodeMapper = dfsCodeMappers.get(dfsCode).get(0);
          }
        }

        addDfsCodeSupporterMapper(minDfsCode, supporter, minDfsCodeMapper);
      }
    }  }

  private void addCoverageDfsCodeMapper(
    Map<Integer, Map<DfsCode, List<DfsCodeMapper>>> coverageMapperDfsCodeMaps,
    DfsCode dfsCode, DfsCodeMapper mapper) {
    Integer coverage = mapper.getCoverage();

    Map<DfsCode, List<DfsCodeMapper>> dfsCodeMappers =
      coverageMapperDfsCodeMaps.get(coverage);

    if(dfsCodeMappers == null) {
      dfsCodeMappers = new HashMap<>();
      coverageMapperDfsCodeMaps.put(coverage,dfsCodeMappers);
    }

    List<DfsCodeMapper> mappers = dfsCodeMappers.get(dfsCode);

    if(mappers == null) {
      mappers = new LinkedList<>();
      dfsCodeMappers.put(dfsCode,mappers);
    }

    mappers.add(mapper);
  }


  private DfsCodeMapper growDfsCodeMapper(DfsCodeMapper parentMapper,
    GSpanEdge edge) {

    DfsCodeMapper childMapper = parentMapper.clone();
    childMapper.add(edge);
    return childMapper;
  }

}
