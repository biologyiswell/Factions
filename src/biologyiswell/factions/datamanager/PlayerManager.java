package biologyiswell.factions.datamanager;

import biologyiswell.factions.FactionsPlugin;
import biologyiswell.factions.datamanager.player.PlayerData;
import com.google.common.io.Files;
import org.apache.commons.io.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class PlayerManager extends Manager {

    // NOTE: This represents the map that storage the all players database
    private final HashMap<Player, PlayerData> MAP;
    private final File playerDatabase;

    // package-private
    PlayerManager() {
        this.MAP = new HashMap<>();
        this.playerDatabase = new File(FactionsPlugin.INSTANCE.getDataFolder(), "players");
    }

    @Override
    protected void initData() {
        if (!this.playerDatabase.exists() && this.playerDatabase.mkdir()) FactionsPlugin.LOGGER.info("Factions PlayerData has been created.");
        updateAllPlayers();
    }

    @Override
    protected void deInitData() {
        saveAllPlayers();
    }

    // public-methods

    public void registerPlayerLocal(Player player) {
        File playerDataFile = getPlayerDataFile(player);
        PlayerData playerData = null;

        // NOTE: If player data file exists, load data
        if (playerDataFile.exists()) {
            playerData = loadData(playerDataFile);

            if (playerData == null) {
                FactionsPlugin.LOGGER.severe(String.format("Ocorreu um erro GRAVE ao carregar a database do jogador %s (File class: %s).", player.getName(), playerDataFile));
                return;
            }

            // Update the last login from player
            playerData.setLastLogin(System.currentTimeMillis());
        }
        // NOTE: If player data file not exists, create data
        else {
            playerData = createData(player, playerDataFile);
        }

        MAP.put(player, playerData);
    }

    public void unregisterPlayerLocal(Player player) {
        saveData(MAP.get(player), getPlayerDataFile(player), false);
        MAP.remove(player);
    }

    public PlayerData getPlayerData(Player player) {
        return MAP.get(player);
    }

    /** This method get the dynamic player data, check if the player is online then get the player data from map, otherwise load data */
    public PlayerData getDynamicPlayerData(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        return player != null ? MAP.get(player) : loadData(getPlayerDataFile(playerName));
    }

    // private-methods

    private void updateAllPlayers() {
        // NOTE: This is to check that time from method to update the all players that are in server
        long start = System.currentTimeMillis();

        Bukkit.getOnlinePlayers().forEach(this::registerPlayerLocal);

        if (FactionsPlugin.DEBUG) FactionsPlugin.LOGGER.info(String.format("Todos os jogadores foram atualizados com sucesso (Time: %sms).", System.currentTimeMillis() - start));
    }

    private void saveAllPlayers() {
        // NOTE: This is to check that time from method to save the all players in database
        long start = System.currentTimeMillis();

        MAP.values().forEach(playerData -> saveData(playerData, getPlayerDataFile(playerData.getName()), false));
        MAP.clear();

        if (FactionsPlugin.DEBUG) FactionsPlugin.LOGGER.info(String.format("Todos os jogadores foram salvos com sucesso (Time: %sms).", System.currentTimeMillis() - start));
    }

    private PlayerData createData(Player player, File file) {
        PlayerData playerData = new PlayerData(player.getName());
        saveData(playerData, file, true);
        return playerData;
    }

    public void saveData(PlayerData playerData, File file, boolean registry) {
        try {
            Files.write(GSON.toJson(playerData), file, Charsets.UTF_8);
        } catch (IOException e) {
            FactionsPlugin.LOGGER.severe(String.format("Ocorreu um erro ao %s a database do jogador %s.", registry ? "criar" : "salvar", playerData.getName()));
            e.printStackTrace();
        }
    }

    private PlayerData loadData(File file) {
        try {
            return file.exists() ? GSON.fromJson(Files.newReader(file, Charsets.UTF_8), PlayerData.class) : null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** This method get the player data file from database by player name */
    public File getPlayerDataFile(String name) {
        return new File(playerDatabase, name + ".json");
    }

    /** This method get the player data file from database by player */
    private File getPlayerDataFile(Player player) {
        return getPlayerDataFile(player.getName());
    }
}
