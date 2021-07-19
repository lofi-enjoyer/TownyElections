package com.aurgiyalgo.TownyElections.commands.party;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.commands.SubCommand;
import com.aurgiyalgo.TownyElections.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.aurgiyalgo.TownyElections.TownyElections.getMessage;

public class PartySetLeaderSubCommand extends SubCommand {

    public PartySetLeaderSubCommand() {
        super("setleader", 2);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Party party;

        switch (args[0].toLowerCase()) {

            case "town": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_SETLEADER)) return true;
                party = partyManager.getPlayerTownParty(player.getUniqueId());
            } break;

            case "nation": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_SETLEADER)) return true;
                party = partyManager.getPlayerNationParty(player.getUniqueId());
            } break;

            default:
                return false;

        }

        if (party == null) {
            player.sendMessage(getMessage("not-in-a-party"));
            return true;
        }

        if (!party.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(getMessage("not-leader"));
            return true;
        }

        Player newLeader = Bukkit.getPlayer(args[1]);
        if (newLeader == null) {
            player.sendMessage(getMessage("player-not-online"));
            return true;
        }

        party.setLeader(newLeader.getUniqueId());

        return true;
    }

}
