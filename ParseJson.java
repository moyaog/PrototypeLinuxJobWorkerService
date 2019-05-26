import org.json.*;
import java.util.*;

import static my.Constants.*;

public class ParseJson {

  public HashMap<String, String> parseJson(JSONObject jsonObj) throws JSONException{
    final String TYPE = "MessageType";
    HashMap<String, String> messageParams = new HashMap<String, String>();
    String tempKey;
    JSONArray tempArray = new JSONArray();

    if(jsonObj.has(METHOD)) {
      tempKey = jsonObj.getString(METHOD);
      messageParams.put(METHOD, tempKey);
    } else {
      tempArray = jsonObj.getJSONArray(RESULT);
      for(int i = 0; i < tempArray.length(); i++) {
        messageParams.put("r"+(i+1), tempArray.getString(i));
      }
    }

    if(messageParams.containsKey(METHOD) && messageParams.get(METHOD) != CURRENT) {
      JSONObject params = jsonObj.getJSONObject(PARAMS);
      tempKey = params.getString(PROCESS);
      messageParams.put(PROCESS, tempKey);
    } 

    if(jsonObj.has(ID)) {
      tempKey = jsonObj.getString(ID);
      messageParams.put(ID, tempKey);
    }

    if(!messageParams.containsKey(ID)) {
      messageParams.put(TYPE, NOTIFICATION);
    } else if(messageParams.containsKey(RESULT)) {
      messageParams.put(TYPE, RESPONSE);
    } else {
      messageParams.put(TYPE, REQUEST);
    }

    return messageParams;
  }
}
