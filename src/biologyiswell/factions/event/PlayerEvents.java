package biologyiswell.factions.event;

import biologyiswell.factions.datamanager.Data;
import biologyiswell.factions.datamanager.faction.Faction;
import biologyiswell.factions.datamanager.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Data.PLAYERS.registerPlayerLocal(event.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Data.PLAYERS.unregisterPlayerLocal(event.getPlayer());
    }

    /** This method is called when entity attack other entity */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            PlayerData attacker = Data.PLAYERS.getPlayerData((Player) event.getDamager());

            if (attacker.hasFaction()) {
                PlayerData receiver = Data.PLAYERS.getPlayerData((Player) event.getEntity());

                if (receiver.hasFaction() && attacker.getFaction().equals(receiver.getFaction())) {
                    event.getDamager().sendMessage("§cVocê não pode atacar membros de sua facção.");
                    event.setCancelled(true);
                }
            }
        }
    }

    /** This method is called when player death */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        PlayerData killerData = Data.PLAYERS.getPlayerData(event.getEntity().getKiller());
        PlayerData killedData = Data.PLAYERS.getPlayerData(event.getEntity());

        if (killerData.hasFaction()) {
            Faction killerFaction = Data.FACTIONS.getFactionBy(killerData.getFaction(), false);
            killerFaction.getOnlinePlayers().forEach(p -> p.sendMessage(String.format("§f%s §7matou o jogador §f%s§7.", killerData.getName(), killedData.getName())));
        }

        if (killedData.hasFaction()) {
            Faction killedFaction = Data.FACTIONS.getFactionBy(killedData.getFaction(), false);
            killedFaction.getOnlinePlayers().forEach(p -> p.sendMessage(String.format("§f%s §7foi morto pelo jogador §f%s§7", killedData.getName(), killerData.getName())));
        }
    }
}
