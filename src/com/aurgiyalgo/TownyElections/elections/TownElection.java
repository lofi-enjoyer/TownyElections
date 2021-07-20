package com.aurgiyalgo.TownyElections.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.parties.TownParty;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;

public class TownElection extends Election {

	private Town town;
	
	public TownElection(long endTime, Town town) {
		super(endTime);
		territoryUuid = town.getUUID();
		setup();
	}

	@Override
	public void setup() {
		try {
			town = TownyUniverse.getInstance().getDataSource().getTown(territoryUuid);
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
	}
	
	public void addVote(UUID player, String candidate) {
		if (votes.containsKey(player))
			return;
		if (instance.getPartyManager().getPartiesForTown(town.getName()).stream()
				.noneMatch(party -> party.getName().equals(candidate)))
			return;
		votes.put(player, candidate);
	}
	
	public void removeVote(UUID player) {
		votes.remove(player);
	}
	
	public String finishElection() {
		if (votes.isEmpty()) {
			TownyElections.sendTownMessage(town, TownyElections.getMessage("no-winner"));
			return null;
		}
		
		Map<String, AtomicInteger> voteCount = new HashMap<>();
		
		for (Map.Entry<UUID, String> entry : votes.entrySet())  {
			if (!voteCount.containsKey(entry.getValue())) {
				voteCount.put(entry.getValue(), new AtomicInteger(1));
				continue;
			}
			voteCount.get(entry.getValue()).incrementAndGet();
		}
		
		Map.Entry<String, AtomicInteger> maxCandidate = null;
		
		for (Map.Entry<String, AtomicInteger> entry : voteCount.entrySet()) {
			if (maxCandidate == null) {
				maxCandidate = entry;
				continue;
			}
			if (maxCandidate.getValue().get() == entry.getValue().get()) {
				TownyElections.sendTownMessage(town, TownyElections.getMessage("no-winner"));
				return null;
			}
			if (maxCandidate.getValue().get() < entry.getValue().get()) {
				maxCandidate = entry;
			}
		}
		
		winner = maxCandidate.getKey();
		
		try {
			TownParty party = instance.getPartyManager().getPartiesForTown(town.getName()).stream().filter(pty -> pty.getName().toLowerCase().equals(winner.toLowerCase())).collect(Collectors.toList()).get(0);
			town.setMayor(TownyUniverse.getInstance().getDataSource().getResident(Bukkit.getOfflinePlayer(party.getLeader()).getName()));
			TownyUniverse.getInstance().getDataSource().saveTown(town);
			String msg = TownyElections.getMessage("election-won").replace("%party%", party.getName());
			TownyElections.sendTownSubtitle(town, msg);
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		return winner;
	}

	public Town getTown() {
		return town;
	}

}
