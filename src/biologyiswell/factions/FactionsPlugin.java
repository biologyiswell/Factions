package biologyiswell.factions;

import biologyiswell.factions.command.FactionsCommand;
import biologyiswell.factions.datamanager.Data;
import biologyiswell.factions.event.PlayerEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class FactionsPlugin extends JavaPlugin {

    /** This represents the logger that make the all registries from the plugin */
    public static final Logger LOGGER = Logger.getLogger("Factions");

    /** This represents if when the plugin loads is to show informations about all handlers initialization */
    public static final boolean DEBUG = true;

    public static FactionsPlugin INSTANCE;

    @Override
    public void onEnable() {
        synchronized (this) {
            INSTANCE = this;

            Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);

            // NOTE: This method initialize the all database handlers
            Data.initData(this);

            getCommand("faction").setExecutor(new FactionsCommand());

            LOGGER.info("Factions enabled with success.");
        }
    }

    @Override
    public void onDisable() {
        synchronized (this) {
            // NOTE: This method de-initialize the all database handlers
            Data.deInitData(this);

            LOGGER.info("Factons disabled with success.");
        }
    }
}
