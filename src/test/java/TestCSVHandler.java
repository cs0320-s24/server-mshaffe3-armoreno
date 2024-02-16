import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Handlers.CSVHandlers.CSVDataSource;
 import Handlers.CSVHandlers.LoadCSV;
import Handlers.CSVHandlers.LoadCSV.FileLoadSuccessResponse;
import Handlers.CSVHandlers.SearchCSV;
import Handlers.CSVHandlers.SearchCSV.InvalidSearchResponse;
import Handlers.CSVHandlers.SearchCSV.SearchResultResponse;
import Handlers.CSVHandlers.ViewCSV;
import Handlers.CSVHandlers.ViewCSV.NoFileLoadedResponse;
import Handlers.CSVHandlers.ViewCSV.ViewLoadedResponse;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestCSVHandler {
  private CSVDataSource source;

  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run
    this.source = new CSVDataSource();

    // In fact, restart the entire Spark server for every test!
    Spark.get("loadcsv", new LoadCSV(this.source));
    Spark.get("viewcsv", new ViewCSV(this.source));
    Spark.get("searchcsv", new SearchCSV(this.source));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("loadcsv");
    Spark.unmap("viewcsv");
    Spark.unmap("searchcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Helper method that calls the loadcsv handler for view and search tests
   *
   * @param filename
   * @throws IOException
   */
  private void loadFile(String filename) throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/" + filename);

    // Get an OK response (the *connection* worked, the *API* provides an error response)
    Moshi moshi = new Moshi.Builder().build();
    // We'll use okio's Buffer class here
    moshi
        .adapter(FileLoadSuccessResponse.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
  }

  @Test
  public void testLoadFailure() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=null");
    Moshi moshi = new Moshi.Builder().build();
    LoadCSV.FileLoadFailureResponse response =
        moshi
            .adapter(LoadCSV.FileLoadFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    // checks that response type is as expected
    assertEquals("loadFailure", response.response_type());
    // checks that datasource is set as empty arraylist
    assertEquals(this.source.getData(), new ArrayList<>());
    clientConnection.disconnect();
  }

  @Test
  public void testLoadSuccess() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stardata.csv");
    Moshi moshi = new Moshi.Builder().build();
    LoadCSV.FileLoadFailureResponse response =
        moshi
            .adapter(LoadCSV.FileLoadFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    // Checks that response type is a success
    assertEquals("loadSuccess", response.response_type());
    // Checks that datasource contains the contents of the parsed file
    assertTrue(this.source.getData().get(0).contains("StarID"));
  }

  @Test
  public void testViewCSVLoaded() throws IOException {
    this.loadFile("stardata.csv");
    HttpURLConnection clientConnection2 = tryRequest("viewcsv");
    Moshi moshi = new Moshi.Builder().build();
    ViewLoadedResponse response2 =
        moshi
            .adapter(ViewLoadedResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
    clientConnection2.disconnect();
    // After running load on a file, success response is sent
    assertEquals("fileViewSuccess", response2.response_type());
  }

  @Test
  public void testViewUnloaded() throws IOException {
    HttpURLConnection clientConnection = tryRequest("viewcsv");
    Moshi moshi = new Moshi.Builder().build();
    NoFileLoadedResponse response =
        moshi
            .adapter(NoFileLoadedResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    assertEquals("noFileLoadedFailure", response.response_type());
  }

  @Test
  public void testSearchCSVUnloaded() throws IOException {

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=Lethia");
    Moshi moshi = new Moshi.Builder().build();
    NoFileLoadedResponse response =
        moshi
            .adapter(NoFileLoadedResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    assertEquals("noFileLoadedFailure", response.response_type());
  }

  @Test
  public void testNoValueProvided() throws IOException {
    this.loadFile("stardata.csv");
    HttpURLConnection clientConnection = tryRequest("searchcsv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    InvalidSearchResponse response =
        moshi
            .adapter(InvalidSearchResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    assertEquals("no search value provided", response.failureReason());
  }

  @Test
  public void testValueNotFound() throws IOException {
    this.loadFile("RI_Census.csv");
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=Oklahoma");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    SearchResultResponse response =
        moshi
            .adapter(SearchResultResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    assertEquals("failure", response.responseMap().get("search_result"));
    assertEquals("Oklahoma", response.responseMap().get("value_query"));
  }

  @Test
  public void testSearchNoIdentifier() throws IOException {
    this.loadFile("RI_Census.csv");
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=Cranston");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    SearchResultResponse response =
        moshi
            .adapter(SearchResultResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    assertEquals("success", response.responseMap().get("search_result"));
    assertEquals(
        List.of(List.of("Cranston", "'77,145.00'", "'95,763.00'", "'38,269.00'")),
        response.responseMap().get("data"));
  }

  @Test
  public void testSearchWithHeader() throws IOException {
    this.loadFile("RI_Census.csv");
    HttpURLConnection clientConnection =
        tryRequest("searchcsv?value=Foster&identifier" + "=City/Town");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    SearchResultResponse response =
        moshi
            .adapter(SearchResultResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    assertEquals("success", response.responseMap().get("search_result"));
    assertEquals(
        List.of(List.of("Foster", "'99,892.00'", "'118,000.00'", "'37,382.00'")),
        response.responseMap().get("data"));
  }

  @Test
  public void testSearchWithIndex() throws IOException {
    this.loadFile("RI_Census.csv");
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=Cranston&identifier=0");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    SearchResultResponse response =
        moshi
            .adapter(SearchResultResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    assertEquals("success", response.responseMap().get("search_result"));
    assertEquals(
        List.of(List.of("Cranston", "'77,145.00'", "'95,763.00'", "'38,269.00'")),
        response.responseMap().get("data"));
  }
}
