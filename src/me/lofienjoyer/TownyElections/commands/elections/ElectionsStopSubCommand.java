package me.lofienjoyer.TownyElections.commands.elections;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.SubCommand;
import me.lofienjoyer.TownyElections.elections.NationElection;
import me.lofienjoyer.TownyElections.elections.TownElection;
import me.lofienjoyer.TownyElections.gui.NationStopGui;
import me.lofienjoyer.TownyElections.gui.TownStopGui;
import org.bukkit.entity.Player;

public class ElectionsStopSubCommand extends SubCommand {

    public ElectionsStopSubCommand() {
        super("stop", 1);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        switch (args[0].toLowerCase()) {

            case "town": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWN_STOP)) return true;

                TownElection election = TownyElections.getInstance().getElectionManager().getTownElection(player);
                if (election == null) {
                    player.sendMessage(TownyElections.getMessage("not-active-election"));
                    return true;
                }
                TownStopGui.INVENTORY.open(player);
            } break;

            case "nation": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATION_STOP)) return true;

                NationElection election = TownyElections.getInstance().getElectionManager().getNationElection(player);
                if (election == null) {
                    player.sendMessage(TownyElections.getMessage("not-active-election-nation"));
                    return true;
                }
                NationStopGui.INVENTORY.open(player);
            } break;

            default:
                return false;

        }
        return false;
    }

}
