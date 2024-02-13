package APIHandlers;

import Broadband.BroadbandData;
import java.io.IOException;

public interface APISource {
  BroadbandData getBroadbandData(String[] loc) throws IOException, DatasourceException;

}
