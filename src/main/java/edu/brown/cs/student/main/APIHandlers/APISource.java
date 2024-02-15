package APIHandlers;

import APIHandlers.Exceptions.DatasourceException;
import Broadband.BroadbandData;
import java.util.concurrent.ExecutionException;

public interface APISource {
  BroadbandData getBroadbandData(String[] loc)
      throws DatasourceException, ExecutionException;
}
