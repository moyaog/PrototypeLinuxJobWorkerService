import static my.Constants.*;

import org.json.*;
import java.util.UUID;

public class BuildJson {
  public JSONObject buildRequest(Request request, String method) throws JSONException {
    JSONObject json = new JSONObject();
    UUID uuid = UUID.randomUUID();
    json.put(PARAMS, request);
    json.put(ENCODING, VERSION);
    json.put(METHOD, method);
    json.put(ID, uuid.toString());
    return json;
  }

  public JSONObject buildResponse(Response response, String id) throws JSONException {
    JSONObject json = new JSONObject();
    json.put(ENCODING, VERSION);
    json.put(RESULT, response);
    json.put(ID, id);

    return json;
  }
}
