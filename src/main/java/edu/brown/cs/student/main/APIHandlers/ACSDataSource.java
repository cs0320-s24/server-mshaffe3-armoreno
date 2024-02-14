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

  public ACSDataSource() throws DatasourceException{
    //instantiate hashMap
    this.stateCodes = new HashMap<>();
    //request from api

    try{
      List<List<String>> body = this.getBody(new URL("https","api.census.gov",
              "/data/2010/dec/sf1?get=NAME&for=state:*"));
      this.buildMap(body);
    }catch(IOException e){
      throw new DatasourceException(e.getMessage(), e);
    }

  }

  private void buildMap(List<List<String>> stateData) {
    stateData.remove(0);
    for(List<String> row: stateData){
      this.stateCodes.put(row.get(0).toLowerCase(Locale.US), row.get(1).toLowerCase(Locale.US));
    }
  }

  /**
   * returns the ACS county code given a state name and county name by querying the API
   * @param stateName
   * @param countyName
   * @return
   * @throws DatasourceException
   */
  private String getCounty(String stateName, String countyName)
      throws DatasourceException {

    String stateCode = stateCodes.get(stateName);
    //queries the API with a given state code
    try{
      List<List<String>> body = this.getBody(new URL(
              "https",
              "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"+
              stateCode));

      //searches for the correct county
      for(List<String> county:body){
        if(county.get(0).toLowerCase(Locale.US).contains(countyName)){
          //returns county code
          return county.get(2);
        }
      }
      throw new DatasourceException("No such county in provided state.");

    }catch (IOException e){
      throw new DatasourceException(e.getMessage(), e);
    }
  }

  /**
   * accesses the Hashmap created in the constructor of the class to return the state code
   * @param state
   * @return
   */
  private String getState(String state) {
    return this.stateCodes.get(state.toLowerCase(Locale.US));
  }

  /**
   * Returns a query from the ACS API in the form of broadband data
   * @param loc
   * @return
   * @throws IOException
   * @throws DatasourceException
   */
  @Override
  public BroadbandData getBroadbandData(String[] loc) throws DatasourceException {
    return queryACS(loc[0], loc[1]);
  }

  /**
   * Helper method that creates the URL and builds the record of Broadband Data from the
   * body returned from the query
   * @param state
   * @param county
   * @return
   * @throws DatasourceException
   */
  private BroadbandData queryACS(String state, String county) throws DatasourceException {

    //convert names into codes
    String stateCode = getState(state.toLowerCase(Locale.US));
    String countyCode = getCounty(state.toLowerCase(Locale.US), county.toLowerCase(Locale.US));

    //create moshi adapter to parse response from api
    try{
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

    }catch (IOException e){
      throw new DatasourceException(e.getMessage(), e);
    }

  }

  private static HttpURLConnection connect(URL requestURL) throws DatasourceException {
    // connects with api and requests
    try{
      URLConnection urlConnection = requestURL.openConnection();
      if(! (urlConnection instanceof HttpURLConnection clientConnection))
        throw new DatasourceException("unexpected: result of connection wasn't HTTP");
      clientConnection.connect(); // GET
      if(clientConnection.getResponseCode() != 200)
        throw new DatasourceException("unexpected: API connection not success status"
                +clientConnection.getResponseMessage());
      return clientConnection;
    }catch (IOException e){
      throw new DatasourceException(e.getMessage(), e);
    }
  }

  /**
   * The helper method queries the API at the requested URL and uses moshi to deserialize it
   * @param requestURL
   * @return
   * @throws IOException
   * @throws DatasourceException
   */
  private List<List<String>> getBody(URL requestURL) throws DatasourceException {

    try{
      HttpURLConnection clientConnection = connect(requestURL);

      Moshi moshi = new Moshi.Builder().build();
      Type listType = Types.newParameterizedType(List.class, List.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

      List<List<String>> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      return body;
    }catch(IOException e){
      throw new DatasourceException(e.getMessage(), e);
    }
  }
}
