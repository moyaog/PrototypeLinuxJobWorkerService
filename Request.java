import java.io.Serializable;

// Request contains data fields, constructors, and getters and setters for each field
public class Request implements Serializable {
  private static final long serialVersionUID = 1L;

  private String process;
  private Long pid;

  Request() {
    this.process = null;
    this.pid = null;
  }

  Request(String process, Long pid) {
    this.process = process;
    this.pid = pid;
  }

  public void setProcess(String process) {
    this.process = process;
  }

  public String getProcess() {
    return this.process;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public Long getPid() {
    return this.pid;
  }
}
