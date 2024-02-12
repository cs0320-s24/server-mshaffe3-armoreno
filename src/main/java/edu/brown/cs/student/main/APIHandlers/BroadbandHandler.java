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

  ACSDataSource proxy;

  public BroadbandHandler(ACSDataSource proxy) {
    this.proxy = proxy;
  }

  @Override
  public Object handle(Request request, Response response) throws DatasourceException, IOException {

    Moshi moshi = new Moshi.Builder().build();

    //creates adapter to format information into a JSON map
    Type mapStringString = Types.newParameterizedType(Map.class, String.class, String.class);
    JsonAdapter<Map<String, String>> adapter = moshi.adapter(mapStringString);

    //map to put results and send back to user
    Map<String, String> responseMap = new HashMap<>();


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

//    try{
//      try{

        //gets data using above parameters and querying the api
        BroadbandData data = this.proxy.getBroadbandData(new String[]{targetState, county});
        // If getBroadbandData returns with no issue, build response
        responseMap.put("type", "success");

        // want to return broadband percentage and time accessed
        responseMap.put("broadband", data.percentage().percentage());
        responseMap.put("date accessed", data.dateTime().getTime().toString());
        responseMap.put("state", data.state());
        responseMap.put("county", data.county());

//      }catch (IOException ioException){
//        responseMap.put("result", ioException.getMessage());
//      }
//    }catch (DatasourceException e){
//      responseMap.put("result", e.getMessage());
//    }

      //return response in form of json to post to page
    return adapter.toJson(responseMap);
  }
}
