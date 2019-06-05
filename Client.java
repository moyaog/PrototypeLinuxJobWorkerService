import static my.Constants.*;
import static my.HardCodes.*;

import java.util.*;
import java.io.*;
import org.json.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {
  private final static int COMMAND_LOC = 0;
  private final static int PID_OR_PROCESS_LOC = 1;
  private final static int MIN_ARGS = 2;

  public static void main(String[] args) throws JSONException {
    try{
      Request request = new Request();
      BuildJson buildJson = new BuildJson();
      JSONObject json = new JSONObject();

      System.out.println("You may start a job, stop a job by PID, query a job by PID," + 
        " get the output of a running job process, or get a list of all current running jobs");
      System.out.println("Valid commands are: START <process>, STOP <pid>, QUERY <pid>," +
        " OUTPUT <pid>, and CURRENT");
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
        json = buildJson.buildRequest(request, START);
      } else if(split[COMMAND_LOC].toLowerCase().equals(STOP)) {
        if(split.length < MIN_ARGS) {
          throw new Exception("No PID provided");
        }
        long pid = Long.parseLong(split[PID_OR_PROCESS_LOC]);
        request.setPid(pid);
        json = buildJson.buildRequest(request, STOP);
      } else if(split[COMMAND_LOC].toLowerCase().equals(QUERY)) {
        if(split.length < MIN_ARGS) {
          throw new Exception("No PID provided");
        }
        long pid = Long.parseLong(split[PID_OR_PROCESS_LOC]);
        request.setPid(pid);
        json = buildJson.buildRequest(request, QUERY);
      } else if(split[COMMAND_LOC].toLowerCase().equals(GET_CURRENT)) {
        json = buildJson.buildRequest(request, GET_CURRENT);
      } else if(split[COMMAND_LOC].toLowerCase().equals(OUTPUT)) {
        if(split.length < MIN_ARGS) {
          throw new Exception("No PID provided");
        }
        long pid = Long.parseLong(split[PID_OR_PROCESS_LOC]);
        request.setPid(pid);
        json = buildJson.buildRequest(request, OUTPUT);
      } else {
        throw new Exception("No valid command provided");
      }

      String id = (String)json.get(ID);

      InitSSL initSsl = new InitSSL();
      SSLContext sslContext = initSsl.initSsl(CLIENT_KEY_LOC);
  
      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      SSLSocket sslSocket = (SSLSocket)sslSocketFactory.createSocket(HOST, PORT);

      sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
      sslSocket.startHandshake();

      SSLSession sslSession = sslSocket.getSession();

      OutputStream outputStream = sslSocket.getOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeObject(json.toString());
      outputStream.flush();

      InputStream inputStream = sslSocket.getInputStream();
      ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);      
     
      JSONObject jsonObjParsed = new JSONObject((String)objectInputStream.readObject());
      ParseJson parseJson = new ParseJson();
      HashMap<String, Object> jsonMap = parseJson.parseJson(jsonObjParsed);

      Response response = (Response)jsonMap.get(RESULT);
      ErrorInfo errorInfo = response.getErrorInfo();
      ArrayList<ErrorInfo> errorInfoArray = response.getRunningJobs();

      String responseId = (String)jsonMap.get(ID);
      if(!id.equals(responseId)) {
        throw new Exception("Response message received did not match ID of request message sent");
      }

      if(errorInfo != null) {
        if(errorInfo.getErrorCode() == ERR_SUCCESS) {
          System.out.println(errorInfo.getIoMessage());
          System.out.println(errorInfo.getErrorMessage());
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
      } else if(errorInfoArray != null) {
        if(errorInfoArray.size() > 0) {
          System.out.println("Currently running processes:");
          for(int i = 0; i < errorInfoArray.size(); i++) {
            ErrorInfo tempErrorInfo = errorInfoArray.get(i);
            System.out.println("PID: " + tempErrorInfo.getPid() + "\tMessage: " + tempErrorInfo.getIoMessage());
          }
        } else {
          System.out.println("There are no currently running processes");
        }
      } else {
        throw new Exception("Response is empty");
      }

      sslSocket.close();
    } catch(Exception e) {
      System.out.println(e.getClass().getName());
      System.out.println(e.getMessage());
    }
  }
}
