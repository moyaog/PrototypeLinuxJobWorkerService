import java.io.Serializable;

public class ErrorInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  private Integer errorCode;
  private String ioMessage;
  private String errorMessage;

  ErrorInfo() {
    this.errorCode = null;
    this.ioMessage = null;
    this.errorMessage = null;
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
}
