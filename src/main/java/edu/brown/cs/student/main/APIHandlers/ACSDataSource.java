package APIHandlers;


import Broadband.BroadbandData;
import Broadband.Broadband;
import com.squareup.moshi.Moshi;
import okio.Buffer;
import com.squareup.moshi.JsonAdapter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Map;

public class ACSDataSource {

  Map<String, String> stateCodes;

  private int getCounty(String targetState, String county) {

    return 31;
  }

  private int getState(String county) {
    return 6;
  }

  public BroadbandData getBroadbandData(String state, String county) throws IOException {
    return getBroadbandData(getState(state), getCounty(county, state));
  }

  private BroadbandData getBroadbandData(int stateCode, int countyCode) throws IOException {
    URL requestURL =
        new URL(
            "https",
            "api.census.gov",
            "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                + countyCode
                + "&in=state:"
                + stateCode);

    HttpURLConnection clientConnection = connect(requestURL);

    Moshi moshi = new Moshi.Builder().build();

    JsonAdapter<Broadband> adapter = moshi.adapter(Broadband.class).nonNull();
    // NOTE: important! pattern for handling the input stream

    // makes our object to interpret received JSON
    Broadband body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // disconnects connection from api
    clientConnection.disconnect();

    return new BroadbandData(body, Calendar.getInstance());
  }

  private static HttpURLConnection connect(URL requestURL) throws IOException {

    // connects with api
    URLConnection urlConnection = requestURL.openConnection();
    //        if(! (urlConnection instanceof HttpURLConnection))
    //            throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    //        if(clientConnection.getResponseCode() != 200)
    //            throw new DatasourceException("unexpected: API connection not success status
    // "+clientConnection.getResponseMessage());
    return clientConnection;
  }
}
