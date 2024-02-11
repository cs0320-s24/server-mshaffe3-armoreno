package edu.brown.cs.student.main.CSVHandlers;

import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.Search;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSV implements Route {
    List<List<String>> data;
    public SearchCSV(Proxy proxy){
        this.data = proxy.getData();
    }


    @Override
    public Object handle(Request request, Response response) throws Exception {
        String value = request.queryParams("value");
        List<List<String>> results;
        String identifier = request.queryParams("identifier");

        Map<String, Object> responseMap = new HashMap<>();


        //headers boolean true = words, false = index
        if(identifier == null){
            //Search without column identifier
           results = new Search(this.data, value).getResults();
        }
        else{ // TODO need to catch value not found exception and return bad response
            boolean headerSearch;
            try{
                Integer.parseInt(identifier);
                headerSearch = false;
            }
            catch(NumberFormatException e){
                headerSearch = true;
            }
            results = new Search(this.data, value, headerSearch, identifier).getResults();
        }
        if(results.size()==0){
            responseMap.put("Search results for "+value, "failure");
        }
        else {
            responseMap.put("Search results for "+value, "success");
            for (List<String> result : results) {
                responseMap.put("Match #" + results.indexOf(result), result);
            }
        }

        //TODO need to serialize!



        return null;
    }
}
