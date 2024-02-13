package APIHandlers;

import Broadband.BroadbandData;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public class ACSProxy implements APISource{

  private final ACSDataSource source;
  private LoadingCache<String[], BroadbandData> cache;


  public ACSProxy() throws DatasourceException, IOException {
    this.source = new ACSDataSource();

    makeCache();
  }

  private void makeCache(){
      CacheLoader<String[], BroadbandData> loader = new CacheLoader<String[], BroadbandData>() {
          @Override
          public BroadbandData load(String[] loc) throws Exception {
              return getBroadbandData(loc);
          }
      };

      this.cache = CacheBuilder.newBuilder().maximumSize(1000).build(loader);
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

