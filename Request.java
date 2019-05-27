import java.io.Serializable;

public class Request implements Serializable {
  private static final long serialVersionUID = 1L;

  private String command;
  private Long pid;

  Request() {
    this.command = null;
    this.pid = null;
  }
  
  public void setCommand(String command) {
    this.command = command;
  }

  public String getCommand() {
    return this.command;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public Long getPid() {
    return this.pid;
  }
}
