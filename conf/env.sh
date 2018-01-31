ACCUMULO_VERSION=$(accumulo version)
HADOOP_VERSION=$(hadoop version | head -1 | cut -f 2 -d ' ')

#TODO fail of command above fails

CLASSPATH=$(mvn -Daccumulo.version=$ACCUMULO_VERSION -Dhadoop.version=$HADOOP_VERSION -q exec:exec -Dexec.executable=echo -Dexec.args="%classpath")
CONF_DIR=$(readlink -f ./conf)
export CLASSPATH="$CONF_DIR:$CLASSPATH"

