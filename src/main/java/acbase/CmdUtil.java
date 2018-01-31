package acbase;

import java.io.InputStream;
import java.util.Properties;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;

public abstract class CmdUtil {
  public static Connector getConnector() throws Exception {
    Properties props = new Properties();
    try (InputStream propsFile = CmdUtil.class.getResourceAsStream("/accumulo-client.properties")) {
      if(propsFile == null) {
        throw new Exception("accumulo-client.properties not found on classpath");
      }
      props.load(propsFile);
    }

    ZooKeeperInstance zki = new ZooKeeperInstance(props.getProperty("accumulo.instance"), props.getProperty("accumulo.zookeepers"));
    return zki.getConnector(props.getProperty("accumulo.user"), new PasswordToken(props.getProperty("accumulo.password")));
  }
}
