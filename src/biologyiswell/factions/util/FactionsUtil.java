package biologyiswell.factions.util;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionsUtil {

    /** This method send messages to sender */
    public static void sendMessages(CommandSender sender, String... messages) {
        for (String message : messages) sender.sendMessage(message);
    }

    /** This method refresh the player */
    public static void refreshPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20f);
        player.setFoodLevel(20);
        player.setExhaustion(0f);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    /** This method translate the seconds to string */
    public static String translateSec(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;

        if (sec >= 60) {
            min += 1;
            sec = 0;
        }

        return String.format("%sm%ss", min, sec);
    }
}
