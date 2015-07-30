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

        // backward edges from rightmost vertex to key (position)
        Collection<GSpanEdge> backwardEdges = new ArrayList<>();
        // forward edges from key (position)
        Collection<GSpanEdge> forwardEdges = new ArrayList<>();

        // growth edges from rightmost vertex
        for(GSpanEdge edge : graph.getAdjacencyList(rightmostVertex)) {
          if(parentMapper.isValidForGrowth(edge)) {

            if(parentMapper.contains(edge.getOtherVertex(rightmostVertex))) {
              backwardEdges.add(edge);
            } else {
              forwardEdges.add(edge);
            }
          }
        }

        // growth edges from rightmost tail
        for(Integer position : parentDfsCode.getRightmostTailPositions()) {
          GSpanVertex vertex = parentMapper.getVertex(position);

          for(GSpanEdge edge : graph.getAdjacencyList(vertex)) {
            if(!backwardEdges.contains(edge) && parentMapper.isValidForGrowth(edge)) {
              forwardEdges.add(edge);
            }
          }
        }

        Collection<GSpanEdge> growthEdges = backwardEdges;
        growthEdges.addAll(forwardEdges);

        // generate new DFS codes for all growth edges

        for(GSpanEdge edge : growthEdges) {
          Integer sourcePosition = parentMapper.positionOf(edge.getSourceVertex());
          Integer targetPosition = parentMapper.positionOf(edge.getTargetVertex());

          Integer fromPosition;
          GSpanVertex fromVertex;
          Integer toPosition;
          GSpanVertex toVertex;
          boolean outgoing;

          // backward edge
          if(sourcePosition > 0 && targetPosition > 0){
            // rightmost vertex is source
            if(sourcePosition > targetPosition) {
              fromPosition = rightmostPosition;
              fromVertex = rightmostVertex;
              toPosition = targetPosition;
              toVertex = parentMapper.getVertex(targetPosition);
              outgoing = true;

              // rightmost vertex is target
            } else {
              fromPosition = sourcePosition;
              fromVertex = parentMapper.getVertex(sourcePosition);
              toPosition = rightmostPosition;
              toVertex = rightmostVertex;
              outgoing = false;
            }
            // forward edge in direction
          } else {
            if(sourcePosition > 0) {
              fromPosition = sourcePosition;
              fromVertex = parentMapper.getVertex(sourcePosition);
              toVertex = edge.getTargetVertex();
              outgoing = true;

              // forward edge inverse direction
            } else {
              fromPosition = targetPosition;
              fromVertex = parentMapper.getVertex(targetPosition);
              toVertex = edge.getSourceVertex();
              outgoing = false;
            }
            toPosition = rightmostPosition + 1;
            parentMapper.add(toVertex);
          }

          DfsEdge dfsEdge = new DfsEdge(fromPosition,toPosition,fromVertex
            .getLabel(),outgoing,edge.getLabel(),toVertex.getLabel());

          DfsCode childDfsCode = parentDfsCode.clone();
          childDfsCode.add(dfsEdge);
          DfsCodeMapper childMapper = parentMapper.clone();
          childMapper.add(edge);

          // add mapper to existing DFS codes or create new entry
          Collection<DfsCodeMapper> mappers =
            worker.getDfsCodeMappersMap().get(childDfsCode);

          if (mappers == null) {
            mappers = new ArrayList<>();
            worker.getDfsCodeMappersMap().put(childDfsCode,mappers);
          }

          // add graph to existing DFS codes or create new entry
          Set<GSpanGraph> supporters =
            worker.getDfsCodeSupportersMap().get(childDfsCode);

          if (supporters == null) {
            supporters = new HashSet<>();
            worker.getDfsCodeSupportersMap().put(childDfsCode,supporters);
          }

          mappers.add(childMapper);
          supporters.add(graph);
        }
      }
    }
  }
}
