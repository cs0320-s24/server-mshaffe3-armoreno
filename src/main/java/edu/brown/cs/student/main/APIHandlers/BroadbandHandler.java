package edu.brown.cs.student.main.APIHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Broadband.Broadband;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class BroadbandHandler implements Route {

    ACSDataSource dataSource;
    public BroadbandHandler(ACSDataSource acsDataSource){
        this.dataSource = acsDataSource;
    }

    @Override
    public Object handle(Request request, Response response) throws IOException {

        Moshi moshi = new Moshi.Builder().build();

        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = new HashMap<>();

        String targetState = request.queryParams("state");
        String county = request.queryParams("county");

        if(targetState == null || county == null){
            // Bad request! Send an error response.
            responseMap.put("query_state", targetState);
            responseMap.put("query_county", county);
            responseMap.put("type", "error");
            responseMap.put("error_type", "missing_parameter");
            responseMap.put("error_arg", targetState == null ? "state" : "county");
            return adapter.toJson(responseMap);
        }

        // Generate the reply
//        try {
            // Low-level NWS API invocation isn't the job of this class!
            // Neither is caching! Just get the data from whatever the source is.
            BroadbandData data = this.dataSource.getBroadbandData(targetState, county);
            // Building responses *IS* the job of this class:
            responseMap.put("type", "success");

            //want to return broadband percentage and time accessed
            responseMap.put("Broadband", data.percentage());
            responseMap.put("Date Accessed", data.dateTime().getTime());
            return adapter.toJson(responseMap);

    }


}
