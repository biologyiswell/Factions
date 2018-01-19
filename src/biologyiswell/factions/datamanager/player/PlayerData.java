package biologyiswell.factions.datamanager.player;

import biologyiswell.factions.datamanager.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerData {

    private final String name;

    private String faction;
    private float power, powerMax;
    private int kills, deaths;

    private long firstLogin, lastLogin;

    public PlayerData(String name) {
        this.name = name;
        this.firstLogin = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();

        // Update the all default configuration
        this.power = Data.CONFIG.defaultPower;
        this.powerMax = Data.CONFIG.defaultPowerMax;
    }

    /** This method get the KDR from player */
    public float getKDR() {
        try {
            return (float) this.kills / (float) (this.deaths == 0 ? 1 : this.deaths);
        } catch (Exception e) {
            return 0f;
        }
    }

    public boolean hasFaction() {
        return this.faction != null && !this.faction.isEmpty();
    }

    public String getTranslatedFaction(boolean tag) {
        if (hasFaction()) return tag ? this.faction + " [" + Data.FACTIONS.getFactionBy(this.faction, false).getTag() + "]" : this.faction;
        else return "Sem Facção";
    }

    /** This method save the player data to database */
    public void saveToDatabase() {
        Data.PLAYERS.saveData(this, Data.PLAYERS.getPlayerDataFile(this.name), false);
    }
}
