package APIHandlers;

import Broadband.Broadband;
import Broadband.BroadbandData;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import okio.Buffer;

public class ACSDataSource implements APISource{

  Map<String, String> stateCodes;

  private String getCounty(String targetState, String county) {

    return "031";
  }

  private String getState(String county) {

    return "06";
  }
  @Override
  public BroadbandData getBroadbandData(String[] loc) throws IOException, DatasourceException {
    return getBroadbandData(getState(loc[0]), getCounty(loc[0], loc[1]));
  }

  public BroadbandData getBroadbandData(String state, String county) throws IOException, DatasourceException {

    String stateCode = getState(state);
    String countyCode = getCounty(county, state);
    URL requestURL =
        new URL(
            "https",
            "api.census.gov",
            "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                + countyCode
                + "&in=state:"
                + stateCode);

    //+"&key=c62c39cc48683fae5510e74dbad5e1aa8cd6ed5a"

    HttpURLConnection clientConnection = connect(requestURL);


    Moshi moshi = new Moshi.Builder().build();

    JsonAdapter<Broadband> adapter = moshi.adapter(Broadband.class).nonNull();
    // NOTE: important! pattern for handling the input stream

    // makes our object to interpret received JSON
    System.out.println(new Buffer().readFrom(clientConnection.getInputStream()));


    Broadband body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    System.out.println("read from client");
    // disconnects connection from api
    clientConnection.disconnect();
    return new BroadbandData(body, Calendar.getInstance(), state, county);
  }

  private static HttpURLConnection connect(URL requestURL) throws IOException, DatasourceException {

    // connects with api
    URLConnection urlConnection = requestURL.openConnection();
            if(! (urlConnection instanceof HttpURLConnection))
                throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
            if(clientConnection.getResponseCode() != 200)
                throw new DatasourceException("unexpected: API connection not success status"
                        +clientConnection.getResponseMessage());
    return clientConnection;
  }
}
