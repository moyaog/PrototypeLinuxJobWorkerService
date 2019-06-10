import static my.Constants.*;

import java.util.*;
import java.io.*;
import javax.net.ssl.SSLSocket;
import java.lang.Object;


public class ParsedRequest implements Serializable {
  HashMap<String, Object> requestMap;
  SSLSocket socket;

  ParsedRequest(HashMap<String, Object> requestMap, SSLSocket socket) throws Exception {
    this.requestMap = new HashMap<String, Object>();
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

  ParsedRequest() {
    this.requestMap = null;
    this.socket = null;
  }

  String getProcess() {
    Request request = (Request)this.requestMap.get(PARAMS);
    return request.getProcess();
  }

  String getMethod() {
    return (String)requestMap.get(METHOD);
  }

  Long getPid() {
    Request request = (Request)this.requestMap.get(PARAMS);
    return request.getPid();
  }

  String getId() {
    return (String)requestMap.get(ID);
  }

  void close() throws Exception {
    this.socket.close();
  }

  SSLSocket getSocket() {
    return this.socket;
  }

  boolean isInitialized() {
    if(this.requestMap == null && this.socket == null) 
      return false;
    return true;
  }
}
