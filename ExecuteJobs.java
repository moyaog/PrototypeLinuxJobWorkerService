import static my.Constants.*;

import java.util.stream.*;
import java.util.*;
import java.io.*;

// ExecuteJobs contains methods that are responsible for creating, querying, and operating
// on Linux processes
public class ExecuteJobs {
  // start accepts a String representing a Linux process and arguments. It returns an ErrorInfo
  // object that contains information about whether or not the process was successfully started
  public ErrorInfo start(String sProcess) throws IOException, InterruptedException {
    ErrorInfo errorInfo = new ErrorInfo();

    // Start process
    Process process = Runtime.getRuntime().exec(sProcess);

    ErrorInfo errorStatus = getProcessStatus(process.pid());
    // True if process fails to start
    if(errorStatus.getIoMessage() == null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_START);
      errorInfo.setErrorMessage("Failed to start " + sProcess);
      return errorInfo;
    }

    // Executes if process successfully starts
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setIoMessage("Successfully started " + sProcess);
    errorInfo.setPid(process.pid());
    return errorInfo;
  }

  // stop accepts a Long representation of a PID whose associated process should be stopped. 
  // It returns an ErrorInfo object that contains information about whether or not the process
  // was successfully stopped.
  public ErrorInfo stop(long pid) throws IOException, InterruptedException {
    ErrorInfo errorInfo = new ErrorInfo();

    // Find process running with PID pid
    ErrorInfo errorStatus = getProcessStatus(pid);
    // True if no process associated with pid
    if(errorStatus.getIoMessage() == null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND);
      errorInfo.setErrorMessage("Failed to find PID " + pid + ". PID was not killed.");
      return errorInfo;
    }

    // Executes if process is running
    // Kill PID
    Process process = Runtime.getRuntime().exec("kill " + pid);
    process.waitFor();

    // Find process with PID pid has been killed
    errorStatus = getProcessStatus(pid);
    // True if process with pid has failed to be killed
    if(errorStatus.getIoMessage() != null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_KILL);
      errorInfo.setErrorMessage("Failed to kill " + pid);
      return errorInfo;
    }

    // Executes if process has successfully been killed
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setIoMessage("Successfully killed " + pid);
    return errorInfo;     
  }

  // query accepts a Long representation of a PID, queries the current status of the process, 
  // and returns the current status
  public ErrorInfo query(long pid) throws IOException, InterruptedException {
    ErrorInfo errorInfo = new ErrorInfo();

    // Find process running with PID pid
    ErrorInfo errorStatus = getProcessStatus(pid);
    // True if no process associated with pid
    if(errorStatus.getIoMessage() == null) {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND);
      errorInfo.setErrorMessage("Failed to find PID " + pid);
      errorInfo.setIoMessage(errorStatus.getIoMessage());
      return errorInfo;
    }

    // Executes if process is running
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setIoMessage("PID " + pid + " status: " + errorStatus.getIoMessage());
    errorInfo.setErrorMessage(errorStatus.getIoMessage());
    return errorInfo;
  }

  // currentJobStatus accepts an ArrayList<ErrorInfo> pids and returns a new 
  // ArrayList<ErrorInfo> statuses. pids contains ErrorInfo objects that have an associated process
  // that is expected to be running. statuses is populated with ErrorInfo objects that have
  // confirmed processes running along with information about the running processes.
  public ArrayList<ErrorInfo> currentJobStatus(ArrayList<ErrorInfo> pids) throws IOException, InterruptedException {
    ArrayList<ErrorInfo> statuses = new ArrayList<ErrorInfo>();

    int i = 0;
    while(i < pids.size()) {
      Long pid = pids.get(i).getPid();
      // Find process running with PID pid
      ErrorInfo errorStatus = getProcessStatus(pid);
      ErrorInfo temp = new ErrorInfo();
      // If process is no longer running, remove from pids ArrayList
      if(errorStatus.getIoMessage() == null) {
        pids.remove(i);
        continue;
      }

      // Executes if process is still running
      Process process = Runtime.getRuntime().exec("ps -p " + pid + " -o comm=");
      BufferedReader processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      temp.setErrorCode(ERR_SUCCESS);
      temp.setErrorMessage(processError.readLine());
      temp.setIoMessage(processInput.readLine());
      temp.setPid(pid);
      statuses.add(temp);
      i++;
    }

    return statuses;
  }

  // getOuputOfRunningJob accepts a Long representation of a PID whose associated process should
  // be running and return an ErrorInfo object with output of the running process
  public ErrorInfo getOutputOfRunningJob(long pid) throws IOException, InterruptedException {
    ErrorInfo errorInfo = new ErrorInfo();

    // Get process output
    Process process = Runtime.getRuntime().exec("cat /proc/" + pid + "/fd/1");
    BufferedReader processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

    Stream<String> inputStream = processInput.lines();
    Stream<String> errorStream = processError.lines();

    // True if there is no running process associated with PID pid
    if(errorStream.count() > 0) {
      errorInfo.setErrorCode(ERR_FAILED_TO_FIND);
      errorInfo.setErrorMessage("Failed to find PID " + pid);
      return errorInfo;
    }

    // Executes if process found
    errorInfo.setErrorCode(ERR_SUCCESS);
    errorInfo.setIoMessage(inputStream.collect(Collectors.joining()));
    return errorInfo;
  }
  
  // getProcessStatus accepts a Long representation of a PID pid, checks for the process
  // associated with PID pid, and returns an ErrorInfo object with the PID and relevant
  // information found
  private ErrorInfo getProcessStatus(long pid) throws IOException, InterruptedException {
    Process process = Runtime.getRuntime().exec("ps -q " + pid + " -o state --no-headers");
    BufferedReader processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
  
    ErrorInfo errorInfo = new ErrorInfo();
    errorInfo.setErrorMessage(processError.readLine());
    errorInfo.setIoMessage(processInput.readLine());
    errorInfo.setPid(pid); 
    return errorInfo;
  }
}
