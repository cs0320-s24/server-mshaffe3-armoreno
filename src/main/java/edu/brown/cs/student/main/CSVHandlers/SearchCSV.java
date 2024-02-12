package CSVHandlers;

import CSVHandlers.SearchFunctionality.Search;
import CSVHandlers.SearchFunctionality.ValueNotFoundException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSV implements Route {
  List<List<String>> data;
  CSVDataSource CSVDataSource;

  public SearchCSV(CSVDataSource CSVDataSource) {
    this.CSVDataSource = CSVDataSource;
  }

  @Override
  public Object handle(Request request, Response response) {
    this.data = CSVDataSource.getData();
    String value = request.queryParams("value");
    List<List<String>> results;
    String identifier = request.queryParams("identifier");

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("value_query", value);
    responseMap.put("column_identifier", identifier);

    if(value ==null){
      return new InvalidSearchResponse("no search value provided");
    }

    // headers boolean true = words, false = index
    if (identifier == null) {
      // Search without column identifier
      results = new Search(this.data, value).getResults();
    } else { // TODO need to catch value not found exception and return bad response
      boolean headerSearch;
      try {
        Integer.parseInt(identifier);
        headerSearch = false;
      } catch (NumberFormatException e) {
        headerSearch = true;
      }
    }
      if (results.size() == 0) {
        responseMap.put("search_result", "failure");
      } else {
        responseMap.put("search_result", "success");
        responseMap.put("data", results);
      }

    return new SearchResultResponse(responseMap).serialize();
  }

  public record SearchResultResponse(Map<String, Object> responseMap) {

    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchResultResponse> adapter = moshi.adapter(SearchResultResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        throw e;
      }
    }
  }

  private record InvalidSearchResponse(String failureReason) {


    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<InvalidSearchResponse> adapter = moshi.adapter(InvalidSearchResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }
}
