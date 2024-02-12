package APIHandlers;

import Broadband.BroadbandData;

public class ACSProxy {
 ACSDataSource source;

  public ACSProxy() {
    this.source = new ACSDataSource();
  }
  public BroadbandData getBroadbandData(int state, int county) {
    return null;
  }
//    LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()
//        .maximumSize(1000)
//        .build(
//            new CacheLoader<Key, Graph>() {
//              public Graph load(Key key) throws AnyException {
//                return createExpensiveGraph(key);
//              }
//            });
//
//...
//    try {
//      return graphs.get(key);
//    } catch (ExecutionException e) {
//      throw new OtherException(e.getCause());
//    }

  }


}
