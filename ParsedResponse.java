import static my.Constants.*;

import javax.net.ssl.SSLSocket;

class ParsedResponse {
  HashMap<String, Object> responseMap;
  SSLSocket socket;

  ParsedResponse(HashMap<String, Object> responseMap, SSLSocket socket) {
  	this.responseMap = responseMap;
  	this.socket = socket;
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

  void close() throws Exception {
  	this.socket.close();
  }
}