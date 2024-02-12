package APIHandlers;

import Broadband.BroadbandData;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {

  ACSProxy proxy;

  public BroadbandHandler(ACSProxy acsProxy) {
    this.proxy = acsProxy;
  }

  @Override
  public Object handle(Request request, Response response){

    Moshi moshi = new Moshi.Builder().build();

    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    String targetState = request.queryParams("state");
    String county = request.queryParams("county");

    if (targetState == null || county == null) {
      // Bad request! Send an error response.
      responseMap.put("query_state", targetState);
      responseMap.put("query_county", county);
      responseMap.put("type", "error");
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", targetState == null ? "state" : "county");
      return adapter.toJson(responseMap);
    }

    try{
      try{
        BroadbandData data = this.proxy.getBroadbandData(targetState, county);
        // Building responses *IS* the job of this class:
        responseMap.put("type", "success");

        // want to return broadband percentage and time accessed
        responseMap.put("Broadband", data.percentage());
        responseMap.put("Date Accessed", data.dateTime().getTime());
        responseMap.put("State", data.state());
        responseMap.put("County", data.county());

      }catch (IOException ioException){
        responseMap.put("result", ioException.getMessage());
      }
    }catch (DatasourceException e){
      responseMap.put("result", e.getMessage());
    }

    return adapter.toJson(responseMap);
  }
}
