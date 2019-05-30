import java.util.stream.*;
import java.util.*;
import java.io.*;
//import java.util.Stream;

public class ExecuteJobs {
  private static final int ERR_SUCCESS = 0;
  private static final int ERR_FAILED_TO_KILL = 1;
  private static final int ERR_FAILED_TO_FIND = 2;
  private static final int ERR_FAILED_TO_START = 3;

  public ErrorInfo start(String sProcess) throws IOException {
    ErrorInfo errorInfo = new ErrorInfo();
    Process process = Runtime.getRuntime().exec(sProcess);

    ErrorInfo errorStatus = getProcessStatus(process.pid());
    if(errorStatus.getIoMessage() == null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_START);
      errorInfo.setIoMessage("Failed to start " + sProcess);
      return errorInfo;
    }
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setIoMessage("Successfully start " + sProcess);
    errorInfo.setPid(process.pid());
    return errorInfo;
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
      errorInfo.setErrorMessage(errorStatus.getIoMessage());
      return errorInfo;
    }
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setIoMessage("PID " + pid + " status: " + errorStatus.getIoMessage());
    errorInfo.setErrorMessage(errorStatus.getIoMessage());
    return errorInfo;
  }

  public ArrayList<ErrorInfo> currentJobStatus(ArrayList<ErrorInfo> pids) throws IOException {
    ArrayList<ErrorInfo> statuses = new ArrayList<ErrorInfo>();

    int i = 0;
    while(i < pids.size()) {
      Long pid = pids.get(i).getPid();
      ErrorInfo errorStatus = getProcessStatus(pid);
      ErrorInfo temp = new ErrorInfo();
      if(errorStatus.getIoMessage() == null) {
        pids.remove(i);
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

    Process process = Runtime.getRuntime().exec("cat /proc/" + pid + "/fd/1");
    BufferedReader processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

    Stream<String> inputStream = processInput.lines();
    Stream<String> errorStream = processError.lines();

    if(errorStream.count() > 0) {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND);
      errorInfo.setErrorMessage("Failed to find PID " + pid);
      return errorInfo;
    }
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setErrorMessage(inputStream.collect(Collectors.joining()));
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
