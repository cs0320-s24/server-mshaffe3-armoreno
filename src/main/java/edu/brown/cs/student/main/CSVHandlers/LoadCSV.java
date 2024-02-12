package CSVHandlers;

import CSVHandlers.SearchFunctionality.Creators.CreatorFromRow;
import CSVHandlers.SearchFunctionality.Creators.StringCreator;
import CSVHandlers.SearchFunctionality.FactoryFailureException;
import CSVHandlers.SearchFunctionality.Parser;
import com.squareup.moshi.Moshi;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSV implements Route {
  private final CSVDataSource CSVDataSource;

  public LoadCSV(CSVDataSource CSVDataSource) {
    this.CSVDataSource = CSVDataSource;
  }

  @Override
  public Object handle(Request request, Response response) {
    String filepath = request.queryParams("filepath");
    if(filepath ==null){
      return new FileLoadFailureResponse("no filepath provided");
    }

    try {
      List<List<String>> parsedData = this.parseData(filepath);
      this.CSVDataSource.setData(parsedData);
    } catch (IOException | FactoryFailureException e) {
      System.out.println(e.getMessage());
      return new FileLoadFailureResponse(filepath).serialize();
    }

    return new FileLoadSuccessResponse(filepath).serialize();
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
    //TODO: build in safeguards against outside file access
    BufferedReader csv = new BufferedReader(new FileReader(csvFileName));

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
  public record FileLoadSuccessResponse(String response_type, String filepath) {
    public FileLoadSuccessResponse(String filepath) {
      this("loadSuccess", filepath);
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
  public record FileLoadFailureResponse(String response_type, String filepath) {
    public FileLoadFailureResponse(String filepath) {
      this("loadFailure", filepath);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FileLoadFailureResponse.class).toJson(this);
    }
  }

//  public record NoFilepathProvidedResponse(){
//
//  }
}
