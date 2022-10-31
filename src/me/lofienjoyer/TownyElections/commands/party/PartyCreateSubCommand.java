package me.lofienjoyer.TownyElections.commands.party;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.SubCommand;
import me.lofienjoyer.TownyElections.parties.NationParty;
import me.lofienjoyer.TownyElections.parties.Party;
import me.lofienjoyer.TownyElections.parties.TownParty;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

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
                    player.sendMessage(TownyElections.getMessage("no-permission"));
                    return true;
                }

                party = partyManager.getPlayerTownParty(player.getUniqueId());
                if (party != null) {
                    player.sendMessage(TownyElections.getMessage("already-part-of-a-party"));
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
                    player.sendMessage(TownyElections.getMessage("no-permission"));
                    return true;
                }

                party = partyManager.getPlayerNationParty(player.getUniqueId());
                if (party != null) {
                    player.sendMessage(TownyElections.getMessage("already-in-a-party"));
                    return true;
                }

                Nation nation;
                try {
                    nation = TownyUniverse.getInstance().getResident(player.getName()).getTown().getNation();
                } catch (NotRegisteredException e) {
                    player.sendMessage(TownyElections.getMessage("not-in-a-nation"));
                    return true;
                }

                if (partyManager.getPartiesForNation(nation.getName()).stream().anyMatch(nationParty -> nationParty.getName().toLowerCase().equals(args[2].toLowerCase()))) {
                    player.sendMessage(TownyElections.getMessage("name-taken"));
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
