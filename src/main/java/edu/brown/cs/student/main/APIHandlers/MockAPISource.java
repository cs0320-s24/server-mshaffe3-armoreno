package APIHandlers;

import APIHandlers.Broadband.BroadbandData;
import APIHandlers.Exceptions.DatasourceException;

import java.util.concurrent.ExecutionException;

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
