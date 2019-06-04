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
      // If running jobs is not null
        // Update running jobs
      // If error result is not null
        // Update error result
      System.out.println("Checking if running jobs are present");
      if(!JSONObject.NULL.equals(res.get(RUNNING_JOBS))) {
        System.out.println("Running jobs present");
        System.out.println("Running jobs " +  res.get(RUNNING_JOBS));
        @SuppressWarnings("unchecked")
        ArrayList<ErrorInfo> test = (ArrayList<ErrorInfo>)res.get(RUNNING_JOBS);
        //response.setRunningJobs((ArrayList<ErrorInfo>)res.get(RUNNING_JOBS));
        response.setRunningJobs(test);
      }
      if(!JSONObject.NULL.equals(res.get(ERROR_INFO))) {
        // TODO create ErrorInfo, set data members, add to response
        System.out.println("Error info present");
        JSONObject err = res.getJSONObject(ERROR_INFO);
        ErrorInfo errorInfo = new ErrorInfo();
        // TODO check for error code
        // TODO check for ioMessage
        // TODO check for errorMessage
        // TODO check for PID
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
          errorInfo.setPid((Long)err.get(PID));
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
    System.out.println("parse ID");
    messageParams.put(ID, jsonObj.get(ID));

    return messageParams;
  }
}
