package APIHandlers;

import Broadband.Broadband;
import Broadband.BroadbandData;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;


public class ACSDataSource implements APISource{
  private final Map<String, String> stateCodes;

  public ACSDataSource() throws DatasourceException, IOException {
    //instantiate hashMap
    this.stateCodes = new HashMap<>();
    //request from api

    List<List<String>> body = this.getBody(new URL("https","api.census.gov",
        "/data/2010/dec/sf1?get=NAME&for=state:*"));
    this.buildMap(body);
  }

  private void buildMap(List<List<String>> stateData) {
    stateData.remove(0);
    for(List<String> row: stateData){
      this.stateCodes.put(row.get(0).toLowerCase(Locale.US), row.get(1).toLowerCase(Locale.US));
    }
  }

  private String getCounty(String stateName, String countyName)
      throws IOException, DatasourceException {

    String stateCode = stateCodes.get(stateName);

    List<List<String>> body = this.getBody(new URL(
            "https",
            "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"+
        stateCode));

    for(List<String> county:body){
      if(county.get(0).toLowerCase(Locale.US).contains(countyName)){
        System.out.println(county);
        return county.get(2).toLowerCase(Locale.US);
      }
    }
    throw new DatasourceException("No such county in provided state.");
  }

  private String getState(String state) {
    return this.stateCodes.get(state.toLowerCase(Locale.US));
  }

  @Override
  public BroadbandData getBroadbandData(String[] loc) throws IOException, DatasourceException {
    return queryACS(loc[0], loc[1]);
  }

  private BroadbandData queryACS(String state, String county) throws IOException, DatasourceException {

    //convert names into codes
    String stateCode = getState(state.toLowerCase(Locale.US));
    String countyCode = getCounty(state.toLowerCase(Locale.US), county.toLowerCase(Locale.US));

    //create moshi adapter to parse response from api

    List<List<String>> body = getBody(new URL(
            "https",
            "api.census.gov",
            "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                    + countyCode
                    + "&in=state:"
                    + stateCode
                    +"&key=c62c39cc48683fae5510e74dbad5e1aa8cd6ed5a"));


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

  private List<List<String>> getBody(URL requestURL) throws IOException, DatasourceException {

    HttpURLConnection clientConnection = connect(requestURL);

    Moshi moshi = new Moshi.Builder().build();
    Type listType = Types.newParameterizedType(List.class, List.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
    List<List<String>> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    return body;
  }
}
