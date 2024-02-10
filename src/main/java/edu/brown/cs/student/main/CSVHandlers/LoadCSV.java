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

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filepath = request.queryParams("filename");
    try{
      List<List<String>> parsedData = this.parseData(filepath);
      Proxy myProxy = new Proxy(parsedData);
    }
    catch (IOException | FactoryFailureException e) {
      return new FileLoadFailureResponse().serialize();
    }

    return new FileLoadSuccessResponse().serialize();
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
   * @param response_type
   */
  public record FileLoadSuccessResponse(String response_type) {
    public FileLoadSuccessResponse() {
      this("loadSuccess");
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
   * @param response_type
   */
  public record FileLoadFailureResponse(String response_type) {
    public FileLoadFailureResponse() {
      this("loadFailure");
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


