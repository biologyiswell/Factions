package biologyiswell.factions.datamanager.faction;

import biologyiswell.factions.datamanager.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
public class Faction {

    private final String name, tag;
    private Map<String, Role> members;

    private float power, powerMax;

    private int kills, neutrals, deaths;
    private int chunksSize;

    private short trophies;
    private short seasonStars;

    private long createdAt;

    public Faction(String name, String tag, String leader) {
        this.name = name;
        this.tag = tag;
        this.createdAt = System.currentTimeMillis();

        if (this.members == null) this.members = new HashMap<>();
        this.members.put(leader, Role.LEADER);
    }

    /** This method get the max chunks that the players can protect */
    public int getMaxChunksSize() {
        // NOTE: This represents a configuration to new factions that the first (maxChunksWithoutCheck) the faction
        // can be protected
        if (this.chunksSize < Data.CONFIG.maxChunksWithoutCheck) return Data.CONFIG.maxChunksWithoutCheck;

        float calc = 20f;
        return Data.CONFIG.maxChunksWithoutCheck + (int) ((this.power / calc) + ((float) this.trophies / (5 * calc)));
    }

    /**
     * This method get online players from faction
     */
    public List<Player> getOnlinePlayers() {
        return this.members.keySet().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void update() {
    }

    /** This method get the leader from faction */
    public String getLeader() {
        return this.getMembers().entrySet().stream().filter(entry -> entry.getValue() == Role.LEADER).findFirst().get().getKey();
    }

    /** This method get the members with icon */
    public String getMembersWithIcon(boolean online) {
        StringBuilder stringBuilder = new StringBuilder();

        if (online)
            getOnlinePlayers().forEach(member -> stringBuilder.append('[').append(members.get(member.getName()).getIcon()).append("] ").append(member.getName()).append(", "));
        else
            getMembers().forEach((key, value) -> stringBuilder.append('[').append(value.getIcon()).append("] ").append(key).append(", "));


        return stringBuilder.substring(0, stringBuilder.length() - 2);
    }
}
