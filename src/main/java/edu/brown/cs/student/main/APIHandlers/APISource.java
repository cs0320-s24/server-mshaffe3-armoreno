package APIHandlers;

import Broadband.BroadbandData;
import java.io.IOException;

public interface APISource {
  public BroadbandData getBroadbandData(String[] loc) throws IOException, DatasourceException;

}
