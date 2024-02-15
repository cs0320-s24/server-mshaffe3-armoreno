

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import CSVHandlers.CSVDataSource;
 import CSVHandlers.LoadCSV;
import CSVHandlers.LoadCSV.FileLoadSuccessResponse;
import CSVHandlers.ViewCSV;
import CSVHandlers.ViewCSV.NoFileLoadedResponse;
import CSVHandlers.ViewCSV.ViewLoadedResponse;
import com.squareup.moshi.Moshi;
 import java.io.IOException;
 import java.net.HttpURLConnection;
 import java.net.URL;
import java.util.ArrayList;
import okio.Buffer;
 import org.junit.jupiter.api.AfterEach;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import spark.Spark;

 public class CSVHandlerTest {
  private CSVDataSource source;
  @BeforeEach
  public void setup() {
   // Re-initialize state, etc. for _every_ test method run
   this.source = new CSVDataSource();

   // In fact, restart the entire Spark server for every test!
   Spark.get("loadcsv", new LoadCSV(this.source));
   Spark.get("viewcsv", new ViewCSV(this.source));
   Spark.init();
   Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
   // Gracefully stop Spark listening on both endpoints after each test
   Spark.unmap("loadcsv");
   Spark.unmap("viewcsv");
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

  @Test
  public void testLoadFailure() throws IOException {
   HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=null");
   // Get an OK response (the *connection* worked, the *API* provides an error response)
   assertEquals(200, clientConnection.getResponseCode());

   // Now we need to see whether we've got the expected Json response.
   Moshi moshi = new Moshi.Builder().build();
   // We'll use okio's Buffer class here
   LoadCSV.FileLoadFailureResponse response =
       moshi
           .adapter(LoadCSV.FileLoadFailureResponse.class)
           .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

   //checks that response type is as expected
   assertEquals("loadFailure", response.response_type());
   //checks that datasource is set as empty arraylist
   assertEquals(this.source.getData(), new ArrayList<>());

   clientConnection.disconnect();
  }

  @Test
  public void testLoadSuccess() throws IOException {
   HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stardata.csv");
   // Get an OK response (the *connection* worked, the *API* provides an error response)
   assertEquals(200, clientConnection.getResponseCode());

   // Now we need to see whether we've got the expected Json response.
   Moshi moshi = new Moshi.Builder().build();
   // We'll use okio's Buffer class here
   LoadCSV.FileLoadFailureResponse response =
       moshi
           .adapter(LoadCSV.FileLoadFailureResponse.class)
           .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
   clientConnection.disconnect();
   //Checks that response type is a success
   assertEquals("loadSuccess",response.response_type());
   //Checks that datasource contains the contents of the parsed file
   assertTrue(this.source.getData().get(0).contains("StarID"));
  }

  @Test
  public void testViewCSVLoaded() throws IOException {

   HttpURLConnection clientConnection = tryRequest(
       "loadcsv?filepath=data/stardata.csv");

   // Get an OK response (the *connection* worked, the *API* provides an error response)
   Moshi moshi = new Moshi.Builder().build();
   // We'll use okio's Buffer class here
       moshi
           .adapter(FileLoadSuccessResponse.class)
           .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

   HttpURLConnection clientConnection2 = tryRequest("viewcsv");

   // Now we need to see whether we've got the expected Json response.
   // We'll use okio's Buffer class here
   ViewLoadedResponse response2 =
       moshi
           .adapter(ViewLoadedResponse.class)
           .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
   clientConnection2.disconnect();
   clientConnection.disconnect();
   //After running load on a file, success response is sent
   assertEquals("fileViewSuccess",response2.response_type());
  }

  @Test
  public void testViewUnloaded() throws IOException {
   HttpURLConnection clientConnection = tryRequest("viewcsv");
   // Get an OK response (the *connection* worked, the *API* provides an error response)
   assertEquals(200, clientConnection.getResponseCode());

   // Now we need to see whether we've got the expected Json response.
   Moshi moshi = new Moshi.Builder().build();
   // We'll use okio's Buffer class here
   NoFileLoadedResponse response =
       moshi
           .adapter(NoFileLoadedResponse.class)
           .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
   clientConnection.disconnect();
   assertEquals("noFileLoadedFailure",response.response_type());
  }

  @Test
  public void testSearchCSVLoaded() {

  }

  @Test
  public void testSearchCSVNotFound() {}
 }
