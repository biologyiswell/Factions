package biologyiswell.factions.datamanager;

import biologyiswell.factions.util.json.LocationAdapter;
import biologyiswell.factions.util.json.NoSerialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;

public class Manager
{

    protected final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .setExclusionStrategies(new NoSerialize.NoSerializeAdapter())
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .create();

    /**
     * This method is added to the Data handle the initialization from data
     */
    protected void initData()
    {
    }

    /**
     * This method is added to the Data handle the de-initialization from data
     */
    protected void deInitData()
    {
    }
}