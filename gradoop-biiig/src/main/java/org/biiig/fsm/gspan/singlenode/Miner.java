package org.biiig.fsm.gspan.singlenode;

import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.DfsCodeGrower;
import org.biiig.fsm.gspan.common.DfsCodeMapper;
import org.biiig.fsm.gspan.common.GSpanEdge;
import org.biiig.fsm.gspan.common.GSpanGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by peet on 20.07.15.
 *
 * grows a collection of DFS codes mapped to a collection of GSPan graphs and
 * records local support in a new dedicated thread
 */
public class Miner extends AbstractDfsEncoder {
  /**
   * constructor
   * @param worker worker initializing the miner
   */
  public Miner(GSpanWorker worker) {
    super(worker);
  }
  /**
   * grows globally frequent DFS codes which are supported by graphs held
   * on the starting worker
   */
  @Override
  public void run() {

    // index graph -> coverage -> DFS code -> Collection of mappers

    // background:
    // 1. every graph may have multiple minimum DFS codes covering the same
    // elements of a graph
    // 2. every DFS code may be mapped to different graph elements

    Map<GSpanGraph, Map<Integer, Map<DfsCode, List<DfsCodeMapper>>>>
      graphCoverageCodeMappers = new HashMap<>();

    // for each local DFS code
    for (Map.Entry<DfsCode, Map<GSpanGraph, Collection<DfsCodeMapper>>>
      dfsCodeSupporterMappersEntry :
      worker.getDfsCodeSupporterMappersMap().entrySet()) {

      DfsCode parentCode = dfsCodeSupporterMappersEntry.getKey();

      // if globally frequent
      if (worker.getGrowableDfsCodes().contains(parentCode)) {

        // for each supporting graph (supporter)
        for (Map.Entry<GSpanGraph, Collection<DfsCodeMapper>>
          supporterMappersEntry :
          dfsCodeSupporterMappersEntry.getValue().entrySet()) {

          GSpanGraph supporter = supporterMappersEntry.getKey();

          // create new index entry for current supporter
          // not created before
          Map<Integer, Map<DfsCode, List<DfsCodeMapper>>> coverageCodeMappers =
            graphCoverageCodeMappers.get(supporter);

          if (coverageCodeMappers == null) {
            coverageCodeMappers = new HashMap<>();
            graphCoverageCodeMappers.put(supporter, coverageCodeMappers);
          }

          // for each mapper of the current supporter
          for (DfsCodeMapper parentMapper : supporterMappersEntry.getValue()) {

            // for each edge of the current supporter
            for (GSpanEdge edge : supporter.getEdges()) {

              // create new grower
              DfsCodeGrower grower = new DfsCodeGrower();

              // if grower can grow to valid DFS code by current edge
              // pre-growth pruning: all the pruning that does not require
              // comparison to other codes is done within the grower,
              // see DfsCodeGrower.grow(...) for further information
              if (grower.grow(parentCode, parentMapper, edge)) {

                // get child code and mapper
                DfsCode childCode = grower.getGrownCode();
                DfsCodeMapper childMapper = grower.getGrownMapper();

                // find other mappers covering the same elements of the
                // supporter or create new index entry
                Integer coverage = childMapper.getCoverage();

                Map<DfsCode, List<DfsCodeMapper>> codeMappers =
                  coverageCodeMappers.get(coverage);

                if (codeMappers == null) {
                  codeMappers = new HashMap<>();
                  coverageCodeMappers.put(coverage, codeMappers);
                }

                List<DfsCodeMapper> mappers = codeMappers.get(childCode);

                if (mappers == null) {
                  mappers = new LinkedList<>();
                  codeMappers.put(childCode, mappers);
                }

                // add current mapper to index
                mappers.add(childMapper);
              }
            }
          }
        }
      }
    }

    // all parent DFS codes are processed now,
    // so we delete them together with their associated mappers
    worker.getDfsCodeSupporterMappersMap().clear();

    // for each graph
    // post-growth pruning: all the pruning that requires comparison to
    // other codes is done here supporter-wise
    for (Map.Entry<GSpanGraph, Map<Integer, Map<DfsCode, List<DfsCodeMapper>>>>
      graphCoverageCodeMappersEntry : graphCoverageCodeMappers.entrySet()) {

      GSpanGraph graph = graphCoverageCodeMappersEntry.getKey();

      // for each coverage
      for (Map<DfsCode, List<DfsCodeMapper>> codeMappers :
        graphCoverageCodeMappersEntry.getValue().values()) {

        DfsCode minCode = null;
        DfsCodeMapper minMapper = null;

        // find the minimum DFS code
        for (DfsCode code : codeMappers.keySet()) {
          if (minCode == null || code.compareTo(minCode) < 0) {
            minCode = code;
            // two mappers can only have the same coverage for "mirrored"
            // structures and thus a random one can be chosen
            minMapper = codeMappers.get(code).get(0);
          }
        }

        // and report this DFS code and its mappers
        addDfsCodeSupporterMapper(minCode, graph, minMapper);
      }
    }
  }
}
