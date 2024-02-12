package APIHandlers;

import Broadband.Broadband;
import Broadband.BroadbandData;
import Broadband.BroadbandWrapper;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
                + stateCode
                    +"&key=c62c39cc48683fae5510e74dbad5e1aa8cd6ed5a");

    HttpURLConnection clientConnection = connect(requestURL);


    Moshi moshi = new Moshi.Builder().build();

    Type listType = Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
    List<List<String>> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    System.out.println("here");
    System.out.println(body.get(1).get(1));

    // disconnects connection from api
    clientConnection.disconnect();
    return new BroadbandData(new Broadband(body.get(1).get(1)), Calendar.getInstance(), state, county);
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
