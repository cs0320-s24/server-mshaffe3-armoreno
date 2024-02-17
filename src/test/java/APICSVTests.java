import static org.junit.jupiter.api.Assertions.assertEquals;

import Handlers.BroadbandHandler.BroadbandHandler;
import Handlers.BroadbandHandler.DataSource.ACSDataSource;
import Handlers.BroadbandHandler.DataSource.CacheType;
import Handlers.CSVHandlers.CSVDataSource;
import Handlers.CSVHandlers.LoadCSV;
import Handlers.CSVHandlers.LoadCSV.FileLoadSuccessResponse;
import Handlers.CSVHandlers.SearchCSV;
import Handlers.CSVHandlers.SearchCSV.SearchResultResponse;
import Handlers.CSVHandlers.ViewCSV;
import Handlers.CSVHandlers.ViewCSV.ViewLoadedResponse;
import Handlers.Exceptions.DatasourceException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** Tests integration between CSVHandlers and BroadbandHandlers */
public class APICSVTests {

  /** setup before all tests */
  @BeforeAll
  public static void setup_before_everything() {

    // Remove the logging spam during tests
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, String>> responseAdapter;
  private JsonAdapter<Integer> numDataAdapter;

  /**
   * set up handlers before each test
   *
   * @throws DatasourceException
   */
  @BeforeEach
  public void setup() throws DatasourceException {
    CSVDataSource source = new CSVDataSource();
    // In fact, restart the entire Spark server for every test!
    Spark.get("loadcsv", new LoadCSV(source));
    Spark.get("viewcsv", new ViewCSV(source));
    Spark.get("searchcsv", new SearchCSV(source));
    Spark.get("broadband", new BroadbandHandler(new ACSDataSource(), CacheType.NO_LIMIT, 0));

    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    Moshi moshi = new Moshi.Builder().build();
    responseAdapter = moshi.adapter(mapStringObject);
    numDataAdapter = moshi.adapter(Integer.class);
  }

  /** clean up handlers after each test */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("loadcsv");
    Spark.unmap("viewcsv");
    Spark.unmap("searchcsv");
    Spark.unmap("broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * helper method to make api calls
   *
   * @param apiCall - which handler to activate
   * @return - api connection
   * @throws IOException - thrown by URL
   */
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
   * Tests all API handlers to show full server capabilities
   *
   * @throws IOException - thrown by moshi
   */
  @Test
  public void integrationTest() throws IOException {
    // First, call loadcsv
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=data/census/RI_Census.csv");
    assertEquals(200, loadConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FileLoadSuccessResponse response =
        moshi
            .adapter(FileLoadSuccessResponse.class)
            .fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("loadSuccess", response.response_type());
    loadConnection.disconnect();

    // next, call broadband
    HttpURLConnection bbConnection = tryRequest("broadband?state=Kentucky&county=Hardin+County");
    assertEquals(200, bbConnection.getResponseCode());
    Map<String, String> responseBody =
        responseAdapter.fromJson(new Buffer().readFrom(bbConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));
    assertEquals("87.2", responseBody.get("percentage"));
    assertEquals("hardin county", responseBody.get("county"));
    assertEquals("kentucky", responseBody.get("state"));
    bbConnection.disconnect();

    // now call searchcsv
    HttpURLConnection searchConnection = tryRequest("searchcsv?value=Foster&identifier=City/Town");
    assertEquals(200, searchConnection.getResponseCode());
    SearchResultResponse result =
        moshi
            .adapter(SearchResultResponse.class)
            .fromJson(new Buffer().readFrom(searchConnection.getInputStream()));
    searchConnection.disconnect();
    assertEquals("success", result.responseMap().get("search_result"));
    assertEquals(
        List.of(List.of("Foster", "'99,892.00'", "'118,000.00'", "'37,382.00'")),
        result.responseMap().get("data"));

    // finally, viewcsv
    HttpURLConnection viewConnection = tryRequest("viewcsv");
    assertEquals(200, viewConnection.getResponseCode());
    ViewLoadedResponse viewLoadedResponse =
        moshi
            .adapter(ViewLoadedResponse.class)
            .fromJson(new Buffer().readFrom(viewConnection.getInputStream()));
    viewConnection.disconnect();
    assertEquals("fileViewSuccess", viewLoadedResponse.response_type());
  }
}
