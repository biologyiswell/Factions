package biologyiswell.factions.command;

import biologyiswell.factions.util.FactionsUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // NOTE: Show all commands
        if (args.length == 0) {
            FactionsUtil.sendMessages(sender,
                    "",
                    "    §a§lFACÇÕES COMANDOS",
                    "",
                    "  /f abandonar §8- §7Abandonar terreno de facção.",
                    "  /f aceitar <nome> §8- §7Aceitar convite de facção.",
                    "  /f convidar <nome> §8- §7Convidar para facção",
                    "  /f criar <nome> <tag> §8- §7Criar uma facção.",
                    "  /f definirlíder <jogador> §8- §7Definir líder da facção.",
                    "  /f expulsar <jogador> §8- §7Expulsar jogador da facção.",
                    "  /f info <tag> §8- §7Visualizar perfil de facção.",
                    "  /f perfil <jogador> §8- §7Visualizar perfil de jogador.",
                    "  /f promover <jogador> §8- §7Promover cargo de jogador.",
                    "  /f proteger §8- §7Proteger área de facção.",
                    "  /f rebaixar <jogador> §8- §7Rebaixar cargo de jogador.",
                    "  /f recusar <nome> §8- §7Recusar convite de facção.",
                    "  /f sair §8- §7Deixar uma facção.",
                    "");
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSomente jogadores podem utilizar este comando.");
            return false;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("abandonar")) {
            FactionsCommandFunction.abandonChunk(player);
            return false;
        }

        if (args[0].equalsIgnoreCase("aceitar")) {
            FactionsCommandFunction.accept(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("convidar")) {
            FactionsCommandFunction.invite(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("criar")) {
            FactionsCommandFunction.createFaction(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("definirlíder")) {
            FactionsCommandFunction.setLeader(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("expulsar")) {
            FactionsCommandFunction.kick(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("info")) {
            FactionsCommandFunction.info(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("perfil")) {
            FactionsCommandFunction.profile(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("promover")) {
            FactionsCommandFunction.promote(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("proteger")) {
            FactionsCommandFunction.protect(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("rebaixar")) {
            FactionsCommandFunction.demote(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("recusar")) {
            FactionsCommandFunction.refuse(player, args);
            return false;
        }

        if (args[0].equalsIgnoreCase("sair")) {
            FactionsCommandFunction.leave(player);
            return false;
        }

        return false;
    }
}
