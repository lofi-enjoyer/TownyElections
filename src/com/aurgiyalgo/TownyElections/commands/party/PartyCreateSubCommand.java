package com.aurgiyalgo.TownyElections.commands.party;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.commands.SubCommand;
import com.aurgiyalgo.TownyElections.parties.NationParty;
import com.aurgiyalgo.TownyElections.parties.Party;
import com.aurgiyalgo.TownyElections.parties.TownParty;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

import static com.aurgiyalgo.TownyElections.TownyElections.getMessage;

public class PartyCreateSubCommand extends SubCommand {

    public PartyCreateSubCommand() {
        super("create", 2);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Party party;

        switch (args[0].toLowerCase()) {
            case "town": {

                if (!player.hasPermission(TownyElections.Permissions.TOWNPARTY_CREATE)) {
                    player.sendMessage(getMessage("no-permission"));
                    return true;
                }

                party = partyManager.getPlayerTownParty(player.getUniqueId());
                if (party != null) {
                    player.sendMessage(getMessage("already-part-of-a-party"));
                    return true;
                }

                Town town;
                try {
                    town = TownyUniverse.getInstance().getResident(player.getName()).getTown();
                } catch (NotRegisteredException e) {
                    player.sendMessage(TownyElections.getMessage("not-in-a-town"));
                    return true;
                }

                if (partyManager.getPartiesForTown(town.getName()).stream().filter(townParty -> townParty.getName().toLowerCase().equals(args[2].toLowerCase())).collect(Collectors.toList()).size() > 0) {
                    player.sendMessage(TownyElections.getMessage("name-taken"));
                    return true;
                }

                party = new TownParty(args[1], player.getUniqueId(), town.getUUID());
            } break;

            case "nation": {

                if (!player.hasPermission(TownyElections.Permissions.NATIONPARTY_CREATE)) {
                    player.sendMessage(getMessage("no-permission"));
                    return true;
                }

                party = partyManager.getPlayerNationParty(player.getUniqueId());
                if (party != null) {
                    player.sendMessage(getMessage("already-in-a-party"));
                    return true;
                }

                Nation nation;
                try {
                    nation = TownyUniverse.getInstance().getResident(player.getName()).getTown().getNation();
                } catch (NotRegisteredException e) {
                    player.sendMessage(getMessage("not-in-a-nation"));
                    return true;
                }

                if (partyManager.getPartiesForNation(nation.getName()).stream().anyMatch(nationParty -> nationParty.getName().toLowerCase().equals(args[2].toLowerCase()))) {
                    player.sendMessage(getMessage("name-taken"));
                    return true;
                }

                party = new NationParty(args[1], player.getUniqueId(), nation.getUUID());
            } break;

            default:
                return false;

        }

        partyManager.addParty(party);
        player.sendMessage(TownyElections.getMessage("party-created"));
        return true;
    }

}
