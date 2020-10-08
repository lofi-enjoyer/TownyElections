package com.aurgiyalgo.TownyElections.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.elections.NationDecision;
import com.aurgiyalgo.TownyElections.elections.NationElection;
import com.aurgiyalgo.TownyElections.elections.TownDecision;
import com.aurgiyalgo.TownyElections.elections.TownElection;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;

public class TEListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (e.getPlayer().hasPermission("townyelections.vote.town")) {
			TownElection election;
			election = TownyElections.getTownElection(e.getPlayer());
			if (election == null) return;
			e.getPlayer().sendTitle(" ", TownyElections.getTranslatedMessage("active-election"), 20, 60, 20);
		}
	}
	
	@EventHandler
	public void onPlayerTownLeave(TownRemoveResidentEvent e) {
		Player player = Bukkit.getPlayer(e.getResident().getName());
		
		TownElection townElection = TownyElections.getTownElection(Bukkit.getPlayer(e.getResident().getName()));
		if (townElection != null) {
			TownyElections.getInstance().getPartyManager().getPlayerTownParty(player.getUniqueId()).removeMember(player.getUniqueId());
			townElection.removeVote(player.getUniqueId());
		}
		
		TownDecision townDecision = TownyElections.getTownDecision(player);
		if (townDecision != null) {
			townDecision.removeVote(player.getUniqueId());
		}
		
		NationElection nationElection = TownyElections.getNationElection(player);
		if (nationElection != null) {
			nationElection.removeCandidate(player.getUniqueId());
			nationElection.removeVote(player.getUniqueId());
		}
		
		NationDecision nationDecision = TownyElections.getNationDecision(player);
		if (nationDecision != null) {
			nationDecision.removeVote(player.getUniqueId());
		}
	}

}
