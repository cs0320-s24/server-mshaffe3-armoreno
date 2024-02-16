import Handlers.Broadband.Broadband;
import Handlers.BroadbandHandler.BroadbandHandler;
import Handlers.BroadbandHandler.DataSource.ACSDataSource;
import Handlers.BroadbandHandler.DataSource.CacheType;
import Handlers.Exceptions.DatasourceException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestCache {
    @BeforeAll
    public static void setup_before_everything() {
        // Set the Spark port number.
        Spark.port(0);

        // Remove the logging spam during tests
        Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
    }

    @AfterEach
    public void teardown() {
        // Gracefully stop Spark listening on both endpoints after each test
        Spark.unmap("broadband");
        Spark.awaitStop(); // don't proceed until the server is stopped
    }

    private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    private JsonAdapter<Map<String, String>> responseAdapter;
    private JsonAdapter<Broadband> broadbandAdapter;

    private String[] loc;

    @Test
    public void noCacheSuccess() throws DatasourceException {
        // In fact, restart the entire Spark server for every test!
        Spark.get("broadband", new BroadbandHandler(new ACSDataSource(), CacheType.MAX_SIZE, 1000));
        Spark.init();
        Spark.awaitInitialization(); // don't continue until the server is listening

        Moshi moshi = new Moshi.Builder().build();
        responseAdapter = moshi.adapter(mapStringObject);
        broadbandAdapter = moshi.adapter(Broadband.class);

        loc = new String[]{"Hardin+County", "Kentucky"};


    }


}
