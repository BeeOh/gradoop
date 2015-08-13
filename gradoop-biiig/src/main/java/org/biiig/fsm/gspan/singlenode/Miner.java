package org.biiig.fsm.gspan.singlenode;

import org.apache.commons.collections.CollectionUtils;

import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.DfsCodeGrower;
import org.biiig.fsm.gspan.common.DfsCodeMapper;
import org.biiig.fsm.gspan.common.GSpanEdge;
import org.biiig.fsm.gspan.common.SearchSpaceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * grows a collection of DFS codes mapped to a collection of GSPan graphs and
 * records local support in a new dedicated thread
 *
 * Created by pe3t on 20.07.15.
 */
public class Miner extends AbstractRunnable {
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

    // for each search space item (search graph and DFS code mappers)
    for (SearchSpaceItem item : worker.getSearchSpace()) {

      Map<Integer, List<DfsCodeMapper>> coverageIndex = new HashMap<>();

      Collection<DfsCode> growableDfsCodes = CollectionUtils.intersection(
        item.getMinimumDfsCodes(), worker.getGrowableDfsCodes());

      // for each edge of the search graph
      for (GSpanEdge edge : item.getGraph().getEdges()) {

        // for each supported and globally frequent minimal DFS code as parent
        for (DfsCode parentCode : growableDfsCodes) {

          // for each mapper of the current DFS code
          for (DfsCodeMapper parentMapper : item.getMappers(parentCode)) {

            // create new grower
            DfsCodeGrower grower = new DfsCodeGrower();

            // PRE-PRUNING
            // if "grower can grow to valid DFS code by current edge" covers
            // all pruning pruning steps which do not require comparison to
            // other grown codes
            if (grower.grow(parentMapper, edge)) {

              DfsCodeMapper childMapper = grower.getGrownMapper();

              // determine coverage hash code of child mapper
              Integer coverage = childMapper.getCoverage();

              // try to find the list of indexed mappers covering the same
              // subgraph
              List<DfsCodeMapper> coverageMappers = coverageIndex.get(coverage);

              // or add coverage entry to index
              if (coverageMappers == null) {
                coverageMappers = new ArrayList<>();
                coverageIndex.put(coverage, coverageMappers);
              }

              // add mapper to index
              coverageMappers.add(childMapper);
            }
          }
        }
      }
      // POST PRUNING
      // remove duplicates that can't be eliminated by pre pruning

      // reset mapper index
      Map<DfsCode, List<DfsCodeMapper>> codeMappersIndex = new HashMap<>();
      item.setDfsCodeMappersIndex(codeMappersIndex);

      // for mapper list if each subgraph (coverage hashcode)
      for (List<DfsCodeMapper> mappers : coverageIndex.values()) {

        DfsCode minDfsCode = null;
        List<DfsCodeMapper> minMappers = new ArrayList<>();

        // determine minimum DFS code
        if (mappers.size() == 1) {

          // skip comparison if only one mapper
          minDfsCode = mappers.get(0).getDfsCode();
          minMappers = mappers;

        } else {

          // find minimum DFS code for all mappers of same subgraph
          for (DfsCodeMapper mapper : mappers) {

            DfsCode mapperDfsCode = mapper.getDfsCode();

            if (minDfsCode == null) {
              minDfsCode = mapperDfsCode;
              minMappers.add(mapper);
            } else if (minDfsCode.equals(mapperDfsCode)) {
              minMappers.add(mapper);
            } else if (minDfsCode.compareTo(mapperDfsCode) > 0) {
              minDfsCode = mapperDfsCode;
              minMappers.clear();
              minMappers.add(mapper);
            }
          }
        }

        // add minimum mappers to code index
        List<DfsCodeMapper> sameCodeMappers = codeMappersIndex.get(minDfsCode);

        if (sameCodeMappers == null) {
          codeMappersIndex.put(minDfsCode, minMappers);
        } else {
          sameCodeMappers.addAll(minMappers);
        }
      }
    }
  }
}
