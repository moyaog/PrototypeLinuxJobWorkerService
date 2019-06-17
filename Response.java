import java.io.Serializable;
import java.util.*;

// Response contains data fields, a constructor, and getters and setters for each field
public class Response implements Serializable {
  private static final long serialVersionUID = 1L;

  private ArrayList<ErrorInfo> runningJobs;
  private ErrorInfo errorInfo;

  Response() {
    this.runningJobs = null;
    this.errorInfo = null;
  } 

  public void setRunningJobs(ArrayList<ErrorInfo> runningJobs) {
    this.runningJobs = runningJobs;
  }

  public ArrayList<ErrorInfo> getRunningJobs() {
    return this.runningJobs;
  }

  public void setErrorInfo(ErrorInfo errorInfo) {
    this.errorInfo = errorInfo;
  }

  public ErrorInfo getErrorInfo() {
    return this.errorInfo;
  }
}
