
import Handlers.Broadband.BroadbandData;
import Handlers.BroadbandHandler.DataSource.ACSDataSource;
import Handlers.BroadbandHandler.DataSource.ACSProxy;
import Handlers.BroadbandHandler.DataSource.CacheType;
import Handlers.BroadbandHandler.DataSource.Location;
import Handlers.Exceptions.DatasourceException;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class TestCache {

    @Test
    public void noCacheSuccess() throws DatasourceException, ExecutionException {

        ACSProxy acsProxy = new ACSProxy(new ACSDataSource(), CacheType.NONE, 0);

        //make sure cache is null
        assertNull(acsProxy.getStats());

        //can still make requests to API
        BroadbandData testData = acsProxy.getBroadbandData(new String[]{"kentucky", "Hardin County"});

        assertEquals("success", testData.result());
        assertEquals("87.2", testData.percentage().percentage());
        assertEquals("hardin county", testData.county());
        assertEquals("kentucky", testData.state());

    }

    @Test
    public void cacheMaxSizeSuccess() throws DatasourceException, ExecutionException {

        ACSProxy acsProxy = new ACSProxy(new ACSDataSource(), CacheType.MAX_SIZE, 1);

        //make a call
        BroadbandData testData = acsProxy.getBroadbandData(new String[]{"kentucky", "Hardin County"});

        assertEquals("success", testData.result());
        assertEquals("87.2", testData.percentage().percentage());
        assertEquals("hardin county", testData.county());
        assertEquals("kentucky", testData.state());

        //item exists in cache
        assertNotNull(acsProxy.getMap().get(new Location(new String[]{"kentucky", "hardin county"})));

        //call the same thing again
        testData = acsProxy.getBroadbandData(new String[]{"kentucky", "Hardin County"});

        assertEquals("success", testData.result());
        assertEquals("87.2", testData.percentage().percentage());
        assertEquals("hardin county", testData.county());
        assertEquals("kentucky", testData.state());

        //cache's hitCount should have gone up by one
        com.google.common.cache.CacheStats cacheStats = acsProxy.getStats();
        assertEquals(cacheStats.hitCount(), 1);
        assertEquals(cacheStats.loadCount(), 1);
        assertEquals(cacheStats.missCount(), 1);

        //make a different call
        testData = acsProxy.getBroadbandData(new String[]{"california", "kings county"});

        assertEquals("success", testData.result());
        assertEquals("83.5", testData.percentage().percentage());
        assertEquals("kings county", testData.county());
        assertEquals("california", testData.state());


        //previous item no longer exists in cache
        assertNull(acsProxy.getMap().get(new Location(new String[]{"kentucky", "hardin county"})));
        //new item exists in cache now
        assertNotNull(acsProxy.getMap().get(new Location(new String[]{"california", "kings county"})));
    }

    @Test
    public void cacheNoLimitTest() throws DatasourceException, ExecutionException {

        ACSProxy acsProxy = new ACSProxy(new ACSDataSource(), CacheType.MAX_SIZE, 1);

        //make sure cache is not null and empty before any call
        com.google.common.cache.CacheStats cacheStats = acsProxy.getStats();
        assertEquals(cacheStats.hitCount(), 0);
        assertEquals(cacheStats.loadCount(), 0);
        assertEquals(cacheStats.missCount(), 0);

        //make a call
        BroadbandData testData = acsProxy.getBroadbandData(new String[]{"kentucky", "Hardin County"});

        assertEquals("success", testData.result());
        assertEquals("87.2", testData.percentage().percentage());
        assertEquals("hardin county", testData.county());
        assertEquals("kentucky", testData.state());

        //cache should have loaded a search
        cacheStats = acsProxy.getStats();
        assertEquals(cacheStats.hitCount(), 0);
        assertEquals(cacheStats.loadCount(), 1);
        assertEquals(cacheStats.missCount(), 1);

        //call the same thing again
        testData = acsProxy.getBroadbandData(new String[]{"kentucky", "Hardin County"});

        assertEquals("success", testData.result());
        assertEquals("87.2", testData.percentage().percentage());
        assertEquals("hardin county", testData.county());
        assertEquals("kentucky", testData.state());

        //cache's hitCount should have gone up by one
        cacheStats = acsProxy.getStats();
        assertEquals(cacheStats.hitCount(), 1);
        assertEquals(cacheStats.loadCount(), 1);
        assertEquals(cacheStats.missCount(), 1);

    }

    @Test
    public void cacheTimeLimitTest() throws DatasourceException, ExecutionException {

        ACSProxy acsProxy = new ACSProxy(new ACSDataSource(), CacheType.TIME, 1);

        //make sure cache is not null and empty before any call
        com.google.common.cache.CacheStats cacheStats = acsProxy.getStats();
        assertEquals(cacheStats.hitCount(), 0);
        assertEquals(cacheStats.loadCount(), 0);
        assertEquals(cacheStats.missCount(), 0);

        //make a call
        BroadbandData testData = acsProxy.getBroadbandData(new String[]{"kentucky", "Hardin County"});

        assertEquals("success", testData.result());
        assertEquals("87.2", testData.percentage().percentage());
        assertEquals("hardin county", testData.county());
        assertEquals("kentucky", testData.state());

        //cache should have loaded a search
        cacheStats = acsProxy.getStats();
        assertEquals(cacheStats.hitCount(), 0);
        assertEquals(cacheStats.loadCount(), 1);
        assertEquals(cacheStats.missCount(), 1);

        //call the same thing again
        testData = acsProxy.getBroadbandData(new String[]{"kentucky", "Hardin County"});

        assertEquals("success", testData.result());
        assertEquals("87.2", testData.percentage().percentage());
        assertEquals("hardin county", testData.county());
        assertEquals("kentucky", testData.state());

        //cache's hitCount should have gone up by one
        cacheStats = acsProxy.getStats();
        assertEquals(cacheStats.hitCount(), 1);
        assertEquals(cacheStats.loadCount(), 1);
        assertEquals(cacheStats.missCount(), 1);

    }

}
