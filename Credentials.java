import static my.HardCodes.*;

import java.io.*;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

// Credentials contains a method that creates and returns an SSLContext object
public class Credentials {
  // init creates and returns an SSLContext. It is a part of the authentication process
  SSLContext init(String keyLocation, String password) throws Exception {
    KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
    keyStore.load(new FileInputStream(keyLocation), password.toCharArray());

    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TRUST_MANAGER);
    keyManagerFactory.init(keyStore, password.toCharArray());

    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TRUST_MANAGER);
    trustManagerFactory.init(keyStore);

    SSLContext sslContext = SSLContext.getInstance(PROTOCOL_VER);
    sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

    return sslContext;
  }  
}
