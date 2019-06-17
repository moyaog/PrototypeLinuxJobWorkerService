import static my.Constants.*;

import java.util.*;
import java.io.*;
import javax.net.ssl.SSLSocket;
import java.lang.Object;

// ParsedRequest contains methods that get and return information contained in the data field
// requestMap, get and operated on the data field socket, and verify ParsedRequest object
// initialization
public class ParsedRequest implements Serializable {
  // Data fields
  HashMap<String, Object> requestMap;
  SSLSocket socket;

  // Constructor accepts HashMap<String,Object> and SSLSocket and initializes the class's
  // data fields
  ParsedRequest(HashMap<String, Object> requestMap, SSLSocket socket) throws Exception {
    this.requestMap = new HashMap<String, Object>();
    // Create deep copy of requestMap
    for(Map.Entry<String, Object> entry : requestMap.entrySet()) {
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
      this.requestMap.put(entry.getKey(), object);
    }
    this.socket = socket;
  }

  // Default constructor initializes data fields to null
  ParsedRequest() {
    this.requestMap = null;
    this.socket = null;
  }

  // getProcess returns the name of the process stored in the requestMap field
  String getProcess() {
    Request request = (Request)this.requestMap.get(PARAMS);
    return request.getProcess();
  }

  // getMethod returns the name of the method stored in the requestMap field
  String getMethod() {
    return (String)requestMap.get(METHOD);
  }

  // getPid returns the PID of the process stored in requestMap
  Long getPid() {
    Request request = (Request)this.requestMap.get(PARAMS);
    return request.getPid();
  }

  // getId returns the id of the request stored in requestMap
  String getId() {
    return (String)requestMap.get(ID);
  }

  // close close's the data field socket
  void close() throws Exception {
    this.socket.close();
  }

  // getSocket returns the SSLSocket in the socket field
  SSLSocket getSocket() {
    return this.socket;
  }

  // isInitialized returns boolean value to verify that the ParsedRequest instance has been 
  // initialized with values other than null
  boolean isInitialized() {
    if(this.requestMap == null && this.socket == null) 
      return false;
    return true;
  }
}
