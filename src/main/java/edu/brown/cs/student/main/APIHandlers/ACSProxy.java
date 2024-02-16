package APIHandlers;

import APIHandlers.Exceptions.DatasourceException;
import APIHandlers.Broadband.BroadbandData;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The Proxy class serves as a wrapper class for the datasource, a mediator between the handler and
 * API calls, and a place for caching to occur.
 */
public class ACSProxy implements APISource {
  //Datasource to be wrapped
  private final APISource source;
  //cache to store results
  private LoadingCache<Location, BroadbandData> cache;
  //determines what eviction policy cache should have
  private final CacheType type;

  /**
   * This constructor takes in a CacheType and numerical value to go with it, and makes a
   * new cache and DataSource.
   * @param myType
   * @param typeAmount
   * @throws DatasourceException
   */
  public ACSProxy(APISource dataSource, CacheType myType, int typeAmount) throws DatasourceException{
    this.source = dataSource;
    this.type = myType;
    this.makeCache(typeAmount);
  }

  /**
   * This copy constructor takes in no caching parameters and can be used if the developer
   * wishes for no results to be cached.
   * @throws DatasourceException
   */
  public ACSProxy(APISource dataSource) throws DatasourceException {
    this.source = dataSource;
    this.type = null;
  }

  /**
   * This helper method creates a cache based on the parameters passed into Proxy.
   * @param typeValue
   */
  private void makeCache(int typeValue){
    CacheLoader<Location, BroadbandData> loader =
        new CacheLoader<>() {

          @Override
          public BroadbandData load(Location location) throws DatasourceException, ExecutionException {
            return makeRequest(location.loc);
          }
        };

    switch (this.type) {
      //delete results after access
      case TIME -> this.cache =
          CacheBuilder.newBuilder()
              .expireAfterAccess(typeValue, TimeUnit.MINUTES)
              .recordStats()
              .build(loader);
      //delete results after max size
      case MAX_SIZE -> this.cache =
          CacheBuilder.newBuilder().maximumSize(typeValue).recordStats().build(loader);
      //never delete results
      case NO_LIMIT -> this.cache = CacheBuilder.newBuilder().recordStats().build(loader);
    }
  }

  /**
   * Called by cache if there isn't already a result for this location
   *
   * @param loc - county and state in string format
   * @return - result of search by user
   * @throws DatasourceException - If accessing Datasource is a problem
   * @throws IOException
   */
  private BroadbandData makeRequest(String[] loc) throws DatasourceException, ExecutionException {
    return this.source.getBroadbandData(loc);
  }

  /**
   * Method makes a Location object of the inputted location and checks whether it is in the cache
   * to return before making a new call to getBroadbandData
   *
   * @param loc - county and state user is searching for in String array format
   * @return - user result from cache or call from getBroadbandData
   * @throws ExecutionException - if getBroadbandData throws an error
   */
  @Override
  public BroadbandData getBroadbandData(String[] loc) throws DatasourceException, ExecutionException {
    String[] newLoc = new String[] {loc[0].toLowerCase(Locale.US), loc[1].toLowerCase(Locale.US)};

    if (this.type != null) {
      Location location = new Location(newLoc);
      // "get" works and recognizes possible exceptions
      BroadbandData result = cache.get(location);
      // For debugging and demo (would remove in a "real" version):
      //System.out.println(cache.stats());
      return result;
    }
    //if not in cache already
    return makeRequest(newLoc);
  }
}
