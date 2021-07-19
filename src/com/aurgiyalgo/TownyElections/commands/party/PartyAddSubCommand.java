package com.aurgiyalgo.TownyElections.commands.party;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.commands.SubCommand;
import com.aurgiyalgo.TownyElections.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.aurgiyalgo.TownyElections.TownyElections.getMessage;

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
            player.sendMessage(getMessage("not-in-a-party"));
            return true;
        }

        if (!party.getLeader().equals(player.getUniqueId()) && !party.isAssistant(player.getUniqueId())) {
            player.sendMessage(getMessage("no-permission"));
            return true;
        }

        Player invitedPlayer = Bukkit.getPlayer(args[1]);
        if (invitedPlayer == null) {
            player.sendMessage(getMessage("player-not-online"));
            return true;
        }

        if (party.isInvited(invitedPlayer.getUniqueId())) {
            player.sendMessage(getMessage("already-invited"));
            return true;
        }

        party.addInvite(invitedPlayer.getUniqueId());
        player.sendMessage(getMessage("player-invited").replace("%player%", invitedPlayer.getName()));
        invitedPlayer.sendMessage(getMessage("invited-to-party").replace("%party%", party.getName()));

        return true;
    }

}
