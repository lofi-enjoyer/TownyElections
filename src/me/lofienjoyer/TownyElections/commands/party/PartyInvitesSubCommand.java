package me.lofienjoyer.TownyElections.commands.party;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.SubCommand;
import me.lofienjoyer.TownyElections.parties.Party;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyInvitesSubCommand extends SubCommand {

    public PartyInvitesSubCommand() {
        super("invites", 1);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());

        List<String> parties = new ArrayList<>();

        switch (args[0].toLowerCase()) {

            case "town": {

                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_INVITES)) return true;

                try {
                    for (Party p : partyManager.getPartiesForTown(resident.getTown().getName()))
                        if (p.isInvited(player.getUniqueId())) parties.add(p.getName());
                } catch (NotRegisteredException e) {
                    player.sendMessage(TownyElections.getMessage("not-in-a-town"));
                    return true;
                }

            } break;

            case "nation": {

                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_INVITES)) return true;

                try {
                    for (Party p : partyManager.getPartiesForNation(resident.getTown().getNation().getName()))
                        if (p.isInvited(player.getUniqueId())) parties.add(p.getName());
                } catch (NotRegisteredException e) {
                    player.sendMessage(TownyElections.getMessage("not-in-a-nation"));
                    return true;
                }

            } break;

            default:
                return false;

        }

        if (parties.size() <= 0) {
            player.sendMessage(TownyElections.getMessage("no-invitations"));
            return true;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(TownyElections.getMessage("invitations"));
        builder.append(parties.get(0));

        for (int i = 1; i < parties.size(); i++) {
            builder.append(", ");
            builder.append(parties.get(i));
        }

        player.sendMessage(builder.toString());

        return true;
    }

}
