package me.lofienjoyer.TownyElections.commands.party;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.SubCommand;
import me.lofienjoyer.TownyElections.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
            player.sendMessage(TownyElections.getMessage("not-in-a-party"));
            return true;
        }

        if (!party.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("not-leader"));
            return true;
        }

        if (!party.getMembers().contains(player.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("not-a-member"));
            return true;
        }

        OfflinePlayer promotedPlayer = Bukkit.getOfflinePlayer(args[1]);
        if (party.isAssistant(promotedPlayer.getUniqueId())) {
            player.sendMessage(TownyElections.getMessage("already-assistant"));
            return true;
        }
        party.addAssistant(promotedPlayer.getUniqueId());

        player.sendMessage(TownyElections.getMessage("player-promoted"));

        if (promotedPlayer.isOnline())
            promotedPlayer.getPlayer().sendMessage(TownyElections.getMessage("you-were-promoted"));

        return true;
    }

}
