package biologyiswell.factions.datamanager;

import biologyiswell.factions.FactionsConfig;
import biologyiswell.factions.FactionsPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;

public class Data {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

    public static final PlayerManager PLAYERS = new PlayerManager();
    public static final FactionManager FACTIONS = new FactionManager();
    public static final ChunkManager CHUNKS = new ChunkManager();

    public static FactionsConfig CONFIG;

    public static void initData(JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
            FactionsPlugin.LOGGER.info("Factions Database has been created.");
        }

        // TODO Need be load the config from database
        CONFIG = new FactionsConfig();

        // NOTE: The method initData need be added to initialize the databases after the database folder is initialized
        PLAYERS.initData();
        FACTIONS.initData();
        CHUNKS.initData();
    }

    /**
     * This method de-initialize the database from the factions
     */
    public static void deInitData(JavaPlugin plugin) {
        PLAYERS.deInitData();
        FACTIONS.deInitData();
        CHUNKS.deInitData();
    }
}
