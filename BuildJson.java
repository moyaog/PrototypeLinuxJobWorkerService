import static my.Constants.*;

import org.json.*;
import java.util.UUID;

public class BuildJson {
  public JSONObject buildRequest(Request request, String method) throws JSONException {
    JSONObject json = new JSONObject();
    JSONObject reqJson = new JSONObject(request);
    UUID uuid = UUID.randomUUID();
    json.put(PARAMS, reqJson);
    json.put(ENCODING, VERSION);
    json.put(METHOD, method);
    json.put(ID, uuid.toString());
    return json;
  }

  public JSONObject buildResponse(Response response, String id) throws JSONException {
    JSONObject json = new JSONObject();
    JSONObject resJson = new JSONObject(response);
    json.put(ENCODING, VERSION);
    json.put(RESULT, resJson);
    json.put(ID, id);

    return json;
  }
}
