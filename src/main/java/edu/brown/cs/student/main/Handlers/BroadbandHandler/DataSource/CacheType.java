package Handlers.BroadbandHandler.DataSource;

/**
 * This enum allows the developer to specify what type of caching they want
 *
 */
public enum CacheType {
  MAX_SIZE, //eviction based on a max size
  TIME, //based on amount of time
  NO_LIMIT //unlimited cache
}
