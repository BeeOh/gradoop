package org.gradoop.model.io.json;

import org.gradoop.model.impl.EPFlinkEdgeData;
import org.gradoop.model.impl.EPFlinkVertexData;
import org.gradoop.model.impl.EPGraph;
import org.json.JSONWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by niklas on 22.07.15.
 */
public class GraphJSONWriter {

  public static void writeGraph(List<EPFlinkVertexData> vertices,
    List<EPFlinkEdgeData> edges) {
    File file = new File("gradoop-flink/src/main/resources/output.json");
    if (file.exists()) {
      file.delete();
    }
    try {
      FileWriter writer = new FileWriter(file);
      JSONWriter jsonWriter = new JSONWriter(writer);
      jsonWriter.array().key("nodes");

      writer.flush();
      writer.close();
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line = reader.readLine();
      System.out.print(line);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
