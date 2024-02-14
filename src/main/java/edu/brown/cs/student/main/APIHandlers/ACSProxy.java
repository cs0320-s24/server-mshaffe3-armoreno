package APIHandlers;

import Broadband.BroadbandData;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
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

  public BroadbandData makeRequest(String[] loc) throws DatasourceException, IOException {
      return this.source.getBroadbandData(loc);
  }

  @Override
  public BroadbandData getBroadbandData(String[] loc) throws ExecutionException {
      Location location = new Location(loc);
    // "get" works and recognizes possible exceptions
    BroadbandData result = cache.get(location);
    // For debugging and demo (would remove in a "real" version):
    System.out.println(cache.stats());
    return result;
  }

}

