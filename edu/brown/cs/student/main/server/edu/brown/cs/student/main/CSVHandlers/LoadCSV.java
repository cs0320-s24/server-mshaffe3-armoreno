package edu.brown.cs.student.main.CSVHandlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.Creators.CreatorFromRow;
import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.Creators.StringCreator;
import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.FactoryFailureException;
import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.Parser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSV implements Route {
  private final Proxy proxy;

  public LoadCSV(Proxy proxy) {
    this.proxy = proxy;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    String filepath = request.queryParams("filepath");
    responseMap.put("filepath", filepath);
    try {
      List<List<String>> parsedData = this.parseData(filepath);
      this.proxy.setData(parsedData);
    } catch (IOException | FactoryFailureException e) {
      return new FileLoadFailureResponse(responseMap).serialize();
    }

    return new FileLoadSuccessResponse(responseMap).serialize();
  }

  /**
   * Parses the CSV data into a 2D array of strings
   *
   * @param csvFileName - the name of the file to be parsed
   * @return - 2D array of parsed data
   * @throws IOException - When there is an error opening the CSV data file as a Buffered Reader
   * @throws FactoryFailureException - When there is a malformed row
   */
  private List<List<String>> parseData(String csvFileName)
      throws IOException, FactoryFailureException {
    // creates a buffered reader out of the fileName created
    BufferedReader csv = new BufferedReader(new FileReader("data/" + csvFileName));

    // creates an object creator to pass to parse
    CreatorFromRow<List<String>> stringCreator = new StringCreator();

    // takes in above to create a parser object
    Parser<List<String>> parser = new Parser<>(csv, stringCreator);

    // returns a double array of strings from the above csv
    return parser.parse();
  }

  /**
   * Builds a response for a File Unable to Load
   *
   * @param response_type
   */
  public record FileLoadSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public FileLoadSuccessResponse(Map<String, Object> responseMap) {
      this("loadSuccess", responseMap);
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FileLoadSuccessResponse.class).toJson(this);
    }
  }

  /**
   * Builds a response for a File Unable to load
   *
   * @param response_type
   */
  public record FileLoadFailureResponse(String response_type, Map<String, Object> responseMap) {
    public FileLoadFailureResponse(Map<String, Object> responseMap) {
      this("loadFailure", responseMap);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FileLoadFailureResponse.class).toJson(this);
    }
  }
}
