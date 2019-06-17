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
  // main method of Server
  public static void main(String[] args) {
    // ArrayList of started processes
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
          // If exception is caught, print message, and then keep server running
          System.out.println(e.getClass().getName());
          System.out.println(e.getMessage());
          e.printStackTrace();
        } finally {
          // Always try to close the socket 
          try {
            parsedRequest.close();
          } catch(Exception e) {
            System.out.println("Socket was not available to close.");
          }
        }
      }    
    // If exception is caught, Server should stop running
    } catch(Exception e) {
      System.out.println(e.getClass().getName());
      System.out.println(e.getMessage());
    }
  }

  // socketSetUpHelper initializes Credentials object, and creates and returns SSLServerSocket
  // This method is protected for testing purposes
  protected static SSLServerSocket socketSetUpHelper(String keyLocation) throws Exception {
    Credentials credentials = new Credentials();
    SSLContext sslContext = credentials.init(keyLocation, SERVER_PASSWORD);
    
    SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
    return (SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT);
  }

  // authenticationHelper creates and returns SSLSocket
  // This method is protected for testing purposes
  protected static SSLSocket authenticationHelper(SSLServerSocket sslServerSocket) throws Exception {
    SSLSocket sslSocket = (SSLSocket)sslServerSocket.accept();

    sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
    sslSocket.startHandshake();

    return sslSocket;
  }

  // receiveAndParseJsonRequest reads in Client request, parses the request, and returns
  // a ParsedRequest object
  private static ParsedRequest receiveAndParseJsonRequest(SSLSocket sslSocket) throws Exception {
    // Read in request from Client
    InputStream inputStream = sslSocket.getInputStream();
    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

    // Initialize JSONObject with Client request and parse JSONObject
    JSONObject jsonObjParsed = new JSONObject((String)objectInputStream.readObject());
    ParseJson parseJson = new ParseJson();
    HashMap<String, Object> jsonMap = parseJson.parseJson(jsonObjParsed);

    return new ParsedRequest(jsonMap, sslSocket);
  }

  // handleRequest accepts a ParsedRequest object and ArrayList<ErrorInfo> containing running 
  // processes. The method will use the ParsedRequest object values to decide which method in the 
  // ExecuteJobs class will be called, call the appropriate method, and then create and return
  // a Response object
  private static Response handleRequest(ParsedRequest parsedRequest, ArrayList<ErrorInfo> pids) throws Exception {
    Response response = new Response();
    ExecuteJobs exec = new ExecuteJobs();
    ErrorInfo errorInfo = new ErrorInfo();

    if(parsedRequest.getMethod().equals(START)) {
      errorInfo = exec.start(parsedRequest.getProcess());
      // Process started, add associated ErrorInfo object to ArrayList<ErrorInfo> pids
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

  // sendResponse accepts a ParsedRequest object and a Response object, builds a new response
  // JSONObject, and sends it back to the Client.
  private static void sendResponse(ParsedRequest parsedRequest, Response response) throws Exception {
    // Build JSONObject
    JSONObject json = new JSONObject();
    BuildJson buildJson = new BuildJson();
    json = buildJson.buildResponse(response, parsedRequest.getId());

    // Send JSONObject to Client
    OutputStream outputStream = parsedRequest.getSocket().getOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
    objectOutputStream.writeObject(json.toString());
    outputStream.flush();
  }
}
