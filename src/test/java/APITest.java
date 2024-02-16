import Handlers.Broadband.Broadband;
import Handlers.BroadbandHandler.BroadbandHandler;
import Handlers.BroadbandHandler.DataSource.ACSDataSource;
import Handlers.BroadbandHandler.DataSource.CacheType;
import Handlers.Exceptions.DatasourceException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APITest {
    @BeforeAll
    public static void setup_before_everything() {
        // Set the Spark port number.
        Spark.port(0);

        // Remove the logging spam during tests
        Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
    }

    private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    private JsonAdapter<Map<String, String>> responseAdapter;
    private JsonAdapter<Broadband> broadbandAdapter;

    private String[] loc;

    @BeforeEach
    public void setup() throws DatasourceException {
        // In fact, restart the entire Spark server for every test!
        Spark.get("broadband", new BroadbandHandler(new ACSDataSource(), CacheType.MAX_SIZE, 1000));
        Spark.init();
        Spark.awaitInitialization(); // don't continue until the server is listening

        Moshi moshi = new Moshi.Builder().build();
        responseAdapter = moshi.adapter(mapStringObject);
        broadbandAdapter = moshi.adapter(Broadband.class);

        loc = new String[]{"Hardin+County", "Kentucky"};
    }

    @AfterEach
    public void teardown() {
        // Gracefully stop Spark listening on both endpoints after each test
        Spark.unmap("broadband");
        Spark.awaitStop(); // don't proceed until the server is stopped
    }

    private static HttpURLConnection tryRequest(String county, String state) throws IOException {
        // Configure the connection (but don't actually send the request yet)
        URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + "broadband" + "?county=" + county + "&state=" + state);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

        // we are getting information from the api
        clientConnection.setRequestMethod("GET");

        clientConnection.connect();
        return clientConnection;
    }

    @Test
    public void testSuccess() throws IOException {
        HttpURLConnection connection = tryRequest(loc[0], loc[1]);

        assertEquals(200, connection.getResponseCode());

        Map<String, String> responseBody = responseAdapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
        showDetailsIfError(responseBody);

        assertEquals("success", responseBody.get("result"));
        assertEquals("87.2", responseBody.get("percentage"));
        assertEquals("hardin county", responseBody.get("county"));
        assertEquals("kentucky", responseBody.get("state"));

        connection.disconnect();
    }

    @Test
    public void testWrongCounty() throws IOException {
        HttpURLConnection connection = tryRequest("harding+county", loc[1]);

        assertEquals(200, connection.getResponseCode());

        Map<String, String> responseBody = responseAdapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
        showDetailsIfError(responseBody);

        assertEquals("error_bad_json", responseBody.get("result"));
        assertEquals("No such county in provided state: kentucky", responseBody.get("information"));

        connection.disconnect();
    }

    @Test
    public void testWrongState() throws IOException {
        HttpURLConnection connection = tryRequest(loc[0], "California");

        assertEquals(200, connection.getResponseCode());

        Map<String, String> responseBody = responseAdapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
        showDetailsIfError(responseBody);

        assertEquals("error_bad_json", responseBody.get("result"));
        assertEquals("No such county in provided state: california", responseBody.get("information"));

        connection.disconnect();
    }


    /**
     * Helper to make working with a large test suite easier: if an error, print more info.
     *
     * @param body
     */
    private void showDetailsIfError(Map<String, String> body) {
        if (body.get("type") == "0" && "error".equals(body.get("result"))) {
            System.out.println(body);
        }
    }
}
