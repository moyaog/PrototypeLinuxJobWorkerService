import java.util.stream.*;
import java.io.Serializable;
import java.util.*;

// ErrorInfo contains methods that update and return the values in it's data fields
public class ErrorInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  private Integer errorCode;
  private String ioMessage;
  private String errorMessage;
  private Long pid;

  // Constructor
  ErrorInfo() {
    this.errorCode = null;
    this.ioMessage = null;
    this.errorMessage = null;
    this.pid = null;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public int getErrorCode() {
    return this.errorCode;
  }

  public void setIoMessage(String ioMessage) {
    this.ioMessage = ioMessage;
  }

  public String getIoMessage() {
    return this.ioMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public Long getPid() {
    return this.pid;
  }

}
