package APIHandlers;

import APIHandlers.Exceptions.CacheException;
import APIHandlers.Exceptions.DatasourceException;
import Broadband.BroadbandData;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ACSProxy implements APISource {

  private final ACSDataSource source;
  private LoadingCache<Location, BroadbandData> cache;

  private final CacheType type;

  public ACSProxy(CacheType myType, int typeAmount) throws DatasourceException, CacheException {
    this.source = new ACSDataSource();
    this.type = myType;
    this.makeCache(typeAmount);
  }

  public ACSProxy() throws DatasourceException {
    this.source = new ACSDataSource();
    this.type = null;
  }

  private void makeCache(int typeValue) throws CacheException {
    CacheLoader<Location, BroadbandData> loader =
        new CacheLoader<>() {

          @Override
          public BroadbandData load(Location location) throws DatasourceException {
            return makeRequest(location.loc);
          }
        };

    switch (this.type) {
      case TIME -> this.cache =
          CacheBuilder.newBuilder()
              .expireAfterAccess(typeValue, TimeUnit.MINUTES)
              .recordStats()
              .build(loader);
      case MAX_SIZE -> this.cache =
          CacheBuilder.newBuilder().maximumSize(typeValue).recordStats().build(loader);
      case NO_LIMIT -> this.cache = CacheBuilder.newBuilder().recordStats().build(loader);
      default -> throw new CacheException("Cache type not available");
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
  private BroadbandData makeRequest(String[] loc) throws DatasourceException {
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
      throws ExecutionException, DatasourceException {
    String[] newLoc = new String[] {loc[0].toLowerCase(Locale.US), loc[1].toLowerCase(Locale.US)};

    if (this.type != null) {
      Location location = new Location(newLoc);
      // "get" works and recognizes possible exceptions
      BroadbandData result = cache.get(location);
      // For debugging and demo (would remove in a "real" version):
      System.out.println(cache.stats());
      return result;
    }

    return makeRequest(newLoc);
  }
}
