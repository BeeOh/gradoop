package org.biiig.fsm.gspan.multithreaded.runnables;

import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.DfsCodeGrower;
import org.biiig.fsm.gspan.common.DfsCodeMapper;
import org.biiig.fsm.gspan.common.GSpanEdge;
import org.biiig.fsm.gspan.common.GSpanGraph;
import org.biiig.fsm.gspan.multithreaded.MTGSpanWorker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by peet on 20.07.15.
 */
public class MTGSpanEdgeSetBasedDfsCodeGrower extends MTGSpanAbstractDfsEncoder {


  public MTGSpanEdgeSetBasedDfsCodeGrower(MTGSpanWorker worker) {
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

            // for each edge
            for(GSpanEdge edge : supporter.getEdges()) {

              DfsCodeGrower grower = new DfsCodeGrower();

              if(grower.grow(parentDfsCode,parentMapper,edge)) {

                DfsCode childDfsCode = grower.getGrownDfsCode();
                DfsCodeMapper childMapper = grower.getGrownMapper();

                addCoverageDfsCodeMapper(coverageDfsCodeMappers, childDfsCode,
                  childMapper);
              }
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
    }
  }



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


}
