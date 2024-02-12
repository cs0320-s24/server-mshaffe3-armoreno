package APIHandlers;

import Broadband.BroadbandData;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.Collection;

public class ACSProxy implements APISource{

  private final ACSDataSource source;
  private final LoadingCache<String[], BroadbandData> cache;


  public ACSProxy() {
    this.source = new ACSDataSource();

    this.cache = CacheBuilder.newBuilder().build(
        new CacheLoader<>() {
          @Override
          public BroadbandData load(String[] loc) throws Exception {
            return source.getBroadbandData(loc);
          }
        } //TODO figure out how to catch the exception from the cache
    );
  }

  @Override
  public BroadbandData getBroadbandData(String[] loc) {
    // "get" is designed for concurrent situations; for today, use getUnchecked:
    BroadbandData result = cache.getUnchecked(loc);
    // For debugging and demo (would remove in a "real" version):
    System.out.println(cache.stats());
    return result;
  }
}

