import static my.HardCodes.*;

import java.io.*;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class InitSSL {
  SSLContext initSsl(String keyLocation) {
    try {
      KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
      keyStore.load(new FileInputStream(keyLocation), PASSWORD.toCharArray());

      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TRUST_MANAGER);
      keyManagerFactory.init(keyStore, PASSWORD.toCharArray());

      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TRUST_MANAGER);
      trustManagerFactory.init(keyStore);

      SSLContext sslContext = SSLContext.getInstance(PROTOCOL_VER);
      sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

      return sslContext;
    } catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }  
}
