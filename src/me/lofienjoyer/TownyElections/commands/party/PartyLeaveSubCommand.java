package me.lofienjoyer.TownyElections.commands.party;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.SubCommand;
import me.lofienjoyer.TownyElections.parties.Party;
import org.bukkit.entity.Player;

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
            player.sendMessage(TownyElections.getMessage("not-in-a-party"));
            return true;
        }

        if (party.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("leader-cannot-leave"));
            return true;
        }

        party.removeMember(player.getUniqueId());
        return true;
    }

}
