package APIHandlers;

import APIHandlers.Exceptions.DatasourceException;
import APIHandlers.Broadband.BroadbandData;
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

/**
 * This Class Handles API requests and returns broadband data for the queried state and county
 */
public class BroadbandHandler implements Route {
  //The ACS Proxy acts as an intermediary for the datasource and is responsible for caching
  ACSProxy proxy;
  Map<String, String> responseMap;
  JsonAdapter<Map<String, String>> adapter;
  Boolean instantiationError;

  /**
   * Constructor sets up the response map and calls instantiateshandling()
   */
  public BroadbandHandler(APISource dataSource){
    this.instantiationError = false;
    //moshi adapter to build responses
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringString = Types.newParameterizedType(Map.class, String.class, String.class);
    this.adapter = moshi.adapter(mapStringString);

    // map to put results and send back to user
    this.responseMap = new HashMap<>();
    this.instantiateHandling(dataSource);
  }

  /**
   * This helper method creates a new Proxy with caching parameters
   * If there is a datasource issue, an informative error response is sent back
   */
  private void instantiateHandling(APISource dataSource){
    try{
      //new Proxy is created with cache parameters.
        this.proxy = new ACSProxy(dataSource, CacheType.MAX_SIZE, 1000);
      //thrown from creation of ACSDatasource
      }catch(DatasourceException datasourceException) {
        this.instantiationError = true;
        this.responseMap.put("result", "error_datasource");
        this.responseMap.put("information", datasourceException.getMessage());
        if (datasourceException.getCause() != null) {
          this.responseMap.put("cause", datasourceException.getCause().toString());
        }

      }

  }

  /**
   * The handle method returns a response to the server with broadband data, access time and date,
   * state and county queries; or, it returns an informative error response
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    //if this is true, an error upon instantiation of datasource must have occurred
    if(this.instantiationError){
      return this.adapter.toJson(responseMap);
    }
    this.responseMap = new HashMap<>();
    String targetState = request.queryParams("state");
    String county = request.queryParams("county");

    if (targetState.isBlank() || county.isBlank()) {
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
        responseMap.put("date accessed", data.dateTime());
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
      responseMap.put("information", e.getCause().getMessage());
      return this.adapter.toJson(responseMap);
    }

    System.out.println(responseMap);
    // return response in form of json to post to page
    return adapter.toJson(responseMap);
  }
}
