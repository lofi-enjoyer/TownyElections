package com.aurgiyalgo.TownyElections.commands.elections;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.commands.SubCommand;
import com.aurgiyalgo.TownyElections.elections.Election;
import com.aurgiyalgo.TownyElections.elections.NationElection;
import com.aurgiyalgo.TownyElections.elections.TownElection;
import org.bukkit.entity.Player;

public class ElectionsUnvoteSubCommand extends SubCommand {

    public ElectionsUnvoteSubCommand() {
        super("unvote", 1);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Election election;

        switch (args[0].toLowerCase()) {

            case "town": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWN_UNVOTE)) return true;

                election = electionManager.getTownElection(player);
            } break;

            case "nation": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATION_UNVOTE)) return true;

                election = electionManager.getNationElection(player);
            } break;

            default:
                return false;

        }
        if (election == null) {
            player.sendMessage(TownyElections.getMessage("not-active-election"));
            return true;
        }
        election.removeVote(player.getUniqueId());
        player.sendMessage(TownyElections.getMessage("unvoted"));
        return true;
    }

}
