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
      JSONObject res = jsonObj.getJSONObject(RESULT);
      Response response = new Response();
      if(!JSONObject.NULL.equals(res.get(RUNNING_JOBS))) {
        // TODO improve
        System.out.println("Running jobs present");
        System.out.println("Running jobs " +  res.get(RUNNING_JOBS));
        @SuppressWarnings("unchecked")
        ArrayList<ErrorInfo> test = (ArrayList<ErrorInfo>)res.get(RUNNING_JOBS);
        //response.setRunningJobs((ArrayList<ErrorInfo>)res.get(RUNNING_JOBS));
        response.setRunningJobs(test);
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
          Long tempPid = ((Number)err.get(PID)).longValue();
          errorInfo.setPid(tempPid);
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
        request.setPid((Long)params.get(PID));
      }
      messageParams.put(PARAMS, request);
    }
    messageParams.put(ID, jsonObj.get(ID));

    return messageParams;
  }
}
