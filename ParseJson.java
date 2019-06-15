import org.json.*;
import java.util.*;

import static my.Constants.*;

public class ParseJson {

  public HashMap<String, Object> parseJson(JSONObject jsonObj) throws JSONException{
    HashMap<String, Object> messageParams = new HashMap<String, Object>();

    if(jsonObj.has(METHOD)) {
      messageParams.put(METHOD, jsonObj.getString(METHOD));
    } else {
      messageParams.put(METHOD, "na");
    } 
    if(jsonObj.has(RESULT)) {
      JSONObject res = jsonObj.getJSONObject(RESULT);
      Response response = new Response();
      if(!JSONObject.NULL.equals(res.get(RUNNING_JOBS))) {
        JSONArray jobArray = res.getJSONArray(RUNNING_JOBS);
        ArrayList<ErrorInfo> errorInfoArray = new ArrayList<ErrorInfo>();
        for(int i = 0; i < jobArray.length(); i++) {
          JSONObject tempJsonObj = jobArray.getJSONObject(i);
          ErrorInfo tempErrorInfo = new ErrorInfo();
          if(!JSONObject.NULL.equals(tempJsonObj.get(ERROR_CODE))) {
            tempErrorInfo.setErrorCode((Integer)tempJsonObj.get(ERROR_CODE));
          }
          if(!JSONObject.NULL.equals(tempJsonObj.get(IO_MESSAGE))) {
            tempErrorInfo.setIoMessage((String)tempJsonObj.get(IO_MESSAGE));
          }
          if(!JSONObject.NULL.equals(tempJsonObj.get(ERROR_MESSAGE))) {
            tempErrorInfo.setErrorMessage((String)tempJsonObj.get(ERROR_MESSAGE));
          }
          if(!JSONObject.NULL.equals(tempJsonObj.get(PID))) {
            tempErrorInfo.setPid(((Number)tempJsonObj.get(PID)).longValue());
          }
          errorInfoArray.add(tempErrorInfo);
        }
        response.setRunningJobs(errorInfoArray);
      }
      if(!JSONObject.NULL.equals(res.get(ERROR_INFO))) {
        JSONObject err = res.getJSONObject(ERROR_INFO);
        ErrorInfo errorInfo = new ErrorInfo();
        if(!JSONObject.NULL.equals(err.get(ERROR_CODE))) {
          errorInfo.setErrorCode((Integer)err.get(ERROR_CODE));
        }
        if(!JSONObject.NULL.equals(err.get(IO_MESSAGE))) {
          errorInfo.setIoMessage((String)err.get(IO_MESSAGE));
        }
        if(!JSONObject.NULL.equals(err.get(ERROR_MESSAGE))) {
          errorInfo.setErrorMessage((String)err.get(ERROR_MESSAGE));
        }
        if(!JSONObject.NULL.equals(err.get(PID))) {
          errorInfo.setPid(((Number)err.get(PID)).longValue());
        }
        response.setErrorInfo(errorInfo);
      }
      
      messageParams.put(RESULT, response);
    }
    if(jsonObj.has(PARAMS)) {
      JSONObject params = jsonObj.getJSONObject(PARAMS);
      Request request = new Request();
      if(!JSONObject.NULL.equals(params.get(PROCESS))) {
        request.setProcess((String)params.get(PROCESS));
      }
      if(!JSONObject.NULL.equals(params.get(PID))) {
        Long tempPid = ((Number)params.get(PID)).longValue();
        request.setPid((tempPid));
      }
      messageParams.put(PARAMS, request);
    }
    if(jsonObj.has(ID)) {
      messageParams.put(ID, jsonObj.get(ID));
    }

    return messageParams;
  }
}
