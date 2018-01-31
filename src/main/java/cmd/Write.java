package cmd;

import static cmd.Format.toHex;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.admin.CompactionConfig;
import org.apache.accumulo.core.client.admin.NewTableConfiguration;
import org.apache.accumulo.core.data.Mutation;
import org.apache.hadoop.io.Text;

import acbase.CmdUtil;

public class Write {
  public static void main(String[] args) throws Exception {
    Connector conn = CmdUtil.getConnector();
    
    NewTableConfiguration ntc = new NewTableConfiguration();
    Map<String, String> props = new HashMap<>();
    props.put("table.file.compress.blocksize.index","256K");
    props.put("table.file.compress.blocksize.index", "8K");
    ntc.setProperties(props);
    
    try {
      conn.tableOperations().create("scanpt", ntc);
    } catch (TableExistsException tee) {
      conn.tableOperations().delete("scanpt");
      conn.tableOperations().create("scanpt", ntc);
    }
    
    SortedSet<Text> partitionKeys = new TreeSet<>(Stream.of("1","2","3","4","5","6","7","8","9","a","b","c","d","e", "f").map(Text::new).collect(toList()));
    conn.tableOperations().addSplits("scanpt", partitionKeys);
    
    BatchWriter bw = conn.createBatchWriter("scanpt", new BatchWriterConfig());
    
    Random rand = new Random();
    
    int numRows = Integer.parseInt(args[0]);
    
    for(int i = 0; i < numRows; i++) {
      Mutation m = new Mutation(toHex(rand.nextLong()));
      int c1 = rand.nextInt(1<<10);
      int c2 = rand.nextInt(1<<10);
      while(c1 == c2) c2 = rand.nextInt(1<<10);
      int c3 = rand.nextInt(1<<10);
      while(c1 == c3 || c2 == c3) c3 = rand.nextInt(1<<10);
      int c4 = rand.nextInt(1<<10);
      while(c1 == c4 || c2 == c4 || c3 == c4) c4 = rand.nextInt(1<<10);
      m.put("fam1", toHex(c1, 3), toHex(rand.nextLong()));
      m.put("fam1", toHex(c2, 3), toHex(rand.nextLong()));
      m.put("fam1", toHex(c3, 3), toHex(rand.nextLong()));
      m.put("fam1", toHex(c4, 3), toHex(rand.nextLong()));
      bw.addMutation(m);
    }
    
    bw.close();
    
    conn.tableOperations().compact("scanpt", new CompactionConfig().setFlush(true).setWait(true));
  }
}
