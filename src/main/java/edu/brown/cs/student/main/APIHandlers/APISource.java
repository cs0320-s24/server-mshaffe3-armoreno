package APIHandlers;

import APIHandlers.Exceptions.DatasourceException;
import Broadband.BroadbandData;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface APISource {
  BroadbandData getBroadbandData(String[] loc)
      throws IOException, DatasourceException, ExecutionException;
}
