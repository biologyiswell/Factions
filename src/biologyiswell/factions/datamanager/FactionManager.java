package biologyiswell.factions.datamanager;

import biologyiswell.factions.FactionsPlugin;
import biologyiswell.factions.datamanager.faction.Faction;
import com.google.common.io.Files;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class FactionManager extends Manager {

    private final ArrayList<Faction> LIST = new ArrayList<>();
    private final Map<String, List<String>> INVITES = new HashMap<>();
    private final File factionsDatabase;

    // package-private
    FactionManager() {
        this.factionsDatabase = new File(FactionsPlugin.INSTANCE.getDataFolder(), "factions");
    }

    @Override
    protected void initData() {
        if (!this.factionsDatabase.exists() && this.factionsDatabase.mkdir()) FactionsPlugin.LOGGER.info("Factions FactionsData has been created.");
        this.loadAllFactions();
    }

    @Override
    protected void deInitData() {
        this.saveAllFactions();
    }

    // public-methods

    /** This method register the faction in database and in faction list */
    public void registerFaction(Faction faction) {
        createFactionData(faction);
        LIST.add(faction);
    }

    /** This method unregister the faction from database and from faction list */
    public void unregisterFaction(Faction faction) {
        // NOTE: Remove the faction from list
        LIST.remove(faction);

        // NOTE: This method force the delete from file
        try {
            FileDeleteStrategy.FORCE.delete(getFactionFile(faction.getName()));
        } catch (IOException e) {
            FactionsPlugin.LOGGER.severe(String.format("Ocorreu um erro ao deletar o arquivo da facção %s.", faction.getName()));
            e.printStackTrace();
        }
    }

    public Faction getFactionBy(String id, boolean tag) {
        if (tag) return LIST.stream().filter(faction -> faction.getTag().equalsIgnoreCase(id)).findFirst().orElse(null);
        else return LIST.stream().filter(faction -> faction.getName().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    // private-methods

    /** This method load the all factions from database */
    private void loadAllFactions() {
        // NOTE: This is to check the time that the load all factions from database
        long start = System.currentTimeMillis();

        // NOTE: List the all factions database files
        for (File file : Objects.requireNonNull(this.factionsDatabase.listFiles())) {
            // Load the faction data and add the faction in list
            Faction faction = loadFactionData(file);
            LIST.add(faction);

            if (FactionsPlugin.DEBUG) FactionsPlugin.LOGGER.info(String.format("A facção %s foi carregada com sucesso.", faction.getName()));
        }

        if (FactionsPlugin.DEBUG) FactionsPlugin.LOGGER.info(String.format("Todas as facções foram carregadas com sucesso (Total: %s, Time: %sms.).", LIST.size(), System.currentTimeMillis() - start));
    }

    /** This method save the all factions to database */
    private void saveAllFactions() {
        // NOTE: This is to check the time that the save all factions save in database
        long start = System.currentTimeMillis();

        LIST.forEach(faction -> saveFactionData(faction, false));
        LIST.clear();

        if (FactionsPlugin.DEBUG) FactionsPlugin.LOGGER.info(String.format("Todas as facções foram salvas com sucesso (Total: %s, Time: %sms.).", LIST.size(), System.currentTimeMillis() - start));
    }

    private void createFactionData(Faction faction) {
        saveFactionData(faction, true);
    }

    private void saveFactionData(Faction faction, boolean registry) {
        try {
            Files.write(GSON.toJson(faction), getFactionFile(faction.getName()), Charsets.UTF_8);
        } catch (IOException e) {
            FactionsPlugin.LOGGER.severe(String.format("Ocorreu um erro ao %s a facção %s na database.", registry ? "criar" : "salvar", faction.getName()));
            e.printStackTrace();
        }
    }

    private Faction loadFactionData(File file) {
        try {
            return file.exists() ? GSON.fromJson(Files.newReader(file, Charsets.UTF_8), Faction.class) : null;
        } catch (FileNotFoundException e) {
            FactionsPlugin.LOGGER.severe(String.format("Ocorreu um erro ao carregar a facção %s da database.", factionsDatabase.getName()));
            e.printStackTrace();
        }
        return null;
    }

    private Faction loadFactionData(String factionName) {
        return loadFactionData(getFactionFile(factionName));
    }

    private File getFactionFile(String factionName) {
        return new File(factionsDatabase, factionName + ".json");
    }

    // invite-methods
    // public-methods

    /** This method add invite to player from faction */
    public boolean addInvite(String factionName, String to) {
        if (!INVITES.containsKey(to)) INVITES.put(to, new ArrayList<>());
        if (INVITES.get(to).contains(to)) return false;

        INVITES.get(to).add(factionName);

        Bukkit.getScheduler().runTaskLater(FactionsPlugin.INSTANCE, () -> {
            if (!INVITES.containsKey(to)) return;
            INVITES.get(to).remove(factionName);

            // NOTE: This method check if the factions invite is size 0
            if (INVITES.get(to).size() == 0) INVITES.remove(to);
        }, (20 * 60) * Data.CONFIG.timeToAcceptInvite);
        return true;
    }

    /** This method remove the invite from faction to player */
    public void removeInvite(String factionName, String from) {
        if (hasInvite(factionName, from)) {
            INVITES.get(from).remove(factionName);
            if (INVITES.get(from).size() == 0) INVITES.remove(from);
        }
    }

    /** This method check if the player has invite from faction */
    public boolean hasInvite(String factionName, String from) {
        return INVITES.containsKey(from) && INVITES.get(from).contains(factionName);
    }
}
