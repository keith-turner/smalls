package cmd;

import static cmd.Format.toHex;

import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;

import acbase.CmdUtil;

public class Scan {
  
  private static void doScans(Connector conn, int numScans) {
    try {
      Random rand = new Random();
      
      long ret = 0;
      
      long t1 = System.currentTimeMillis();
      
      for(int i = 0; i < numScans; i++) {
        Scanner scanner = conn.createScanner("scanpt", Authorizations.EMPTY);
      
        scanner.setRange(new Range(toHex(rand.nextLong())));
        
        for (Entry<Key,Value> entry : scanner) {
          ret++;
        }
        
        scanner.close();
      }
      
      long t2 = System.currentTimeMillis();
      
      double secs = (t2 - t1) / 1000.0;
      double rate = numScans / secs;
      int avgTime = (int)(((double)(t2 - t1)) / numScans);
      
      System.out.printf("Finished %d scans, saw %d entries, rate %6.2f scans/sec , avg scan time %d ms\n", numScans, ret, rate, avgTime);
    } catch (TableNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  
  public static void main(String[] args) throws Exception {
    int numScans = Integer.parseInt(args[0]);
    int numThreads = Integer.parseInt(args[1]);
    
    int scansPerThread = numScans / numThreads;
    
    Connector conn = CmdUtil.getConnector();
   
    ExecutorService es = Executors.newFixedThreadPool(numThreads);
    for(int i = 0; i < numThreads; i++) {
      es.submit(() -> doScans(conn, scansPerThread));
    }
    
    es.shutdown();
    
    while(!es.isTerminated()) {
      es.awaitTermination(1, TimeUnit.MINUTES);
    }
  }
}
