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
      Credentials credentials = new Credentials();
      SSLContext sslContext = credentials.init(SERVER_KEY_LOC);

      SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
      SSLServerSocket sslServerSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT);
      
      while(true) {
        ParsedRequest parsedRequest = new ParsedRequest();

        try {
          parsedRequest = receiveAndParseJsonRequest(sslServerSocket);
          Response response = handleRequest(parsedRequest, pids);
          sendResponse(parsedRequest, response);
        } catch(Exception e) {
          // TODO deal and then keep server running
          System.out.println(e.getClass().getName());
          System.out.println(e.getMessage());
        } finally {
          if(parsedRequest != null) {
            parsedRequest.close();
          }
        }
      }    
    } catch(Exception e) {
      System.out.println(e.getClass().getName());
      System.out.println(e.getMessage());
    }
  }

  private static ParsedRequest receiveAndParseJsonRequest(SSLServerSocket sslServerSocket) throws Exception {
    // TODO remove
    System.out.println("In receiveAndParseJsonRequest");
    SSLSocket sslSocket = (SSLSocket)sslServerSocket.accept();

    sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
    sslSocket.startHandshake();

    InputStream inputStream = sslSocket.getInputStream();
    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

    JSONObject jsonObjParsed = new JSONObject((String)objectInputStream.readObject());
    ParseJson parseJson = new ParseJson();
    HashMap<String, Object> jsonMap = parseJson.parseJson(jsonObjParsed);

    return new ParsedRequest(jsonMap, sslSocket);
  }

  private static Response handleRequest(ParsedRequest parsedRequest, ArrayList<ErrorInfo> pids) throws Exception {
    // TODO remove
    System.out.println("In handleRequest");
    Response response = new Response();
    ExecuteJobs exec = new ExecuteJobs();
    ErrorInfo errorInfo = new ErrorInfo();

    if(parsedRequest.getMethod().equals(START)) {
      //TODO remove
      System.out.println("In start");
      System.out.println(parsedRequest.getProcess());
      errorInfo = exec.start(parsedRequest.getProcess());
      // TODO remove
      System.out.println("Started process");
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
      // TODO remove
      System.out.println("In current");
      for(int i = 0; i < currentJobs.size(); i++) {
        System.out.println(currentJobs.get(i));
        System.out.println(currentJobs.get(i).getIoMessage());
        System.out.println(currentJobs.get(i).getPid());
      }
      response.setRunningJobs(currentJobs);
    } else if(parsedRequest.getMethod().equals(OUTPUT)) {
      errorInfo = exec.getOutputOfRunningJob(parsedRequest.getPid());
      response.setErrorInfo(errorInfo);
    } else {
      // TODO improve
      // TODO ERR_FAILED_TO_FIND_VALID_METHOD = 4;
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND_VALID_METHOD);
      errorInfo.setErrorMessage("Failed to find valid method.");
      response.setErrorInfo(errorInfo);
    }

    return response;
  }

  private static void sendResponse(ParsedRequest parsedRequest, Response response) throws Exception {
    // TODO remove
    System.out.println("In sendResponse");
    JSONObject json = new JSONObject();
    BuildJson buildJson = new BuildJson();
    json = buildJson.buildResponse(response, parsedRequest.getId());

    OutputStream outputStream = parsedRequest.getSocket().getOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
    objectOutputStream.writeObject(json.toString());
    outputStream.flush();
  }
}
