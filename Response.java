import java.io.Serializable;
import java.util.*;

public class Response implements Serializable {
  private static final long serialVersionUID = 1L;

  private Long pid;
  private String queryResult;
  private Integer stopCode;
  private ArrayList<String> runningJobs;

  Response() {
    this.pid = null;
    this.queryResult = null;
    this.stopCode = null;
    this.runningJobs = null;
  } 

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public Long getPid() {
    return this.pid;
  }

  public void setQueryResult(String queryResult) {
    this.queryResult = queryResult;
  }

  public String getQueryResult() {
    return this.queryResult;
  }

  public void setStopCode(Integer stopCode) {
    this.stopCode = stopCode;
  }

  public Integer getStopCode() {
    return this.stopCode;
  }

  public void setRunningJobs(ArrayList<String> runningJobs) {
    this.runningJobs = runningJobs;
  }

  public ArrayList<String> getRunningJobs() {
    return this.runningJobs;
  }
}
