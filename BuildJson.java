import static my.Constants.*;

import org.json.*;
import java.util.UUID;

// BuildJson contains methods that build JSONObjects that comply with the JSON-RPC 2.0
// Specification. 
public class BuildJson {
  // buildRequest returns a JSONObject that contains all the information that will be sent from
  // the Client to the Server. The JSONObject is a JSON-RPC request. 
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

  // buildResponse returns a JSONObject that contains all the information that will be sent from
  // the Server to the Client. The JSONObject is a JSON-RPC response.
  public JSONObject buildResponse(Response response, String id) throws JSONException {
    JSONObject json = new JSONObject();
    JSONObject resJson = new JSONObject(response);
    json.put(ENCODING, VERSION);
    json.put(RESULT, resJson);
    json.put(ID, id);

    return json;
  }
}