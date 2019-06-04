import java.io.Serializable;

public class Request implements Serializable {
  private static final long serialVersionUID = 1L;

  private String process;
  private Long pid;

  Request() {
    this.process = null;
    this.pid = null;
  }

  Request(String process, Long pid) {
    System.out.println("in request");
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
