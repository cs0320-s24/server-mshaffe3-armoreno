package APIHandlers;

import APIHandlers.Broadband.BroadbandData;
import Exceptions.DatasourceException;
import java.util.concurrent.ExecutionException;

/**
 * Simple interface implemented by the ACSDataSource and the ACSProxy, which is a wrapper class of
 * ACSDataSource
 */
public interface APISource {
  BroadbandData getBroadbandData(String[] loc) throws DatasourceException, ExecutionException;
}
