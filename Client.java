import static my.Constants.*;
import static my.HardCodes.*;

import java.util.*;
import java.io.*;
import org.json.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLHandshakeException;

public class Client {
  private final static int COMMAND_LOC = 0;
  private final static int PID_OR_PROCESS_LOC = 1;
  private final static int MIN_ARGS = 2;

  public static void main(String[] args) {
    ParsedResponse parsedResponse = new ParsedResponse();

    try {
      printUserInstructions();
      JSONObject jsonRequest = createJsonRequestFromStdin();
      parsedResponse = sendJsonRequest(jsonRequest);

      if(!parsedResponse.isValid(requestId(jsonRequest))) {
        throw new Exception("Response message received did not match ID of request message sent");
      }

      handleResponse(parsedResponse);

      /*if(parsedResponse.isInitialized()) 
        parsedResponse.close();*/
    } catch(Exception e) {
      //e.printStackTrace();
      System.out.println(e.getClass().getName());
      System.out.println(e.getMessage());
    }
  }

  private static void printUserInstructions() {
    System.out.println("You may start a job, stop a job by PID, query a job by PID," + 
        " get the output of a running job process, or get a list of all current running jobs");
      System.out.println("Valid commands are: START <process>, STOP <pid>, QUERY <pid>," +
        " OUTPUT <pid>, and CURRENT");
  }

  private static JSONObject createJsonRequestFromStdin() throws Exception {
    JSONObject json = new JSONObject();
    BuildJson buildJson = new BuildJson();
    Request request = new Request();

    Scanner scan = new Scanner(System.in);
    String[] split = System.console().readLine().split(" ");
    System.out.println();

    if(split[COMMAND_LOC].toLowerCase().equals(START)) {
      if(split.length < MIN_ARGS) {
        throw new Exception("No process provided");
      }
      String completeJob = split[PID_OR_PROCESS_LOC];
      for(int i = PID_OR_PROCESS_LOC+1; i < split.length; i++) {
        completeJob += (" " + split[i]);
      }
      request.setProcess(completeJob);
      //json = buildJson.buildRequest(request, START);
      return buildJson.buildRequest(request, START);
    } else if(split[COMMAND_LOC].toLowerCase().equals(STOP)) {
      if(split.length < MIN_ARGS) {
        throw new Exception("No PID provided");
      }
      long pid = Long.parseLong(split[PID_OR_PROCESS_LOC]);
      request.setPid(pid);
      //json = buildJson.buildRequest(request, STOP);
      return buildJson.buildRequest(request, STOP);
    } else if(split[COMMAND_LOC].toLowerCase().equals(QUERY)) {
      if(split.length < MIN_ARGS) {
        throw new Exception("No PID provided");
      }
      long pid = Long.parseLong(split[PID_OR_PROCESS_LOC]);
      request.setPid(pid);
      //json = buildJson.buildRequest(request, QUERY);
      return buildJson.buildRequest(request, QUERY);
    } else if(split[COMMAND_LOC].toLowerCase().equals(GET_CURRENT)) {
      //json = buildJson.buildRequest(request, GET_CURRENT);
      return buildJson.buildRequest(request, GET_CURRENT);
    } else if(split[COMMAND_LOC].toLowerCase().equals(OUTPUT)) {
      if(split.length < MIN_ARGS) {
        throw new Exception("No PID provided");
      }
      long pid = Long.parseLong(split[PID_OR_PROCESS_LOC]);
      request.setPid(pid);
      //json = buildJson.buildRequest(request, OUTPUT);
      return buildJson.buildRequest(request, OUTPUT);
    } else {
      throw new Exception("No valid command provided");
    }
    //return json;
  }

  protected static JSONObject readAndWriteJsonObjectHelper(SSLSocket sslSocket, JSONObject jsonObj) throws JSONException, Exception {
    SSLSession sslSession = sslSocket.getSession();
    
    OutputStream outputStream = sslSocket.getOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
    objectOutputStream.writeObject(jsonObj.toString());
    outputStream.flush();

    InputStream inputStream = sslSocket.getInputStream();
    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

    return new JSONObject((String)objectInputStream.readObject());
  }

  private static ParsedResponse sendJsonRequest(JSONObject jsonRequest) throws Exception {
    try {
      /*Credentials credentials = new Credentials();
      SSLContext sslContext = credentials.init(CLIENT_KEY_LOC);

      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      SSLSocket sslSocket = (SSLSocket)sslSocketFactory.createSocket(HOST, PORT);*/

      SSLSocket sslSocket = authenticationHelper(CLIENT_KEY_LOC);
      //SSLSocket sslSocket = authenticationHelper(FAKE_KEY_LOC);

      try {
        //sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
        // TODO this might be last method to test for authentication
        //sslSocket.startHandshake();

        /*SSLSession sslSession = sslSocket.getSession();

        OutputStream outputStream = sslSocket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(jsonRequest.toString());
        outputStream.flush();

        InputStream inputStream = sslSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);      
   
        JSONObject jsonObjParsed = new JSONObject((String)objectInputStream.readObject());*/
        JSONObject jsonObjParsed = readAndWriteJsonObjectHelper(sslSocket, jsonRequest);
        ParseJson parseJson = new ParseJson();
        HashMap<String, Object> responseMap = parseJson.parseJson(jsonObjParsed);
    
        sslSocket.close();
        return new ParsedResponse(responseMap);
      } catch(Exception e) {
        sslSocket.close();
        throw new Exception(e);
      }
    
    //return new ParsedResponse(responseMap);
      //sslSocket.close();
    } catch(Exception e) {
      //sslSocket.close();
      throw new Exception(e);
    }
    
    //sslSocket.close();

    //return new ParsedResponse(responseMap, sslSocket);
    //return new ParsedResponse(responseMap);
  }

  protected static SSLSocket authenticationHelper(String keyLocation) throws SSLHandshakeException, Exception {
    Credentials credentials = new Credentials();
    SSLContext sslContext = credentials.init(keyLocation);
    
    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
    SSLSocket sslSocket = (SSLSocket)sslSocketFactory.createSocket(HOST, PORT);

    try {
      sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
      sslSocket.startHandshake();
    } catch(SSLHandshakeException e) {
      sslSocket.close();
      throw new SSLHandshakeException(e.getMessage());
    } catch(Exception e) {
      sslSocket.close();
      throw new Exception(e);
    }
    return sslSocket;
  }

  private static void handleResponse(ParsedResponse parsedResponse) throws Exception {
    try {
      ErrorInfo errorInfo = parsedResponse.getErrorInfo();
      ArrayList<ErrorInfo> runningJobs = parsedResponse.getRunningJobs();

      if(errorInfo == null && runningJobs == null){
        throw new Exception("Response is empty");
      }
      if(errorInfo != null) {
        if(errorInfo.getErrorCode() == ERR_SUCCESS) {
          System.out.println("IO Message: " + errorInfo.getIoMessage());
          System.out.println("Error Message: " + errorInfo.getErrorMessage());
          if(errorInfo.getPid() != null) {
            System.out.println("PID " + errorInfo.getPid()); 
          }
        } else {
          if(errorInfo.getErrorCode() == ERR_FAILED_TO_KILL) {
            System.out.println("Error: Failed to kill process");
          } else if(errorInfo.getErrorCode() == ERR_FAILED_TO_FIND) {
            System.out.println("Error: Failed to find process");
          } else if(errorInfo.getErrorCode() == ERR_FAILED_TO_START) {
            System.out.println("Error: Failed to start process");
          } else {
            System.out.println("Error: Unidentified error. Check error message for more information.");
          }
          System.out.println("Error Code: " + errorInfo.getErrorCode());
          System.out.println("Error Message: " + errorInfo.getErrorMessage());
        }
        return; 
      }
      
      if(runningJobs.size() > 0) {
        System.out.println("Currently running processes:");
        for(int i = 0; i < runningJobs.size(); i++) {
          ErrorInfo tempErrorInfo = runningJobs.get(i);
          System.out.println("PID: " + tempErrorInfo.getPid() + "\tMessage: " + tempErrorInfo.getIoMessage());
        }
      } else {
        System.out.println("There are no currently running processes");
      }
    } catch(Exception e) {
      System.out.println(e.getClass().getName());
      System.out.println(e.getMessage());
    }
  }

  private static String requestId(JSONObject jsonRequest) throws Exception {
    return (String)jsonRequest.get(ID);
  }

}
