package CSVHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSV implements Route {
  List<List<String>> data;
  Proxy proxy;

  public ViewCSV(Proxy proxy) { // proxy is created in load, but
    this.proxy = proxy;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    this.data = proxy.getData();
    // If there has not been a loaded file/empty file, return a load failure response
    if (this.data.equals(new ArrayList<>())) {
      return new NoFileLoadedResponse().serialize();
    }
    return new ViewLoadedResponse(data).serialize();
  }

  /**
   * Response for successfully accessed loaded file
   *
   * @param response_type
   * @param csv
   */
  public record ViewLoadedResponse(String response_type, List<List<String>> csv) {
    public ViewLoadedResponse(List<List<String>> csv) {
      this("fileViewSuccess", csv);
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
      this("fileViewFailure");
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
