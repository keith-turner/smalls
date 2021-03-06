# Accumulo Small Scan Performance Test

Simple test to measure the performance of lots of threads doing many small scans.

Check the files in conf before running.

The following are some possible accumulo performance settings. Also consider disabling debug logging on the tserver.  These require a tserver restart.

```
accumulo shell -u root -p secret <<EOF
config -s table.durability=flush
config -t accumulo.metadata -d table.durability
config -t accumulo.root -d table.durability
config -s tserver.readahead.concurrent.max=256
config -s tserver.server.threads.minimum=256
config -s tserver.scan.files.open.max=1000
EOF
```

Adding the following to `accumulo-site.xml` seems to help performance on machines
with lots of cores.  The default thrift server has a single thread servicing
all I/O.  This single thread can become a bottleneck.

```xml
  <property>
    <name>general.rpc.server.type</name>
    <value>threadpool</value>
  </property>
```

To use, do the following.

 * Run `mvn package`
 * Run `./bin/run.sh cmd.Write 10000000`
 * Run `./bin/run.sh cmd.Scan 10000 10`


