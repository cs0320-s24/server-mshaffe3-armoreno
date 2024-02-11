package edu.brown.cs.student.main.CSVHandlers;

import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.Search;
import java.util.List;
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

        //TODO need to convert List<List<String>> into JSON!



        return null;
    }
}
