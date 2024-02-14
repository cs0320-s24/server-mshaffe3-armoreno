package APIHandlers;

import Broadband.BroadbandData;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ACSProxy implements APISource{

  private final ACSDataSource source;
  private LoadingCache<Location, BroadbandData> cache;


  public ACSProxy() throws DatasourceException, IOException {
          this.source = new ACSDataSource();


    this.makeCache();
  }

  private void makeCache(){
      CacheLoader<Location, BroadbandData> loader = new CacheLoader<>() {


          @Override
              public BroadbandData load (Location location) throws DatasourceException, IOException {
              return makeRequest(location.loc);
            }
          };

      this.cache =CacheBuilder.newBuilder().recordStats().build(loader);

  }

    /**
     * Called by cache if there isn't already a result for this location
     * @param loc - county and state in string format
     * @return - result of search by user
     * @throws DatasourceException - If accessing Datasource is a problem
     * @throws IOException
     */
  public BroadbandData makeRequest(String[] loc) throws DatasourceException, IOException {
      return this.source.getBroadbandData(loc);
  }

    /**
     * Method makes a Location object of the inputted location and checks whether it is in the cache to return
     * before making a new call to getBroadbandData
     * @param loc - county and state user is searching for in String array format
     * @return - user result from cache or call from getBroadbandData
     * @throws ExecutionException - if getBroadbandData throws an error
     */
  @Override
  public BroadbandData getBroadbandData(String[] loc) throws ExecutionException {

      String[] newLoc = new String[] {loc[0].toLowerCase(Locale.US), loc[1].toLowerCase(Locale.US)};
      Location location = new Location(newLoc);
      // "get" works and recognizes possible exceptions
      BroadbandData result = cache.get(location);
      // For debugging and demo (would remove in a "real" version):
      System.out.println(cache.stats());
      return result;
  }

}

