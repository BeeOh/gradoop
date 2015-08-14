package org.biiig.fsm.gspan.flink;

import org.biiig.fsm.gspan.common.DfsCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by p3et on 13.08.15.
 *
 * class providing static methods for DFS code compression and decompression
 */
public class DfsCodeCompressor {
  /**
   * compression
   * @param dfsCode DFS code
   * @return compressed byte array
   * @throws IOException
   */
  public static byte[] compress(DfsCode dfsCode) throws IOException {
    ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
    GZIPOutputStream gzipOS = new GZIPOutputStream(byteArrayOS);
    ObjectOutputStream objectOS = new ObjectOutputStream(gzipOS);
    objectOS.writeObject(dfsCode);
    objectOS.close();
    return byteArrayOS.toByteArray();
  }
  /**
   * decompression
   * @param bytes compressed byte array
   * @return DFS code
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public static DfsCode decompress(byte[] bytes) throws IOException,
    ClassNotFoundException {
    ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(bytes);
    GZIPInputStream gzipIn = new GZIPInputStream(byteArrayIS);
    ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
    DfsCode dfsCode = (DfsCode) objectIn.readObject();
    objectIn.close();
    return dfsCode;
  }
}
