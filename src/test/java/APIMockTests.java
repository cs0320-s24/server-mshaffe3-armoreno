import static org.junit.jupiter.api.Assertions.assertEquals;

import Handlers.Broadband.Broadband;
import Handlers.Broadband.BroadbandData;
import Handlers.BroadbandHandler.BroadbandHandler;
import Handlers.BroadbandHandler.DataSource.CacheType;
import Handlers.BroadbandHandler.DataSource.MockAPISource;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** this class tests our API using a mock data source */
public class APIMockTests {

  /** runs before all tests */
  @BeforeAll
  public static void setup_before_everything() {

    // Remove the logging spam during tests
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, String>> responseAdapter;
  private JsonAdapter<Integer> numDataAdapter;

  private String[] loc;

  /** sets up broadband handler before every test */
  @BeforeEach
  public void setup() {
    // In fact, restart the entire Spark server for every test!

    Spark.get(
        "broadband",
        new BroadbandHandler(
            new MockAPISource(
                new BroadbandData(
                    "success",
                    new Broadband("30"),
                    Calendar.getInstance().getTime().toString(),
                    "Kentucky",
                    "Hardin County")),
            CacheType.MAX_SIZE,
            1000));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    Moshi moshi = new Moshi.Builder().build();
    responseAdapter = moshi.adapter(mapStringObject);
    numDataAdapter = moshi.adapter(Integer.class);

    loc = new String[] {"Hardin+County", "Kentucky"};
  }

  /** cleans up handler after every test */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * helper method to help make api requests to broadband
   *
   * @param county - county to query
   * @param state - state to query
   * @return - api connection
   * @throws IOException - thrown by URL
   */
  private static HttpURLConnection tryRequest(String county, String state) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL =
        new URL(
            "http://localhost:"
                + Spark.port()
                + "/"
                + "broadband"
                + "?county="
                + county
                + "&state="
                + state);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // we are getting information from the api
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * tests a regular call from mock api
   *
   * @throws IOException - thrown by responseAdapter
   */
  @Test
  public void testRegularCallSuccess() throws IOException {

    HttpURLConnection connection = tryRequest(loc[0], loc[1]);

    assertEquals(200, connection.getResponseCode());

    Map<String, String> responseBody =
        responseAdapter.fromJson(new Buffer().readFrom(connection.getInputStream()));

    assertEquals("success", responseBody.get("result"));

    assertEquals(numDataAdapter.toJson(30), responseBody.get("percentage"));

    connection.disconnect();
  }

  /**
   * tests if both parameters are blank
   *
   * @throws IOException - thrown by responseAdapter
   */
  @Test
  public void testBothEmptyParams() throws IOException {
    HttpURLConnection connection = tryRequest("", "");

    assertEquals(200, connection.getResponseCode());

    Map<String, String> responseBody =
        responseAdapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("error_bad_request", responseBody.get("result"));

    connection.disconnect();
  }

  /**
   * tests if one parameter is empty
   *
   * @throws IOException - thrown by responseAdapter
   */
  @Test
  public void testOneEmptyParams() throws IOException {
    HttpURLConnection connection = tryRequest(loc[0], "");

    assertEquals(200, connection.getResponseCode());

    Map<String, String> responseBody =
        responseAdapter.fromJson(new Buffer().readFrom(connection.getInputStream()));

    assertEquals("error_bad_request", responseBody.get("result"));

    connection.disconnect();
  }
}
