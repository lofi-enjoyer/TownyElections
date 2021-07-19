package com.aurgiyalgo.TownyElections.commands.party;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.commands.SubCommand;
import com.aurgiyalgo.TownyElections.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static com.aurgiyalgo.TownyElections.TownyElections.getMessage;

public class PartyPromoteSubCommand extends SubCommand {

    public PartyPromoteSubCommand() {
        super("promote", 2);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Party party;

        switch (args[0].toLowerCase()) {

            case "town": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_PROMOTE)) return true;
                party = partyManager.getPlayerTownParty(player.getUniqueId());
            } break;

            case "nation": {
                if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_PROMOTE)) return true;
                party = partyManager.getPlayerNationParty(player.getUniqueId());
            } break;

            default:
                return false;

        }

        if (party == null) {
            player.sendMessage(getMessage("not-in-a-party"));
            return true;
        }

        if (!party.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(getMessage("not-leader"));
            return true;
        }

        if (!party.getMembers().contains(player.getUniqueId())) {
            player.sendMessage(getMessage("not-a-member"));
            return true;
        }

        OfflinePlayer promotedPlayer = Bukkit.getOfflinePlayer(args[1]);
        if (party.isAssistant(promotedPlayer.getUniqueId())) {
            player.sendMessage(getMessage("already-assistant"));
            return true;
        }
        party.addAssistant(promotedPlayer.getUniqueId());

        player.sendMessage(getMessage("player-promoted"));

        if (promotedPlayer.isOnline())
            promotedPlayer.getPlayer().sendMessage(getMessage("you-were-promoted"));

        return true;
    }

}
