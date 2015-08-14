package org.biiig.fsm.gspan.flink;

import org.biiig.fsm.gspan.common.DfsCode;
import org.biiig.fsm.gspan.common.DfsEdge;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DfsCodeCompressorTest {

  @Test
  public void testCompressionAndDecompression() throws Exception {

    DfsCode originalCode = new DfsCode();
    originalCode.add(new DfsEdge(0, 0, true, 2, 1, 1));
    originalCode.add(new DfsEdge(1, 1, true, 2, 0, 0));

    System.out.println(originalCode);

    byte[] bytes = DfsCodeCompressor.compress(originalCode);
    System.out.println(bytes);

    DfsCode resultCode = DfsCodeCompressor.decompress(bytes);
    System.out.println(resultCode);


    assertTrue(originalCode.equals(resultCode));
  }

}