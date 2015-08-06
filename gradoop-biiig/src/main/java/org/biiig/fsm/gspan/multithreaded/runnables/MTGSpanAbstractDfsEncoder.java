package org.biiig.fsm.gspan.multithreaded.runnables;

import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.DfsCodeMapper;
import org.biiig.fsm.gspan.common.GSpanGraph;
import org.biiig.fsm.gspan.multithreaded.MTGSpanWorker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peet on 05.08.15.
 */
public abstract class MTGSpanAbstractDfsEncoder extends
  MTGSpanAbstractRunnable {

  /**
   * constructor
   *
   * @param worker calling worker
   */
  public MTGSpanAbstractDfsEncoder(MTGSpanWorker worker) {
    super(worker);
  }

  protected void addDfsCodeSupporterMapper(DfsCode dfsCode,
    GSpanGraph supporter, DfsCodeMapper mapper) {
    // add graph to existing DFS code or create new entry
    Map<GSpanGraph,Collection<DfsCodeMapper>> supporterMappers =
      worker.getDfsCodeSupporterMappersMap().get(dfsCode);

    if(supporterMappers == null) {
      supporterMappers = new HashMap<>();
      worker.getDfsCodeSupporterMappersMap().put(dfsCode, supporterMappers);
    }

    // add mapper to existing DFS codes or create new entry
    Collection<DfsCodeMapper> mappers = supporterMappers.get(supporter);

    if (mappers == null) {
      mappers = new ArrayList<>();
      supporterMappers.put(supporter,mappers);
    }

    mappers.add(mapper);
  }


}
