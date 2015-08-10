package org.biiig.fsm.gspan.singlenode;

import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.DfsCodeMapper;
import org.biiig.fsm.gspan.common.GSpanGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * generalization of all runnables generating DFS code and mappers
 *
 * Created by p3et on 05.08.15.
 */
public abstract class AbstractDfsEncoder extends AbstractRunnable {
  /**
   * constructor
   * @param worker calling worker
   */
  public AbstractDfsEncoder(GSpanWorker worker) {
    super(worker);
  }
  /**
   *
   * @param code DFS code
   * @param supporter graph supporting the DFS code
   * @param mapper mapper describing the mapping between DFS code and the graph
   */
  protected void addDfsCodeSupporterMapper(DfsCode code,
    GSpanGraph supporter, DfsCodeMapper mapper) {

    // add graph to existing DFS code or create new entry
    Map<GSpanGraph, Collection<DfsCodeMapper>> supporterMappers =
      worker.getDfsCodeSupporterMappersMap().get(code);

    if (supporterMappers == null) {
      supporterMappers = new HashMap<>();
      worker.getDfsCodeSupporterMappersMap().put(code, supporterMappers);
    }

    // add mapper to existing DFS codes or create new entry
    Collection<DfsCodeMapper> mappers = supporterMappers.get(supporter);

    if (mappers == null) {
      mappers = new ArrayList<>();
      supporterMappers.put(supporter, mappers);
    }

    mappers.add(mapper);
  }


}
