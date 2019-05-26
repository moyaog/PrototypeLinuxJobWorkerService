import static my.Constants.*;

import org.json.*;
import java.util.UUID;

public class BuildJson {

  public JSONObject buildJson(String commands[]) throws JSONException{
    JSONObject jsonObj = new JSONObject();
    if(commands[0].equals(QUERY) || commands[0].equals(GET_CURRENT)) {
      if(commands[0].equals(QUERY) && commands.length < 2) {
        // TODO error
        return jsonObj;
      }
      jsonObj = buildRequest(commands);
    } else {
      if(commands.length < 2) {
        // TODO error
        return jsonObj;
      }
      jsonObj = buildNotification(commands[0], commands[1]);
    }
    return jsonObj;
  }

  private JSONObject buildNotification(String method, String process) throws JSONException{
    JSONObject json = new JSONObject();
    JSONObject jsonParams = new JSONObject();
  
    jsonParams.put(PROCESS, process);

    json.put(ENCODING, VERSION);
    json.put(METHOD, method);
    json.put(PARAMS, jsonParams);
    
    return json;
  }

  private JSONObject buildRequest(String[] commands) throws JSONException{
    UUID uuid = UUID.randomUUID();
    JSONObject json = new JSONObject();
    JSONObject jsonParams = new JSONObject();

    if(commands.length > 1) {
      jsonParams.put(PROCESS, commands[1]);
      json.put(PARAMS, jsonParams);
    }
    json.put(ENCODING, VERSION);
    json.put(METHOD, commands[0]);
    json.put(ID, uuid.toString());
    commands[0] = uuid.toString();
    
    return json;
  }

  public JSONObject buildResponse(String[] result, String id) throws JSONException{
    JSONObject json = new JSONObject();
    JSONArray jsonParams = new JSONArray();
    
    json.put(ENCODING, VERSION);
    for(int i = 0; i < result.length; i++) {
      jsonParams.put(result[i]);
    }
    json.put(RESULT, jsonParams);
    json.put(ID, id);

    return json;
  }
}
