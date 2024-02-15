package APIHandlers;

import APIHandlers.Exceptions.DatasourceException;
import Broadband.BroadbandData;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {

  ACSProxy proxy;
  Map<String, Object> responseMap;
  JsonAdapter<Map<String, Object>> adapter;

  public BroadbandHandler(){
    //moshi adapter to build responses
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringString = Types.newParameterizedType(Map.class, String.class, String.class);
    this.adapter = moshi.adapter(mapStringString);

    // map to put results and send back to user
    this.responseMap = new HashMap<>();
    this.instantiateHandling();
  }

  private void instantiateHandling(){
    try{
        this.proxy = new ACSProxy(CacheType.MAX_SIZE, 1000);

      }catch(DatasourceException datasourceException) {

        this.responseMap.put("result", "error_datasource");
        this.responseMap.put("information", datasourceException.getMessage());
        if (datasourceException.getCause() != null) {
          this.responseMap.put("cause", datasourceException.getCause());
        }

      }
    this.adapter.toJson(responseMap);
  }

  @Override
  public Object handle(Request request, Response response) {
    this.responseMap = new HashMap<>();
    String targetState = request.queryParams("state");
    String county = request.queryParams("county");

    if (targetState == null || county == null) {
      // Bad request! Send an error response.
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_state", targetState);
      responseMap.put("query_county", county);
      responseMap.put("type", "missing parameter");
      responseMap.put("error_arg", targetState == null ? "state" : "county");
      return this.adapter.toJson(responseMap);
    }

    try {
      try {

        // gets data using above parameters and querying the api
        BroadbandData data = this.proxy.getBroadbandData(new String[] {targetState, county});
        // If getBroadbandData returns with no issue, build response
        responseMap.put("result", "success");

        // want to return broadband percentage and time accessed
        responseMap.put("broadband", data.percentage().percentage());
        responseMap.put("date accessed", data.dateTime().getTime().toString());
        responseMap.put("state", data.state());
        responseMap.put("county", data.county());

      } catch (ExecutionException e) {
        //is catching illegalArgument or datasource
        responseMap.put("result", "error_bad_json");
        responseMap.put("information", e.getCause().getMessage());
        return this.adapter.toJson(responseMap);
      }
    } catch (DatasourceException e) {
      responseMap.put("result", e.getMessage());
      return this.adapter.toJson(responseMap);
    }

    // return response in form of json to post to page
    return adapter.toJson(responseMap);
  }
}
