import static my.Constants.*;

import javax.net.ssl.SSLSocket;

class ParsedRequest {
  HashMap<String, Object> requestMap;
  SSLSocket socket;

  ParsedRequest(HashMap<String, Object> requestMap, SSLSocket socket) {
    this.requestMap = requestMap;
    this.socket = socket;
  }

  String getProcess() {
  	Request request = (Request)this.requestMap.get(PROCESS);
  	return request.getProcess();
  }

  String getMethod() {
  	return requestMap.get(METHOD);
  }

  Long getPid() {
  	Request request = (Request)this.requestMap.get(PID);
  	return request.getPid();
  }

  String getId() {
  	return requestMap.get(ID);
  }

  void close() throws Exception {
  	this.socket.close();
  }

  SSLSocket getSocket() {
  	return this.socket;
  }
}