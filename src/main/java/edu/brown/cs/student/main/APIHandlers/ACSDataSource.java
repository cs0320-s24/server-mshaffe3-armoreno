package APIHandlers;

import Broadband.Broadband;
import Broadband.BroadbandData;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;

public class ACSDataSource implements APISource{
  private Map<String, String> stateCodes;
  public ACSDataSource() throws DatasourceException, IOException {
    this.stateCodes = new HashMap<>();
    this.initialQueryStateCodes();
  }

  private String getCounty(String targetState, String county) {

    return "031";
  }

  private String getState(String state) {
    return this.stateCodes.get(state);
  }
  @Override
  public BroadbandData getBroadbandData(String[] loc) throws IOException, DatasourceException {
    return queryACS(getState(loc[0]), getCounty(loc[0], loc[1]));
  }

  private void initialQueryStateCodes() throws IOException, DatasourceException {
    URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?"
        +"get=NAME&for=state:*");
    HttpURLConnection clientConnection = connect(requestURL);

    Moshi moshi = new Moshi.Builder().build();
    Type listType = Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
    List<List<String>> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    body.remove(0); //takes out header row
    //populates the state
    for(List<String> row :body){
      stateCodes.put(row.get(0), row.get(1));
    }
    clientConnection.disconnect();


  }

  private BroadbandData queryACS(String state, String county) throws IOException, DatasourceException {

    //convert names into codes
    String stateCode = getState(state);
    String countyCode = getCounty(county, state);

    //query the api with above quotes
    URL requestURL =
        new URL(
            "https",
            "api.census.gov",
            "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                + countyCode
                + "&in=state:"
                + stateCode
                    +"&key=c62c39cc48683fae5510e74dbad5e1aa8cd6ed5a");
    HttpURLConnection clientConnection = connect(requestURL);

    //create moshi adapter to parse response from api
    Moshi moshi = new Moshi.Builder().build();
    Type listType = Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
    List<List<String>> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));


    // disconnects connection from api
    clientConnection.disconnect();

    //returns data needed in the form of a Broadband data
    return new BroadbandData(new Broadband(body.get(1).get(1)), Calendar.getInstance(), state, county);
  }

  private static HttpURLConnection connect(URL requestURL) throws IOException, DatasourceException {

    // connects with api and requests
    URLConnection urlConnection = requestURL.openConnection();
            if(! (urlConnection instanceof HttpURLConnection clientConnection))
                throw new DatasourceException("unexpected: result of connection wasn't HTTP");
      clientConnection.connect(); // GET
            if(clientConnection.getResponseCode() != 200)
                throw new DatasourceException("unexpected: API connection not success status"
                        +clientConnection.getResponseMessage());
    return clientConnection;
  }
}
