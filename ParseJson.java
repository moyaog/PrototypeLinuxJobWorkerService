import org.json.*;
import java.util.*;

import static my.Constants.*;

public class ParseJson {

  public HashMap<String, Object> parseJson(JSONObject jsonObj) throws JSONException{
    HashMap<String, Object> messageParams = new HashMap<String, Object>();

    if(jsonObj.has(METHOD)) {
      messageParams.put(METHOD, jsonObj.getString(METHOD));
    } 
    if(jsonObj.has(RESULT)) {
      messageParams.put(RESULT, jsonObj.get(RESULT));
    }
    if(jsonObj.has(PARAMS)) {
      messageParams.put(PARAMS, jsonObj.get(PARAMS));
    }
    messageParams.put(ID, jsonObj.get(ID));

    return messageParams;
  }
}
