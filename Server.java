import static my.Constants.*;
import static my.HardCodes.*;

import org.json.*;
import java.util.*;
import java.io.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

public class Server {
  public static void main(String[] args) {
    ArrayList<ErrorInfo> pids = new ArrayList<ErrorInfo>();
  
    try {
      SSLServerSocket sslServerSocket = socketSetUpHelper(SERVER_KEY_LOC);
      
      while(true) {
        ParsedRequest parsedRequest = new ParsedRequest();

        try {
          SSLSocket sslSocket = authenticationHelper(sslServerSocket);
          parsedRequest = receiveAndParseJsonRequest(sslSocket);
          Response response = handleRequest(parsedRequest, pids);
          sendResponse(parsedRequest, response);
        } catch(Exception e) {
          // deal with exception and then keep server running
          System.out.println(e.getClass().getName());
          System.out.println(e.getMessage());
          e.printStackTrace();
        } finally {
          try {
            parsedRequest.close();
          } catch(Exception e) {
            System.out.println("Socket was not available to close.");
          }
        }
      }    
    } catch(Exception e) {
      System.out.println(e.getClass().getName());
      System.out.println(e.getMessage());
    }
  }

  protected static SSLServerSocket socketSetUpHelper(String keyLocation) throws Exception {
    Credentials credentials = new Credentials();
    SSLContext sslContext = credentials.init(keyLocation, SERVER_PASSWORD);
    
    SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
    return (SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT);
  }

  protected static SSLSocket authenticationHelper(SSLServerSocket sslServerSocket) throws Exception {
    SSLSocket sslSocket = (SSLSocket)sslServerSocket.accept();

    sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
    sslSocket.startHandshake();

    return sslSocket;
  }

  private static ParsedRequest receiveAndParseJsonRequest(SSLSocket sslSocket) throws Exception {
    InputStream inputStream = sslSocket.getInputStream();
    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

    JSONObject jsonObjParsed = new JSONObject((String)objectInputStream.readObject());
    ParseJson parseJson = new ParseJson();
    HashMap<String, Object> jsonMap = parseJson.parseJson(jsonObjParsed);

    return new ParsedRequest(jsonMap, sslSocket);
  }

  private static Response handleRequest(ParsedRequest parsedRequest, ArrayList<ErrorInfo> pids) throws Exception {
    Response response = new Response();
    ExecuteJobs exec = new ExecuteJobs();
    ErrorInfo errorInfo = new ErrorInfo();

    if(parsedRequest.getMethod().equals(START)) {
      errorInfo = exec.start(parsedRequest.getProcess());
      pids.add(errorInfo);
      response.setErrorInfo(errorInfo);
    } else if(parsedRequest.getMethod().equals(STOP)) {
      errorInfo = exec.stop(parsedRequest.getPid());
      response.setErrorInfo(errorInfo);
    } else if(parsedRequest.getMethod().equals(QUERY)) {
      errorInfo = exec.query(parsedRequest.getPid());
      response.setErrorInfo(errorInfo);
    } else if(parsedRequest.getMethod().equals(GET_CURRENT)) {
      ArrayList<ErrorInfo> currentJobs = exec.currentJobStatus(pids);
      response.setRunningJobs(currentJobs);
    } else if(parsedRequest.getMethod().equals(OUTPUT)) {
      errorInfo = exec.getOutputOfRunningJob(parsedRequest.getPid());
      response.setErrorInfo(errorInfo);
    } else {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND_VALID_METHOD);
      errorInfo.setErrorMessage("Failed to find valid method.");
      response.setErrorInfo(errorInfo);
    }

    return response;
  }

  private static void sendResponse(ParsedRequest parsedRequest, Response response) throws Exception {
    JSONObject json = new JSONObject();
    BuildJson buildJson = new BuildJson();
    json = buildJson.buildResponse(response, parsedRequest.getId());

    OutputStream outputStream = parsedRequest.getSocket().getOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
    objectOutputStream.writeObject(json.toString());
    outputStream.flush();
  }
}
