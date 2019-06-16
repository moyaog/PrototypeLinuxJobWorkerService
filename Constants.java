package my;

public class Constants{
  private Constants() {}

  public static final String METHOD = "method";
  public static final String RESULT = "result";
  public static final String CURRENT = "current";
  public static final String PARAMS = "params";
  public static final String QUERY = "query";
  public static final String OUTPUT = "output";
  public static final String ID = "id";
  public static final String START = "start";
  public static final String STOP = "stop";
  public static final String GET_CURRENT = "current";
  public static final String ENCODING = "jsonrpc";
  public static final String VERSION = "2.0";
  public static final String PROCESS = "process";
  public static final String REQUEST = "request";
  public static final String RESPONSE = "response";
  public static final String NOTIFICATION = "notification";
  public static final String PID = "pid";
  public static final String RUNNING_JOBS = "runningJobs";
  public static final String ERROR_INFO = "errorInfo";
  public static final String IO_MESSAGE = "ioMessage";
  public static final String ERROR_MESSAGE = "errorMessage";
  public static final String ERROR_CODE = "errorCode";

  public static final int ERR_SUCCESS = 0;
  public static final int ERR_FAILED_TO_KILL = 1;
  public static final int ERR_FAILED_TO_FIND = 2;
  public static final int ERR_FAILED_TO_START = 3;
  public static final int ERR_FAILED_TO_FIND_VALID_METHOD = 4;
}