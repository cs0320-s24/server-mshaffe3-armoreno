package Handlers.BroadbandHandler.DataSource;

import Handlers.Broadband.BroadbandData;

/** This class is used for testing the Broadband Handler without querying the actual ACS API */
public class MockAPISource implements APISource {

  BroadbandData myData;

  public MockAPISource(BroadbandData data) {
    this.myData = data;
  }

  /**
   * acts as a mock call to getBroadbandData source
   *
   * @param loc - whatever location inputted
   * @return - returns data inputted on instantiation of class
   */
  @Override
  public BroadbandData getBroadbandData(String[] loc) {
    return this.myData;
  }
}
