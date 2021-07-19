package com.aurgiyalgo.TownyElections.commands.party;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.commands.SubCommand;
import com.aurgiyalgo.TownyElections.parties.Party;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

import static com.aurgiyalgo.TownyElections.TownyElections.getMessage;

public class PartyAcceptSubCommand extends SubCommand {

    public PartyAcceptSubCommand() {
        super("accept", 2);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        Party party;

        switch (args[0].toLowerCase()) {

            case "town": {

                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_ACCEPT)) return true;

                try {
                    party = partyManager.getPartiesForTown(resident.getTown().getName()).stream().filter(obj -> obj.getName().toLowerCase().equals(args[1])).collect(Collectors.toList()).get(0);
                } catch (Exception e) {
                    player.sendMessage(getMessage("not-in-a-nation"));
                    return true;
                }

            } break;

            case "nation": {

                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_ACCEPT)) return true;

                try {
                    party = partyManager.getPartiesForNation(resident.getTown().getNation().getName()).stream().filter(obj -> obj.getName().toLowerCase().equals(args[1])).collect(Collectors.toList()).get(0);
                } catch (Exception e) {
                    player.sendMessage(getMessage("not-in-a-nation"));
                    return true;
                }

            } break;

            default:
                return false;

        }

        if (party == null) {
            player.sendMessage(ChatColor.RED + "That party does not exist!");
            return true;
        }
        if (!party.isInvited(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not invited to that party");
            return true;
        }

        party.addMember(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You joined the party!");

        for (Player iteratedPlayer : Bukkit.getOnlinePlayers()) {
            if (party.getMembers().contains(iteratedPlayer.getUniqueId()))
                iteratedPlayer.sendMessage(getMessage("player-joined-the-party").replace("%player%", player.getName()));
        }

        return true;
    }

}
