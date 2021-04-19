package com.aurgiyalgo.TownyElections.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.parties.NationParty;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;

public class NationElection extends Election {

	private Nation nation;

	public NationElection(Nation nation, long endTime) {
		super(endTime);
		territoryUuid = nation.getUuid();
		setup();
	}

	public void setup() {
		try {
			nation = TownyUniverse.getInstance().getDataSource().getNation(territoryUuid);
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
	}

	public String finishElection() {
		if (votes.isEmpty()) {
			TownyElections.sendNationMessage(nation, TownyElections.getInstance().getLanguageData().getString("no-winner"));
			return null;
		}

		Map<String, AtomicInteger> voteCount = new HashMap<String, AtomicInteger>();

		for (Map.Entry<UUID, String> entry : votes.entrySet()) {
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
				TownyElections.sendNationMessage(nation, TownyElections.getInstance().getLanguageData().getString("no-winner"));
				return null;
			}
			if (maxCandidate.getValue().get() < entry.getValue().get()) {
				maxCandidate = entry;
				continue;
			}
		}

		winner = maxCandidate.getKey();

		try {
			NationParty party = TownyElections.getInstance().getPartyManager().getPartiesForNation(nation.getName()).stream().filter(t -> t.getName().toLowerCase().equals(winner.toLowerCase())).collect(Collectors.toList()).get(0);
			nation.setCapital(TownyUniverse.getInstance().getDataSource()
					.getResident(Bukkit.getOfflinePlayer(party.getLeader()).getName()).getTown());
			TownyUniverse.getInstance().getDataSource().saveNation(nation);
			String msg = TownyElections.getInstance().getLanguageData().getString("election-won").replace("%party%", party.getName());
			TownyElections.sendNationSubtitle(nation, msg);

		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		return winner;
	}

	public void addVote(UUID player, String candidate) {
		if (TownyElections.getInstance().getPartyManager().getPartiesForNation(nation.getName()).stream()
				.filter(party -> party.getName().equals(candidate)).collect(Collectors.toList()).isEmpty())
			return;
		if (votes.containsKey(player))
			return;
		votes.put(player, candidate);
	}

	public void removeVote(UUID player) {
		if (votes.containsKey(player))
			votes.remove(player);
	}

	public Nation getNation() {
		return nation;
	}

}
