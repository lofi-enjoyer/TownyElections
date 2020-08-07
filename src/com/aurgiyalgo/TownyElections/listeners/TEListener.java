package com.aurgiyalgo.TownyElections.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.elections.NationDecision;
import com.aurgiyalgo.TownyElections.elections.NationElection;
import com.aurgiyalgo.TownyElections.elections.TownDecision;
import com.aurgiyalgo.TownyElections.elections.TownElection;
import com.aurgiyalgo.TownyElections.revolutions.Revolution;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

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
			townElection.getCandidates().remove(player.getUniqueId());
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
	
	@EventHandler
	public void onPlayerKill(EntityDamageByEntityEvent e) {
		if (!TownyElections.areRevolutionsEnabled()) return;
		if (!(e.getEntity() instanceof Player) || !(e.getEntity() instanceof Player)) return;
		
		Player victimPlayer = (Player) e.getEntity();
		if (victimPlayer.getHealth() > 0 ) return;
		
		Player attackerPlayer = (Player) e.getDamager();
		Revolution revolution = TownyElections.getRevolution(attackerPlayer);
		if (revolution == null) return;
		
		try {
			Resident victimResident = TownyUniverse.getInstance().getDataSource().getResident(victimPlayer.getName());
			Town victimTown = victimResident.getTown();
			if (victimTown == null) return;
			if (revolution.getTown() != victimTown) return;
			if (!victimResident.hasTownRank("assistant") && !(victimTown.getMayor() == victimResident)) return;
			revolution.addKilledStaff(victimPlayer.getUniqueId());
			int staffCount = revolution.getTown().getAssistants().size() + 1;
			int count = 0;
			
			for (Resident res : victimTown.getResidents()) {
				UUID resUUID = Bukkit.getOfflinePlayer(res.getName()).getUniqueId();
				if (!revolution.getKilledStaff().contains(resUUID)) continue;
				if (!victimResident.hasTownRank("assistant") && !victimResident.hasTownRank("mayor")) continue;
				count++;
			}
			
			if (count >= staffCount) TownyElections.successRevolution(revolution);
		} catch (NotRegisteredException e1) {
			return;
		}
	}

}
