package me.lofienjoyer.TownyElections.commands.elections;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.SubCommand;
import me.lofienjoyer.TownyElections.elections.Election;
import me.lofienjoyer.TownyElections.elections.NationElection;
import me.lofienjoyer.TownyElections.elections.TownElection;
import me.lofienjoyer.TownyElections.gui.NationVoteGui;
import me.lofienjoyer.TownyElections.gui.TownVoteGui;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ElectionsVoteSubCommand extends SubCommand {

    public ElectionsVoteSubCommand() {
        super("vote", 1);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        System.out.println(Arrays.toString(args));
        Election election;

        switch (args[0].toLowerCase()) {

            case "town": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWN_VOTE)) return true;
                election = electionManager.getTownElection(player);

                if (doChecks(player, election)) return true;

                if (System.currentTimeMillis() >= election.getEndTime()) {
                    electionManager.removeTownElection((TownElection) election);
                    return true;
                }
                TownVoteGui.INVENTORY.open(player);
            } break;

            case "nation": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATION_VOTE)) return true;
                election = electionManager.getNationElection(player);

                if (doChecks(player, election)) return true;

                if (System.currentTimeMillis() >= election.getEndTime()) {
                    electionManager.removeNationElection((NationElection) election);
                    return true;
                }
                NationVoteGui.INVENTORY.open(player);
            } break;

            default:
                return false;

        }
        return true;
    }
    
    private boolean doChecks(Player player, Election election) {
        if (election == null) {
            player.sendMessage(TownyElections.getMessage("not-active-election"));
            return true;
        }
        if (election.hasVoted(player.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("already-voted"));
            return true;
        }
        return false;
    }

}
