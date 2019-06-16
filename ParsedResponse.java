import static my.Constants.*;

import java.util.*;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class ParsedResponse implements Serializable {
  HashMap<String, Object> responseMap;
  
  ParsedResponse(HashMap<String, Object> responseMap) throws Exception {
    this.responseMap = new HashMap<String, Object>();
    // Create deep copy of responseMap
    for(Map.Entry<String, Object> entry : responseMap.entrySet()) {
      ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
      ObjectOutputStream objOutStream = new ObjectOutputStream(byteOutStream);
      Object object = entry.getValue();
      objOutStream.writeObject(object);
      objOutStream.flush();
      objOutStream.close();
      byteOutStream.close();
      byte[] byteData = byteOutStream.toByteArray();
    
      ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteData);
      object = (Object) new ObjectInputStream(byteInputStream).readObject();
      this.responseMap.put(entry.getKey(), object);
    }
  }

  ParsedResponse() {
    this.responseMap = null;
  }

  boolean isValid(String id) {
    String responseId = (String)this.responseMap.get(ID);
    return id.equals(responseId);
  }

  ErrorInfo getErrorInfo() {
    Response response = (Response)this.responseMap.get(RESULT);
    return response.getErrorInfo();
  }

  ArrayList<ErrorInfo> getRunningJobs() {
    Response response = (Response)this.responseMap.get(RESULT);
    return response.getRunningJobs();
  }
}