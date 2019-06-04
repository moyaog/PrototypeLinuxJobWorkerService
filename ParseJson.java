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
      System.out.println(jsonObj.toString());
      System.out.println(jsonObj.get(PARAMS).toString());
      JSONObject param = new JSONObject(jsonObj.get(PARAMS));
      Request request = new Request();
      System.out.println(param.toString());
      /*if(!JSONObject.NULL.equals(param.get(PROCESS))) {
        //messageParams.put(PROCESS, param.get(PROCESS));
        request.setProcess(param.get(PROCESS));
      }
      if(!JSONObject.NULL.equals(param.get(PID))) {
        request.setPid(param.get(PID));
      }
      //messageParams.put(PARAMS, jsonObj.get(PARAMS));
      messageParams.put(PARAMS, request);*/
    }
    messageParams.put(ID, jsonObj.get(ID));

    return messageParams;
  }
}
