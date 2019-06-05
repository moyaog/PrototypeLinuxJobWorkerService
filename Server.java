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
  public static void main(String[] args) throws JSONException, IOException {
    ArrayList<ErrorInfo> pids = new ArrayList<ErrorInfo>();
  
    try {
      InitSSL initSsl = new InitSSL();
      SSLContext sslContext = initSsl.initSsl(SERVER_KEY_LOC);

      SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
      SSLServerSocket sslServerSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT);
      
      // TODO improve Exception handling in while loop to prevent unnecessary server shutdown
      while(true) {
        SSLSocket sslSocket = (SSLSocket)sslServerSocket.accept();

        sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
        sslSocket.startHandshake();

        InputStream inputStream = sslSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        JSONObject jsonObjParsed = new JSONObject((String)objectInputStream.readObject());
        ParseJson parseJson = new ParseJson();
        HashMap<String, Object> jsonMap = parseJson.parseJson(jsonObjParsed);

        Request request = (Request)jsonMap.get(PARAMS);
        Response response = new Response();
        ExecuteJobs exec = new ExecuteJobs();
        ErrorInfo errorInfo = new ErrorInfo();

        if(jsonMap.get(METHOD).equals(START)) {
          errorInfo = exec.start(request.getProcess());
          pids.add(errorInfo);
          response.setErrorInfo(errorInfo);
        } else if(jsonMap.get(METHOD).equals(STOP)) {
          errorInfo = exec.stop(request.getPid());
          response.setErrorInfo(errorInfo);
        } else if(jsonMap.get(METHOD).equals(QUERY)) {
          errorInfo = exec.query(request.getPid());
          response.setErrorInfo(errorInfo);
        } else if(jsonMap.get(METHOD).equals(GET_CURRENT)) {
          ArrayList<ErrorInfo> currentJobs = exec.currentJobStatus(pids);
          response.setRunningJobs(currentJobs);
        } else if(jsonMap.get(METHOD).equals(OUTPUT)) {
          errorInfo = exec.getOutputOfRunningJob(request.getPid());
          response.setErrorInfo(errorInfo);
        } else {
          // TODO improve
          // TODO ERR_FAILED_TO_FIND_VALID_METHOD = 4;
          errorInfo.setErrorCode(ERR_FAILED_TO_FIND_VALID_METHOD);
          errorInfo.setErrorMessage("Failed to find valid method.");
          response.setErrorInfo(errorInfo);
        }

        JSONObject json = new JSONObject();
        BuildJson buildJson = new BuildJson();
        json = buildJson.buildResponse(response, (String)jsonMap.get(ID));

        OutputStream outputStream = sslSocket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(json.toString());
        outputStream.flush();

        sslSocket.close();
      }    
    } catch(Exception e) {
      System.out.println(e.getClass().getName());
      System.out.println(e.getMessage());
    }
  }
}
