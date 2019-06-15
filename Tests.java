import static my.Constants.*;
import static my.HardCodes.*;

import org.json.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLHandshakeException;

class Tests {
  static Client client = new Client();
  static Server server = new Server();

  public static void main(String args[]) {
    boolean isSuccessfullyAuthenticated = verifySuccessfulAuthentication();
    boolean sslHandshakeExceptionSuccessfullyThrown = verifySSLHandshakeExceptionThrown();
    boolean nonSSLHandshakeExceptionSuccessfullyThrown = verifyNonSSLHandshakeExceptionThrown();

    boolean jsonObjectSuccessfullyReturned = verifyJSONObjectSuccessfullyReturned();

    printResults("Client-Server authentication", isSuccessfullyAuthenticated);
    printResults("Client-Server SSL handshake authentication error", sslHandshakeExceptionSuccessfullyThrown);
    printResults("Client-Server Non SSL handshake authentication error", nonSSLHandshakeExceptionSuccessfullyThrown);

    printResults("Network JSONObject passing", jsonObjectSuccessfullyReturned);
  }

  private static void printResults(String message, boolean result) {
    System.out.print(message + " ");
    if(result) {
      System.out.println("PASSED");
    } else {
      System.out.println("FAILED");
    }
  }

  private static boolean verifySuccessfulAuthentication() {
    try {
      SSLSocket clientSocket = client.authenticationHelper(CLIENT_KEY_LOC);
      try {
        clientSocket.close();
        return true;
      } catch(Exception e) {
        return true;
      }
    } catch(Exception e) {
      return false;
    }
  }

  private static boolean verifySSLHandshakeExceptionThrown() {
    try {
      SSLSocket clientSocket = client.authenticationHelper(FAKE_KEY_LOC);
      try {
        clientSocket.close();
        return false;
      } catch(Exception e) {
        return false;
      }
    } catch(SSLHandshakeException e) {
      return true;
    } catch(Exception e) {
      return false;
    }
  }

  private static boolean verifyNonSSLHandshakeExceptionThrown() {
    try {
      SSLSocket clientSocket = client.authenticationHelper("fakeLocation");
    
      try {
        clientSocket.close();
        return false;
      } catch(Exception e) {
        return false;
      }
    } catch(SSLHandshakeException e) {
      return false;
    } catch(Exception e) {
      return true;
    }
  } 

  private static boolean verifyJSONObjectSuccessfullyReturned() {
    try {
      SSLSocket clientSocket = client.authenticationHelper(CLIENT_KEY_LOC);

      JSONObject jsonIn = new JSONObject();
      JSONObject jsonOut = client.readAndWriteJsonObjectHelper(clientSocket, jsonIn);

      try {
        clientSocket.close();
        return true;
      } catch(Exception e) {
        return true;
      }
    } catch(Exception e) {
      return false;
    }
  }
}
