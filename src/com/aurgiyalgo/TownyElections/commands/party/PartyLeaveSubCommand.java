package com.aurgiyalgo.TownyElections.commands.party;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.commands.SubCommand;
import com.aurgiyalgo.TownyElections.parties.Party;
import org.bukkit.entity.Player;

import static com.aurgiyalgo.TownyElections.TownyElections.getMessage;

public class PartyLeaveSubCommand extends SubCommand {

    public PartyLeaveSubCommand() {
        super("leave", 1);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Party party;

        switch (args[0].toLowerCase()) {

            case "town": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_LEAVE)) return true;
                party = partyManager.getPlayerTownParty(player.getUniqueId());
            } break;

            case "nation": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_LEAVE)) return true;
                party = partyManager.getPlayerNationParty(player.getUniqueId());
            } break;

            default:
                return false;

        }

        if (party == null) {
            player.sendMessage(getMessage("not-in-a-party"));
            return true;
        }

        if (party.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(getMessage("leader-cannot-leave"));
            return true;
        }

        party.removeMember(player.getUniqueId());
        return true;
    }

}
