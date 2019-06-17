import static my.Constants.*;

import java.util.*;
import javax.net.ssl.SSLSocket;
import java.io.*;

// ParsedResponse contains methods that get and return information contained in the data field
// responseMap, and verifies that id's match
public class ParsedResponse implements Serializable {
  HashMap<String, Object> responseMap;
  
  // Constructor accepts HashMap<String,Object and initializes responseMap
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

  // Default constructor initializeds responseMap to null
  ParsedResponse() {
    this.responseMap = null;
  }

  // Verifies that passed in ID matches the ID stored in responseMap
  boolean isValid(String id) {
    String responseId = (String)this.responseMap.get(ID);
    return id.equals(responseId);
  }

  // Returns ErrorInfo object stored in responseMap
  ErrorInfo getErrorInfo() {
    Response response = (Response)this.responseMap.get(RESULT);
    return response.getErrorInfo();
  }

  // Returns ArrayList<ErrorInfo> object stored in responseMap
  ArrayList<ErrorInfo> getRunningJobs() {
    Response response = (Response)this.responseMap.get(RESULT);
    return response.getRunningJobs();
  }
}
