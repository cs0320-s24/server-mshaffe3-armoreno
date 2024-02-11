package edu.brown.cs.student.main.Broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class BroadbandUtilities {
    /**
     * Deserializes JSON from the BoredAPI into an Activity object.
     *
     * @param jsonBroadband
     * @return
     */
    public static Broadband deserializeActivity(String jsonBroadband) {
        try {
            // Initializes Moshi
            Moshi moshi = new Moshi.Builder().build();

            // Initializes an adapter to an Activity class then uses it to parse the JSON.
            JsonAdapter<Broadband> adapter = moshi.adapter(Broadband.class);

            return adapter.fromJson(jsonBroadband);
        }
        // Returns an empty activity... Probably not the best handling of this error case...
        // Notice an alternative error throwing case to the one done in OrderHandler. This catches
        // the error instead of pushing it up.
        catch (IOException e) {
            e.printStackTrace();
            return new Broadband();
        }
    }
}

