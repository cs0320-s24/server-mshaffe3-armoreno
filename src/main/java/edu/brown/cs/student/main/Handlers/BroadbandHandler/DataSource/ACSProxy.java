package Handlers.BroadbandHandler.DataSource;

import Handlers.Broadband.BroadbandData;
import Handlers.Exceptions.DatasourceException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The Proxy class serves as a wrapper class for the datasource, a mediator between the handler and
 * API calls, and a place for caching to occur.
 */
public class ACSProxy implements APISource {

  // Datasource to be wrapped
  private final APISource source;
  // cache to store results
  private LoadingCache<Location, BroadbandData> cache;
  // determines what eviction policy cache should have
  private final CacheType type;

  /**
   * This constructor takes in a CacheType and numerical value to go with it, and makes a new cache
   * and DataSource.
   *
   * @param myType type of cache
   * @param typeAmount amount of time or size of cache for eviction policy
   * @throws DatasourceException from ACSDatasource
   */
  public ACSProxy(APISource dataSource, CacheType myType, int typeAmount)
      throws DatasourceException {
    this.source = dataSource;
    this.type = myType;
    this.makeCache(typeAmount);
  }

  /**
   * This copy constructor takes in no caching parameters and can be used if the developer wishes
   * for no results to be cached.
   *
   * @throws DatasourceException if ACS Datasource runs into issues with the API
   */
  public ACSProxy(APISource dataSource) throws DatasourceException {
    this.source = dataSource;
    this.type = null;
  }

  /**
   * This helper method creates a cache based on the parameters passed into Proxy.
   *
   * @param typeValue the type of cache to make
   */
  private void makeCache(int typeValue) {
    CacheLoader<Location, BroadbandData> loader =
        new CacheLoader<>() {

          @Override
          public BroadbandData load(Location location)
              throws DatasourceException, ExecutionException {
            return makeRequest(location.loc);
          }
        };

    switch (this.type) {
        // delete results after access
      case TIME -> this.cache =
          CacheBuilder.newBuilder()
              .expireAfterAccess(typeValue, TimeUnit.SECONDS)
              .recordStats()
              .build(loader);
        // delete results after max size
      case MAX_SIZE -> this.cache =
          CacheBuilder.newBuilder().maximumSize(typeValue).recordStats().build(loader);
        // never delete results
      case NO_LIMIT -> this.cache = CacheBuilder.newBuilder().recordStats().build(loader);
      case NONE -> this.cache = null;
    }
  }

  /**
   * Called by cache if there isn't already a result for this location
   *
   * @param loc - county and state in string format
   * @return - result of search by user
   * @throws DatasourceException - If accessing Datasource is a problem
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
  public BroadbandData getBroadbandData(String[] loc)
      throws DatasourceException, ExecutionException {
    String[] newLoc = new String[] {loc[0].toLowerCase(Locale.US), loc[1].toLowerCase(Locale.US)};

    if (this.type != CacheType.NONE) {
      Location location = new Location(newLoc);
      // "get" works and recognizes possible exceptions
      BroadbandData result = cache.get(location);
      return result;
    }
    // if not in cache already
    return makeRequest(newLoc);
  }

  /**
   * function that returns the caches status for testing purposes
   *
   * @return null if no cache is used and a caches stats if it is
   */
  public com.google.common.cache.CacheStats getStats() {
    if (this.cache == null) {
      return null;
    }
    return cache.stats();
  }

  public ConcurrentMap<Location, BroadbandData> getMap() {
    if (this.cache == null) {
      return null;
    }
    return cache.asMap();
  }
}
