package Handlers.CSVHandlers;

import Handlers.CSVHandlers.SearchFunctionality.Search;
import Handlers.Exceptions.ValueNotFoundException;
import Handlers.CSVHandlers.ViewCSV.NoFileLoadedResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class implements the search functionality from CSV Sprint but uses it to handle API
 * requests
 */
public class SearchCSV implements Route {

  //the data field is the parsedCSV
  List<List<String>> data;
  //the CSVDataSource is used to obtain the data
  CSVDataSource CSVDataSource;

  public SearchCSV(CSVDataSource CSVDataSource) {
    this.CSVDataSource = CSVDataSource;
  }

  /**
   * The handle() method tries to run a search with the provided queries, but returns invalid search
   * responses if anything goes wrong
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    this.data = CSVDataSource.getData();
    //If there is no data to be obtained, then no "loadcsv" call made yet
    if (this.data.size() == 0) {
      return new NoFileLoadedResponse().serialize();
    }
    //store queried values
    String value = request.queryParams("value");
    List<List<String>> results;
    String identifier = request.queryParams("identifier");

    //create responseMap
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("value_query", value);
    responseMap.put("column_identifier", identifier);

    //Checks to see if no value was provided-- cannot search for null!
    if (value == null) {
      return new InvalidSearchResponse("no search value provided").serialize();
    }

    // headers boolean determines type of column identifier: true = name of header, false = index
    //of column
    if (identifier == null) {
      // Search without column identifier
      results = new Search(this.data, value).getResults();
    } else {
      boolean headerSearch;
      try {
        //See if identifier is an index or header
        Integer.parseInt(identifier);
        headerSearch = false;
      } catch (NumberFormatException e) {
        headerSearch = true;
      }
      try {
        results = new Search(this.data, value, headerSearch, identifier).getResults();

      } catch (ValueNotFoundException e) {
        //Return response for valueNotFoundException thrown
        return new InvalidSearchResponse(e.getMessage()).serialize();
      }
    }
    //if no results turned up
    if (results.size() == 0) {
      responseMap.put("search_result", "failure");
    } else {
      //add successful results to field in response map
      responseMap.put("search_result", "success");
      responseMap.put("data", results);
    }
    return new SearchResultResponse(responseMap).serialize();
  }

  /**
   * Response returned if search method ran properly, regardless of number of matches
   * @param responseMap
   */
  public record SearchResultResponse(Map<String, Object> responseMap) {
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<SearchResultResponse> adapter = moshi.adapter(SearchResultResponse.class);
      return adapter.toJson(this);
    }
  }

  /**
   * Response returned if error occurred in the formation of the request of other strange
   * issues
   * @param failureReason
   */
  public record InvalidSearchResponse(String failureReason) {
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<InvalidSearchResponse> adapter = moshi.adapter(InvalidSearchResponse.class);
      return adapter.toJson(this);
    }
  }
}
