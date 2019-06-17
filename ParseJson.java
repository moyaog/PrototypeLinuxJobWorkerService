import org.json.*;
import java.util.*;

import static my.Constants.*;

// ParseJson contains a method that parse a JSONObject sent by Server or Client that is expected
// to comply with the JSON-RPC 2.0 Specification.
public class ParseJson {
  // parseJson accepts a JSONObject and returns a HashMap representation of the JSONObject.
  public HashMap<String, Object> parseJson(JSONObject jsonObj) throws JSONException{
    HashMap<String, Object> messageParams = new HashMap<String, Object>();
    // Verify that jsonObj contains a method
    if(jsonObj.has(METHOD)) {
      messageParams.put(METHOD, jsonObj.getString(METHOD));
    } else {
      // Requests will no contain methods
      messageParams.put(METHOD, "na");
    } 
    // Verify that jsonObj has a RESULT key
    if(jsonObj.has(RESULT)) {
      // Create a JSONObject using the object at RESULT
      JSONObject res = jsonObj.getJSONObject(RESULT);
      Response response = new Response();
      // Verify that res has a RUNNING_JOBS key
      if(!JSONObject.NULL.equals(res.get(RUNNING_JOBS))) {
        // Create a JSONArray using the object at RUNNING_JOBS
        JSONArray jobArray = res.getJSONArray(RUNNING_JOBS);
        ArrayList<ErrorInfo> errorInfoArray = new ArrayList<ErrorInfo>();
        for(int i = 0; i < jobArray.length(); i++) {
          // Get JSONObject from JSONArray
          JSONObject tempJsonObj = jobArray.getJSONObject(i);
          ErrorInfo tempErrorInfo = new ErrorInfo();

          // Set ErrorInfo object fields with information from JSONObject tempJsonObj
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

          // Add ErrorInfo object to ArrayList of ErrorInfo objects
          errorInfoArray.add(tempErrorInfo);
        }
        // Set the runningJobs field of the Response object
        response.setRunningJobs(errorInfoArray);
      }
      // Verify that res has an ERROR_INFO key
      if(!JSONObject.NULL.equals(res.get(ERROR_INFO))) {
        // Create a JSONObject using the object at ERROR_INFO
        JSONObject err = res.getJSONObject(ERROR_INFO);
        ErrorInfo errorInfo = new ErrorInfo();

        // Set ErrorInfo object fields with information from JSONObject err
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

        // Set the errorInfo field of the Response object
        response.setErrorInfo(errorInfo);
      }
      messageParams.put(RESULT, response);
    }
    // Verify that jsonObj has a PARAMS key
    if(jsonObj.has(PARAMS)) {
      // Create JSONObject using the object at PARAMS
      JSONObject params = jsonObj.getJSONObject(PARAMS);
      Request request = new Request();

      // Set Request object fields with information from JSONObject params
      if(!JSONObject.NULL.equals(params.get(PROCESS))) {
        request.setProcess((String)params.get(PROCESS));
      }
      if(!JSONObject.NULL.equals(params.get(PID))) {
        Long tempPid = ((Number)params.get(PID)).longValue();
        request.setPid((tempPid));
      }
      
      messageParams.put(PARAMS, request);
    }
    // Verify that jsonObj has an ID key
    if(jsonObj.has(ID)) {
      messageParams.put(ID, jsonObj.get(ID));
    }

    return messageParams;
  }
}
