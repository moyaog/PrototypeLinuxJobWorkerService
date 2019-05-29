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

    String status = getProcessStatus(pid);
    if(status == null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND);
      errorInfo.setErrorMessage("Failed to find PID " + pid + ". PID was not killed.");
      return errorInfo;
    }

    Runtime.getRuntime().exec("kill " + pid);
    status = getProcessStatus(pid);
    if(status != null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_KILL);
      errorInfo.setErrorMessage("Failed to kill " + pid);
      return errorInfo;
    }

    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setErrorMessage("Successfully killed " + pid);
    return errorInfo;     
  }

  public ErrorInfo query(long pid) throws IOException {
    ErrorInfo errorInfo = new ErrorInfo();

    String status = getProcessStatus(pid);
    if(status == null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND);
      errorInfo.setErrorMessage("Failed to find PID " + pid);
      return errorInfo;
    }
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setErrorMessage("PID " + pid + " status: " + status);
    return errorInfo;
  }

  public ArrayList<String> currentJobStatus(ArrayList<Long> pids) throws IOException {
    ArrayList<String> statuses = new ArrayList<String>();

    int i = 0;
    while(i < pids.size()) {
      Long pid = pids.get(i);
      String status = getProcessStatus(pid);
      if(status == null) {
        pids.remove(pid);
        continue;
      }
      Process process = Runtime.getRuntime().exec("ps -p " + pid + " -o comm=");
      BufferedReader processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String name = processInput.readLine();
      String result = name + " " + status + " " + pid;
      statuses.add(result);
      i++;
    }

    return statuses;
  }
  
  private String getProcessStatus(long pid) throws IOException {
    // This implementation method allows the user to pass in any pid
    // The user can get the status of any PID running on the system
    Process process = Runtime.getRuntime().exec("ps -q " + pid + " -o state --no-headers");
    BufferedReader processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    return processInput.readLine();
  }
}
