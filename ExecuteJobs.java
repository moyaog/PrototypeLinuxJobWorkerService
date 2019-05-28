import java.util.*;
import java.io.*;

public class ExecuteJobs {
  public long start(String process) throws IOException {
    // TODO some error handling
    Process p = Runtime.getRuntime().exec(process);
    long pid = p.pid();
    return pid;
  }

  public int stop(long pid) throws IOException {
    Process p = Runtime.getRuntime().exec("ps -q " + pid + " -o state --no-headers");
    BufferedReader pInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String s = pInput.readLine();
    if(s == null) {
      // pid not found
      return 2;
    }

    Runtime.getRuntime().exec("kill " + pid);
    p = Runtime.getRuntime().exec("ps -q " + pid + " -o state --no-headers");
    pInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
    s = pInput.readLine();
    if(s != null) {
      for(int i = 0; i < 5; i++) {
        s = null;
        p = Runtime.getRuntime().exec("kill " + pid);
        pInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        s = pInput.readLine();
        if(s == null) {
          break;
        }
      }
      s = null;
      pInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      s = pInput.readLine();
      if(s != null) {
        // Failed to kill pid
        return 1;
      }
    }
    // Successfully killed pid
    return 0;     
  }

  public String query(long pid) throws IOException {
    Process p = Runtime.getRuntime().exec("ps -q " + pid + " -o state --no-headers");
    BufferedReader pInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String s = pInput.readLine();
    if(s == null) {
      return pid + " not found";
    }
    return s;
  }

  public ArrayList<String> currentJobStatus(ArrayList<Long> pids) throws IOException {
    ArrayList<String> statuses = new ArrayList<String>();

    int i = 0;
    while(i < pids.size()) {
      Long pid = pids.get(i);
      Process p = Runtime.getRuntime().exec("ps -q " + pid + " -o state --no-headers");
      BufferedReader pInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String status = pInput.readLine();
      if(status == null) {
        pids.remove(pid);
        continue;
      }
      p = Runtime.getRuntime().exec("ps -p " + pid + " -o comm=");
      pInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String name = pInput.readLine();
      String result = name + " " + status + " " + pid;
      statuses.add(result);
      i++;
    }

    return statuses;
  }
}
