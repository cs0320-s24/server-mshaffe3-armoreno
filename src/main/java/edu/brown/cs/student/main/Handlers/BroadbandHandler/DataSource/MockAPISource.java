package Handlers.BroadbandHandler.DataSource;

import Handlers.Broadband.BroadbandData;

/**
 *
 */
public class MockAPISource implements APISource {

    BroadbandData myData;
    public MockAPISource(BroadbandData data){
        this.myData = data;
    }
    @Override
    public BroadbandData getBroadbandData(String[] loc) {
        return this.myData;
    }


}
