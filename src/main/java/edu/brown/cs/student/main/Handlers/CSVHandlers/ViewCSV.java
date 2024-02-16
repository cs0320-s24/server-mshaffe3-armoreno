package Handlers.CSVHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class handles the API call for "viewcsv" by displaying the loaded csv file
 */
public class ViewCSV implements Route {
  //Uses the CSVDataSource for ensuring csv has been loaded.
  List<List<String>> data;
  CSVDataSource CSVDataSource;

  public ViewCSV(CSVDataSource CSVDataSource) { // proxy is created in load, but
    this.CSVDataSource = CSVDataSource;
  }

  /**
   * The handle() method checks for "loadcsv" and either returns a NoFileLoadedResponse() or a View
   * LoadedResponse() which serializes the data from the proxy class.
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    this.data = CSVDataSource.getData();

    // If there has not been a loaded file/empty file, return a load failure response
    if (this.data.size() == 0) {
      return new NoFileLoadedResponse().serialize();
    }
    return new ViewLoadedResponse(data).serialize();
  }

  /**
   * Response for successfully accessed loaded file
   *
   * @param response_type
   * @param data
   */
  public record ViewLoadedResponse(String response_type, List<List<String>> data) {
    public ViewLoadedResponse(List<List<String>> data) {
      this("fileViewSuccess", data);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ViewLoadedResponse> adapter = moshi.adapter(ViewLoadedResponse.class);
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

  public record NoFileLoadedResponse(String response_type) {
    public NoFileLoadedResponse() {
      this("noFileLoadedFailure");
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(NoFileLoadedResponse.class).toJson(this);
    }
  }
}
