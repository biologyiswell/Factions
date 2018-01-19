package biologyiswell.factions.command;

import biologyiswell.factions.datamanager.Data;
import biologyiswell.factions.datamanager.chunk.ChunkData;
import biologyiswell.factions.datamanager.faction.Faction;
import biologyiswell.factions.datamanager.faction.Role;
import biologyiswell.factions.datamanager.player.PlayerData;
import biologyiswell.factions.util.FactionsUtil;
import biologyiswell.factions.util.TextComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

public class FactionsCommandFunction {

    // package-private
    protected FactionsCommandFunction() {
    }

    protected static void abandonChunk(Player player) {
        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        if (!playerData.hasFaction()) {
            player.sendMessage("§cOps, você não tem uma facção para tentar abandonar um terreno.");
            return;
        }

        Faction faction = Data.FACTIONS.getFactionBy(playerData.getFaction(), false);

        if (faction.getMembers().get(player.getName()).ordinal() > 1) {
            player.sendMessage("§cOps, somente o LÍDER e CAPITÃO podem abandonar terrenos.");
            return;
        }

        ChunkData currentChunk = Data.CHUNKS.getChunkData(player.getLocation().getChunk());

        // NOTE: This represents that the chunk is a free
        if (currentChunk == null) {
            player.sendMessage("§cOps, este terreno já é livre.");
            return;
        }

        if (!currentChunk.getFactionOwner().equals(faction.getName())) {
            player.sendMessage("§cOps, este terreno não pertence a sua facção.");
            return;
        }

        // NOTE: This method is important to remove the registry from chunk data
        Data.CHUNKS.removeChunkData(currentChunk);

        // NOTE: This method is important to update the chunks size
        faction.setChunksSize(faction.getChunksSize() - 1);

        player.sendMessage("§e ** Você abandonou um terreno **");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
    }

    protected static void protect(Player player, String[] args) {
        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        if (!playerData.hasFaction()) {
            player.sendMessage("§cOps, você não tem uma facção para tentar proteger um terreno.");
            return;
        }

        Faction faction = Data.FACTIONS.getFactionBy(playerData.getFaction(), false);

        if (faction.getMembers().get(player.getName()).ordinal() > 1) {
            player.sendMessage("§cOps, somente o LÍDER e CAPITÃO podem proteger terras.");
            return;
        }

        int maxChunksSize = faction.getMaxChunksSize();
        if (faction.getChunksSize() >= maxChunksSize) {
            player.sendMessage(String.format("§cOps, sua facção não tem PODER e TROFÉUS suficiente para poder proteger mais áreas. (máx: %s)", maxChunksSize));
            return;
        }

        // TODO Check if the player have money to protect chunk

        ChunkData currentChunk = Data.CHUNKS.getChunkData(player.getLocation().getChunk());

        // NOTE: This represents that the chunk is a free
        if (currentChunk == null) {
            currentChunk = new ChunkData(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld().getName(), faction.getName());
            // NOTE: This method is important to register the chunk data
            Data.CHUNKS.addChunkData(currentChunk);

            // NOTE: This method increase the chunks size
            faction.setChunksSize(faction.getChunksSize() + 1);

            player.sendMessage("§e ** Você protegeu um terreno **");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
        }
        // NOTE: This represents that the chunk is a protected chunk
        else {
            // NOTE: This represents that the chunk is owner by the current faction
            if (currentChunk.getFactionOwner().equals(faction.getName())) {
                player.sendMessage("§cOps, você já protegeu este terreno.");
                return;
            }
            // NOTE: This represents that this chunk is from other faction
            else {
                Faction targetFaction = Data.FACTIONS.getFactionBy(currentChunk.getFactionOwner(), false);

                if (targetFaction.getChunksSize() < targetFaction.getMaxChunksSize()) {
                    player.sendMessage("§cVocê não pode proteger o terreno dessa facção, pois eles estão com PODER e TROFÉUS suficiente.");
                    return;
                } else {
                    // NOTE: Update the faction owner
                    currentChunk.setFactionOwner(faction.getName());

                    // NOTE: Update from both factions the chunks size
                    faction.setChunksSize(faction.getChunksSize() + 1);
                    targetFaction.setChunksSize(targetFaction.getChunksSize() - 1);

                    faction.getOnlinePlayers().forEach(member -> member.sendMessage(String.format("§e ** O jogador %s roubou um terreno da facção %s **", player.getName(), targetFaction.getName())));
                    targetFaction.getOnlinePlayers().forEach(member -> member.sendMessage(String.format("§c§l ** O jogador %s roubou um terreno de sua facção **", player.getName())));
                }
            }
        }
    }

    protected static void kick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(commandArgumentErrorString("f expulsar <jogador>"));
            return;
        }

        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        if (!playerData.hasFaction()) {
            player.sendMessage("§cOps, você não tem uma facção para tentar expulsar alguém.");
            return;
        }

        Faction faction = Data.FACTIONS.getFactionBy(playerData.getFaction(), false);

        if (!faction.getLeader().equals(player.getName())) {
            player.sendMessage("§cOps, você precisa ser o LÍDER para expulsar alguém.");
            return;
        }

        String targetName = args[1];

        if (player.getName().equals(targetName)) {
            player.sendMessage("§cOps, você não pode expulsar a si memso.");
            return;
        }

        if (!faction.getMembers().containsKey(targetName)) {
            player.sendMessage("§cOps, esse jogador não faz parte da facção.");
            return;
        }

        faction.getMembers().remove(targetName);
        faction.getOnlinePlayers().forEach(member -> member.sendMessage(String.format("§e ** O jogador §f%s§e foi expulso da facção **", targetName)));

        // NOTE: This method is important to update the faction
        faction.update();

        Player targetPlayer = Bukkit.getPlayer(targetName);
        PlayerData targetPlayerData = Data.PLAYERS.getDynamicPlayerData(targetName);
        targetPlayerData.setFaction(null);

        if (targetPlayer != null) targetPlayer.sendMessage("§e ** Você foi expulso da facção **");
            // NOTE: This represents that the player from player data is offline then save the player data in database
        else targetPlayerData.saveToDatabase();
    }

    protected static void setLeader(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(commandArgumentErrorString("f definirlíder <jogador>"));
            return;
        }

        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        if (!playerData.hasFaction()) {
            player.sendMessage("§cOps, você não tem uma facção para tentar dar líder para alguém.");
            return;
        }

        Faction faction = Data.FACTIONS.getFactionBy(playerData.getFaction(), false);

        if (!faction.getLeader().equals(player.getName())) {
            player.sendMessage("§cOps, você precisa ser o LÍDER para definir LÍDER para outro jogador.");
            return;
        }

        String targetName = args[1];

        if (player.getName().equals(targetName)) {
            player.sendMessage("§cOps, você não pode definir líder a si memso.");
            return;
        }

        if (!faction.getMembers().containsKey(targetName)) {
            player.sendMessage("§cOps, esse jogador não faz parte da facção.");
            return;
        }

        faction.getMembers().put(player.getName(), Role.SOLDIER);
        faction.getMembers().put(targetName, Role.LEADER);
        faction.getOnlinePlayers().forEach(member ->
        {
            member.sendMessage(String.format("§e ** O jogador §f%s§e foi definido como LÍDER **", targetName));
            if (member == player)
                member.sendMessage(String.format("§e ** Você foi definido como %s **", faction.getMembers().get(player.getName()).getTranslatedName().toUpperCase()));
        });
    }

    protected static void demote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(commandArgumentErrorString("f rebaixar <jogador>"));
            return;
        }

        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        if (!playerData.hasFaction()) {
            player.sendMessage("§cOps, você não tem facção para tentar rebaixar alguém.");
            return;
        }

        Faction faction = Data.FACTIONS.getFactionBy(playerData.getFaction(), false);

        if (!faction.getLeader().equals(player.getName())) {
            player.sendMessage("§cOps, você precisa ser o LÍDER para rebaixar alguém.");
            return;
        }

        String targetName = args[1];

        if (player.getName().equals(targetName)) {
            player.sendMessage("§cOps, você não pode rebaixar a si memso.");
            return;
        }

        if (!faction.getMembers().containsKey(targetName)) {
            player.sendMessage("§cOps, esse jogador não faz parte da facção.");
            return;
        }

        if (faction.getMembers().get(targetName).ordinal() == Role.values().length - 1) {
            player.sendMessage("§cOps, esse jogador já está em sua demoção máxima.");
            return;
        }

        Role promotedRole = Role.values()[faction.getMembers().get(targetName).ordinal() + 1];
        Player targetPlayer = Bukkit.getPlayer(targetName);

        faction.getMembers().put(targetName, promotedRole);
        faction.getOnlinePlayers().forEach(member ->
        {
            if (member != targetPlayer)
                member.sendMessage(String.format("§e ** O jogador %s foi rebaixado para %s **", targetName, promotedRole.getTranslatedName()));
            else
                member.sendMessage(String.format("§e ** Você foi rebaixado para %s **", promotedRole.getTranslatedName()));
        });
    }

    protected static void promote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(commandArgumentErrorString("f promover <jogador>"));
            return;
        }

        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        if (!playerData.hasFaction()) {
            player.sendMessage("§cOps, você não tem facção para tentar promover alguém.");
            return;
        }

        Faction faction = Data.FACTIONS.getFactionBy(playerData.getFaction(), false);

        if (!faction.getLeader().equals(player.getName())) {
            player.sendMessage("§cOps, você precisa ser o LÍDER para promover alguém.");
            return;
        }

        String targetName = args[1];

        if (player.getName().equals(targetName)) {
            player.sendMessage("§cOps, você não pode promover a si memso.");
            return;
        }

        if (!faction.getMembers().containsKey(targetName)) {
            player.sendMessage("§cOps, esse jogador não faz parte da facção.");
            return;
        }

        if (faction.getMembers().get(targetName).ordinal() == 1) {
            player.sendMessage("§cOps, esse jogador já está em sua promoção máxima.");
            return;
        }

        Role promotedRole = Role.values()[faction.getMembers().get(targetName).ordinal() - 1];
        Player targetPlayer = Bukkit.getPlayer(targetName);

        faction.getMembers().put(targetName, promotedRole);
        faction.getOnlinePlayers().forEach(member ->
        {
            if (member != targetPlayer)
                member.sendMessage(String.format("§e ** O jogador %s foi promovido para %s **", targetName, promotedRole.getTranslatedName()));
            else
                member.sendMessage(String.format("§e ** Você foi promovido para %s **", promotedRole.getTranslatedName()));
        });
    }

    protected static void info(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(commandArgumentErrorString("f info <tag>"));
            return;
        }

        Faction faction = Data.FACTIONS.getFactionBy(args[1], true);

        if (faction == null) {
            player.sendMessage("§cOps, essa facção não foi encontrada.");
            return;
        }

        FactionsUtil.sendMessages(player,
                "",
                "    §eInformações de §f" + faction.getName() + " [" + faction.getTag() + "]§f.",
                "",
                "  Poder/Poder Máximo: §e" + faction.getPower() + "/" + faction.getPowerMax() + "§f.",
                "  Líder: §e" + faction.getLeader() + "§f.",
                "  Troféus: §6" + faction.getTrophies() + "§f.",
                "  Vítimas/Neutrais/Mortes: §a" + faction.getKills() + "§e/§7" + faction.getNeutrals() + "§e/§c" + faction.getDeaths() + "§f.",
                "  Terras: §e" + faction.getChunksSize() + "§f.",
                "  Membros: §e" + faction.getMembersWithIcon(false) + "§f.",
                "  Membros Online: §e" + faction.getMembersWithIcon(true) + "§f.",
                "  Criado em: §e" + Data.DATE_FORMAT.format(faction.getCreatedAt()),
                "");
    }

    protected static void profile(Player player, String[] args) {
        String targetName = args.length < 2 ? player.getName() : args[1];
        PlayerData targetPlayerData = Data.PLAYERS.getDynamicPlayerData(targetName);

        if (targetPlayerData == null) {
            player.sendMessage("§cOps, jogador não encontrado.");
            return;
        }

        float kdr = targetPlayerData.getKDR();

        FactionsUtil.sendMessages(player,
                "",
                "    §ePerfil de §f" + targetName + "§e.",
                "",
                "  Poder/Poder Máximo: §e" + targetPlayerData.getPower() + "/" + targetPlayerData.getPowerMax() + "§f.",
                "  Vítimas/Mortes: §a" + targetPlayerData.getKills() + "§e/§c" + targetPlayerData.getDeaths() + String.format("§f.               KDR: %s%s§f.", kdr < 1 ? "§c" : "§a", kdr),
                "  Facção: §e" + targetPlayerData.getTranslatedFaction(true) + "§f.",
                "  Primeiro Login: §e" + Data.DATE_FORMAT.format(targetPlayerData.getFirstLogin()),
                "  Ùltimo Login: §e" + Data.DATE_FORMAT.format(targetPlayerData.getLastLogin()),
                ""
        );
    }

    protected static void refuse(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(commandArgumentErrorString("f recusar <nome>"));
            return;
        }

        if (!Data.FACTIONS.hasInvite(args[1], player.getName())) {
            player.sendMessage("§cOps, você não recebeu nenhum convite dessa facção.");
            return;
        }

        // NOTE: This method is important to remove the registry from the invite from faction to player
        Data.FACTIONS.removeInvite(args[1], player.getName());

        player.sendMessage("§eVocê recusou o pedido de convite para entrar na facção.");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 0.25f);
    }

    protected static void accept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(commandArgumentErrorString("f aceitar <nome>"));
            return;
        }

        if (!Data.FACTIONS.hasInvite(args[1], player.getName())) {
            player.sendMessage("§cOps, você não recebeu nenhum convite dessa facção.");
            return;
        }

        // NOTE: This method not need check if the player has faction, because the invite method only can send invites
        // to players that not have faction

        // NOTE: This method is important to remove the registry from the invite from faction to player
        Data.FACTIONS.removeInvite(args[1], player.getName());

        Faction targetFaction = Data.FACTIONS.getFactionBy(args[1], false);
        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        // NOTE: This method is important to update the faction from player
        playerData.setFaction(targetFaction.getName());

        // NOTE: This method is important to update the faction
        targetFaction.update();

        targetFaction.getMembers().put(player.getName(), Role.SOLDIER);
        targetFaction.getOnlinePlayers().forEach(member -> member.sendMessage(String.format("§e ** O jogador %s entrou na facção **", player.getName())));
    }

    protected static void invite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(commandArgumentErrorString("f convidar <nome>"));
            return;
        }

        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        if (!playerData.hasFaction()) {
            player.sendMessage("§cOps, você não tem uma facção para tentar convidar alguém.");
            return;
        }

        Faction faction = Data.FACTIONS.getFactionBy(playerData.getFaction(), false);

        // NOTE: This represents that only the roles LEADER and CAPTAIN can be invite a player
        if (faction.getMembers().get(player.getName()).ordinal() > 1) {
            player.sendMessage("§cOps, apenas o LÍDER e CAPITÃO podem convidar alguém.");
            return;
        }

        if (player.getName().equals(args[1])) {
            player.sendMessage("§cOps, você não pode convidar a si memso.");
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);

        if (targetPlayer == null) {
            player.sendMessage("§cOps, jogador não encontrado.");
            return;
        }

        PlayerData targetPlayerData = Data.PLAYERS.getPlayerData(targetPlayer);

        if (targetPlayerData.hasFaction()) {
            player.sendMessage("§cOps, você só pode convidar jogadores que não tem facção.");
            return;
        }

        // NOTE: This method is important to registry the invite from faction
        boolean invite = Data.FACTIONS.addInvite(faction.getName(), targetPlayer.getName());

        // NOTE: This condition represents that the player is already invited by this faction
        if (!invite) {
            player.sendMessage("§cOps, você já enviou um convite para este jogador.");
            return;
        }

        TextComponent mainComponent = TextComponentBuilder.builder()
                .addText(String.format("§eA facção §f%s §econvidou você para ser um membro, você tem %s %s para aceitar o pedido.",
                        faction.getName(), Data.CONFIG.timeToAcceptInvite, Data.CONFIG.timeToAcceptInvite == 1 ? "minuto" : "minutos"))
                .addClickRunCommand(" §e§l[ACEITAR] ", "/f aceitar " + faction.getName())
                .addClickRunCommand("§c§l[RECUSAR]", "/f recusar " + faction.getName()).toTextComponent();

        player.sendMessage(String.format("§aVocê convidou o jogador §f%s§a para facção.", targetPlayer.getName()));
        targetPlayer.spigot().sendMessage(mainComponent);
    }

    /**
     * This method disband the faction
     */
    protected static void leave(Player player) {
        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        if (!playerData.hasFaction()) {
            player.sendMessage("§cOps, você não tem uma facção para tentar abandona-lá.");
            return;
        }

        Faction faction = Data.FACTIONS.getFactionBy(playerData.getFaction(), false);

        // process = 0, represents that the faction is deleted
        // process = 1, represents that a new leader is set,
        // process = 2, represents that a member leave
        byte process = -1;

        if (faction.getMembers().size() == 1) {
            // NOTE: This method remove the all chunks from faciton from chunks list
            Data.CHUNKS.removeAllChunksFromFaction(faction);

            // NOTE: This method delete the faction from database
            Data.FACTIONS.unregisterFaction(faction);
            process = 0;
        } else {
            // NOTE: This represents that the player that leave from faction is the leader, then need set a new leader
            // to faction
            if (faction.getLeader().equals(player.getName())) {
                Role[] promotedRoles = new Role[]{Role.CAPTAIN, Role.SARGEANT, Role.SOLDIER};
                for (Role role : promotedRoles) {
                    for (Map.Entry<String, Role> entry : faction.getMembers().entrySet()) {
                        if (entry.getValue() == role) {
                            faction.getMembers().put(entry.getKey(), Role.LEADER);
                            faction.getOnlinePlayers().forEach(member -> member.sendMessage(String.format("§e ** O líder deixou a facção, agora o novo líder é %s **", entry.getKey())));

                            process = 1;
                            break;
                        }
                    }
                }
            } else process = 2;

        }

        if (process != 0) {
            faction.getMembers().remove(player.getName());

            if (process == 2)
                faction.getOnlinePlayers().forEach(member -> member.sendMessage(String.format("§e ** O jogador %s deixou a facção **", player.getName())));
        }

        playerData.setFaction(null);

        player.sendMessage("§aVocê deixou a facção.");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
    }

    /**
     * This method create faction to player
     */
    protected static void createFaction(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(commandArgumentErrorString("f criar <nome> <tag>"));
            return;
        }

        String factionName = args[1], factionTag = args[2].toUpperCase();

        if (factionName.length() > Data.CONFIG.maxFactionNameLength) {
            player.sendMessage(String.format("§cOps, o nome da facção excedeu o número de caracteres permitidos. §8(máx: %s)", Data.CONFIG.maxFactionNameLength));
            return;
        } else if (factionTag.length() > Data.CONFIG.maxTagLength) {
            player.sendMessage(String.format("§cOps, a TAG da facção excedeu o número de caracteres permitidos. §8(máx: %s)", Data.CONFIG.maxTagLength));
            return;
        }

        PlayerData playerData = Data.PLAYERS.getPlayerData(player);

        if (playerData.hasFaction()) {
            player.sendMessage("§cOps, você já tem uma facção.");
            return;
        }

        if (Data.FACTIONS.getFactionBy(factionName, false) != null) {
            player.sendMessage("§cOps, já existe uma facção com este nome.");
            return;
        }

        if (Data.FACTIONS.getFactionBy(factionTag, true) != null) {
            player.sendMessage("§cOps, já existe uma facção com este nome.");
            return;
        }

        Faction faction = new Faction(factionName, factionTag, player.getName());
        Data.FACTIONS.registerFaction(faction);

        // NOTE: This method is important to update the faction from player data
        playerData.setFaction(faction.getName());

        player.sendMessage("§a ** Você criou uma facção **");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
    }

    /**
     * This method generate the command argument error string
     */
    private static String commandArgumentErrorString(String commandName) {
        return "Está faltando argumentos neste comando. Digite: /" + commandName + ".";
    }
}
