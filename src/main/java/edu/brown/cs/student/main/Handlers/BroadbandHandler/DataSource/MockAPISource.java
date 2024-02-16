package Handlers.BroadbandHandler.DataSource;

import Handlers.Broadband.BroadbandData;

<<<<<<< HEAD
/** */
=======
/** This class is used for testing the Broadband Handler without querying the actual ACS API */
>>>>>>> 31d448b4c4cb74e86b585f0abb4a875d0b09a7e2
public class MockAPISource implements APISource {

  BroadbandData myData;

  public MockAPISource(BroadbandData data) {
    this.myData = data;
  }

  @Override
  public BroadbandData getBroadbandData(String[] loc) {
    return this.myData;
  }
}
