package me.lofienjoyer.TownyElections.commands.party;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.SubCommand;
import me.lofienjoyer.TownyElections.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PartyDemoteSubCommand extends SubCommand {

    public PartyDemoteSubCommand() {
        super("demote", 2);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Party party;

        switch (args[0].toLowerCase()) {

            case "town": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_DEMOTE)) return true;
                party = partyManager.getPlayerTownParty(player.getUniqueId());
            } break;

            case "nation": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_DEMOTE)) return true;
                party = partyManager.getPlayerNationParty(player.getUniqueId());
            } break;

            default:
                return false;

        }

        if (party == null) {
            player.sendMessage(TownyElections.getMessage("not-in-a-party"));
            return true;
        }

        if (!party.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("not-leader"));
            return true;
        }

        if (!party.getMembers().contains(player.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("not-a-member"));
            return true;
        }

        OfflinePlayer demotedPlayer = Bukkit.getOfflinePlayer(args[1]);
        if (party.getLeader().equals(demotedPlayer.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("cannot-demote-leader"));
            return true;
        }

        if (!party.isAssistant(demotedPlayer.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("not-assistant"));
            return true;
        }

        party.removeAssistant(demotedPlayer.getUniqueId());

        player.sendMessage(TownyElections.getMessage("player-was-demoted").replace("%player%", demotedPlayer.getName()));

        if (demotedPlayer.isOnline())
            demotedPlayer.getPlayer().sendMessage(TownyElections.getMessage("you-were-demoted"));

        return true;
    }

}
