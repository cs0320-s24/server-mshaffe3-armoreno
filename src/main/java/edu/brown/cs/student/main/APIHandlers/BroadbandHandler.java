package edu.brown.cs.student.main.APIHandlers;

import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {



    @Override
    public Object handle(Request request, Response response) throws Exception {

        /**
         *     Set<String> params = request.queryParams();
         *     String participants = request.queryParams("participants");
         *
         *     // Creates a hashmap to store the results of the request
         *     Map<String, Object> responseMap = new HashMap<>();
         *
         *     try {
         *       // Sends a request to the API and receives JSON back
         *       String activityJson = this.sendRequest(Integer.parseInt(participants));
         *       // Deserializes JSON into an Activity
         *       Activity activity = ActivityAPIUtilities.deserializeActivity(activityJson);
         *       // Adds results to the responseMap
         *       responseMap.put("result", "success");
         *       responseMap.put("activity", activity);
         *       responseMap.put("participants", participants);
         *       return responseMap;
         *     } catch (Exception e) {
         *       e.printStackTrace();
         *       // This is a relatively unhelpful exception message. An important part of this sprint will be
         *       // in learning to debug correctly by creating your own informative error messages where Spark
         *       // falls short.
         *       responseMap.put("result", "Exception");
         *     }
         *     return responseMap;
         */
        return null;
    }
}
