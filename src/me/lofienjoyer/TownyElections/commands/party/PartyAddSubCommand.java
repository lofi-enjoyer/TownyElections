package me.lofienjoyer.TownyElections.commands.party;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.SubCommand;
import me.lofienjoyer.TownyElections.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PartyAddSubCommand extends SubCommand {

    public PartyAddSubCommand() {
        super("add", 2);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Party party;

        switch (args[0].toLowerCase()) {

            case "town": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_ADD)) return true;
                party = partyManager.getPlayerTownParty(player.getUniqueId());
            } break;

            case "nation": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_ADD)) return true;
                party = partyManager.getPlayerNationParty(player.getUniqueId());
            } break;

            default:
                return false;

        }

        if (party == null) {
            player.sendMessage(TownyElections.getMessage("not-in-a-party"));
            return true;
        }

        if (!party.getLeader().equals(player.getUniqueId()) && !party.isAssistant(player.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("no-permission"));
            return true;
        }

        Player invitedPlayer = Bukkit.getPlayer(args[1]);
        if (invitedPlayer == null) {
            player.sendMessage(TownyElections.getMessage("player-not-online"));
            return true;
        }

        if (party.isInvited(invitedPlayer.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("already-invited"));
            return true;
        }

        party.addInvite(invitedPlayer.getUniqueId());
        player.sendMessage(TownyElections.getMessage("player-invited").replace("%player%", invitedPlayer.getName()));
        invitedPlayer.sendMessage(TownyElections.getMessage("invited-to-party").replace("%party%", party.getName()));

        return true;
    }

}
