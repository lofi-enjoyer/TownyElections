package me.lofienjoyer.TownyElections.commands.elections;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.SubCommand;
import me.lofienjoyer.TownyElections.elections.NationElection;
import me.lofienjoyer.TownyElections.elections.TownElection;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ElectionsConvokeSubCommand extends SubCommand {

    public ElectionsConvokeSubCommand() {
        super("convoke", 2);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        long finishTime;
        try {
            finishTime = Integer.parseInt(args[1]) * 60 * 1000;
        } catch (Exception e) {
            player.sendMessage(TownyElections.getMessage("error-input-number"));
            return false;
        }

        switch (args[0].toLowerCase()) {

            case "town": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWN_CONVOKE)) return true;

                Town t;
                try {
                    t = TownyUniverse.getInstance().getResident(player.getName()).getTown();
                } catch (NotRegisteredException e) {
                    player.sendMessage(TownyElections.getMessage("not-in-a-town"));
                    return true;
                }

                if (electionManager.getTownElection(player) != null) {
                    player.sendMessage(TownyElections.getMessage("active-election"));
                    return true;
                }

                if (doChecks(player, args[1])) return true;

                TownElection e = new TownElection(finishTime + System.currentTimeMillis(), t);
                electionManager.addTownElection(e);
                TownyElections.sendTownSubtitle(t, TownyElections.getMessage("election-convoked"));
            } break;

            case "nation": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATION_CONVOKE)) return true;

                Nation n;
                try {
                    n = TownyUniverse.getInstance().getResident(player.getName()).getTown().getNation();
                } catch (NotRegisteredException e) {
                    player.sendMessage(TownyElections.getMessage("not-in-a-nation"));
                    return true;
                }

                if (electionManager.getNationElection(player) != null) {
                    player.sendMessage(TownyElections.getMessage("active-election"));
                }

                if (doChecks(player, args[1])) return true;

                NationElection e = new NationElection(n, finishTime + System.currentTimeMillis());
                electionManager.addNationElection(e);
                TownyElections.sendNationSubtitle(n, TownyElections.getMessage("election-convoked"));
            } break;

            default:
                return false;

        }
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
        player.sendMessage(TownyElections.getMessage("election-convoked").replaceAll("%time%", args[1]));
        return true;
    }

    private boolean doChecks(Player player, String time) {
        long finishTime = 0;
        try {
            finishTime = Integer.parseInt(time) * 60 * 1000;
        } catch (Exception e) {
            player.sendMessage(TownyElections.getMessage("error-input-string"));
            return true;
        }
        if (finishTime / (1000 * 60) < TownyElections.Configuration.MIN_DURATION) {
            player.sendMessage(TownyElections.getMessage("min-duration").replace("%min%", String.valueOf(TownyElections.Configuration.MIN_DURATION)));
            return true;
        }
        if (finishTime / (1000 * 60) > TownyElections.Configuration.MAX_DURATION) {
            player.sendMessage(TownyElections.getMessage("max-duration").replace("%max%", String.valueOf(TownyElections.Configuration.MAX_DURATION)));
            return true;
        }
        return false;
    }

}
