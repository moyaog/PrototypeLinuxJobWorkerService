import java.util.*;
import java.io.*;

public class ExecuteJobs {
  final int ERR_SUCCESS = 0;
  final int ERR_FAILED_TO_KILL = 1;
  final int ERR_FAILED_TO_FIND = 2;

  public long start(String sProcess) throws IOException {
    Process process = Runtime.getRuntime().exec(sProcess);
    long pid = process.pid();
    return pid;
  }

  public ErrorInfo stop(long pid) throws IOException {
    ErrorInfo errorInfo = new ErrorInfo();

    ErrorInfo errorStatus = getProcessStatus(pid);
    if(errorStatus.getIoMessage() == null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND);
      errorInfo.setIoMessage("Failed to find PID " + pid + ". PID was not killed.");
      return errorInfo;
    }

    Runtime.getRuntime().exec("kill " + pid);
    errorStatus = getProcessStatus(pid);
    if(errorStatus.getIoMessage() != null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_KILL);
      errorInfo.setIoMessage("Failed to kill " + pid);
      return errorInfo;
    }

    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setIoMessage("Successfully killed " + pid);
    return errorInfo;     
  }

  public ErrorInfo query(long pid) throws IOException {
    ErrorInfo errorInfo = new ErrorInfo();

    ErrorInfo errorStatus = getProcessStatus(pid);
    if(errorStatus.getIoMessage() == null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND);
      errorInfo.setIoMessage("Failed to find PID " + pid);
      return errorInfo;
    }
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setIoMessage("PID " + pid + " status: " + errorStatus.getIoMessage());
    return errorInfo;
  }

  public ArrayList<ErrorInfo> currentJobStatus(ArrayList<Long> pids) throws IOException {
    ArrayList<ErrorInfo> statuses = new ArrayList<ErrorInfo>();

    int i = 0;
    while(i < pids.size()) {
      Long pid = pids.get(i);
      ErrorInfo errorStatus = getProcessStatus(pid);
      ErrorInfo temp = new ErrorInfo();
      if(errorStatus.getIoMessage() == null) {
        pids.remove(pid);
        continue;
      }
      Process process = Runtime.getRuntime().exec("ps -p " + pid + " -o comm=");
      BufferedReader processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      temp.setErrorCode(ERR_SUCCESS);
      temp.setErrorMessage(processError.readLine());
      temp.setIoMessage(processInput.readLine());
      statuses.add(temp);
      i++;
    }

    return statuses;
  }

  public ErrorInfo getOutputOfRunningJob(long pid) throws IOException {
    ErrorInfo errorInfo = new ErrorInfo();

    Process process = Runtime.getRuntime().exec("ps " + pid);
    BufferedReader processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

    String inputStatus = processInput.readLine();
    String errorStatus = processError.readLine();
    if(inputStatus == null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND);
      errorInfo.setErrorMessage(errorStatus);
      errorInfo.setIoMessage("Failed to find PID " + pid);
      return errorInfo;
    }
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setErrorMessage(errorStatus);
    errorInfo.setIoMessage(inputStatus);
    return errorInfo;
  }
  
  private ErrorInfo getProcessStatus(long pid) throws IOException {
    // This implementation method allows the user to pass in any pid
    // The user can get the status of any PID running on the system
    Process process = Runtime.getRuntime().exec("ps -q " + pid + " -o state --no-headers");
    BufferedReader processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
  
    ErrorInfo errorInfo = new ErrorInfo();
    errorInfo.setErrorMessage(processError.readLine());
    errorInfo.setIoMessage(processInput.readLine()); 
    return errorInfo;
  }
}
