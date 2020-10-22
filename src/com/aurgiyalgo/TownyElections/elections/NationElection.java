package com.aurgiyalgo.TownyElections.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.TownyElections.MutableInteger;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;

public class NationElection extends Election {

	private Nation nation;

	public NationElection(Nation nation, long endTime) {
		super(endTime);
		this.nation = nation;
	}

	public void setup() {
		try {
			nation = TownyUniverse.getInstance().getDataSource().getNation(territoryUuid);
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public String finishElection() {
		if (votes.isEmpty()) {
			TownyElections.sendNationMessage(nation, TownyElections.getTranslatedMessage("no-winner"));
			return null;
		}

		Map<String, MutableInteger> voteCount = new HashMap<String, MutableInteger>();

		for (Map.Entry<UUID, String> entry : votes.entrySet()) {
			if (!voteCount.containsKey(entry.getValue())) {
				voteCount.put(entry.getValue(), new MutableInteger(1));
				continue;
			}
			voteCount.get(entry.getValue()).value++;
		}

		Map.Entry<String, MutableInteger> maxCandidate = null;

		for (Map.Entry<String, MutableInteger> entry : voteCount.entrySet()) {
			if (maxCandidate == null) {
				maxCandidate = entry;
				continue;
			}
			if (maxCandidate.getValue().value == entry.getValue().value) {
				TownyElections.sendNationMessage(nation, TownyElections.getTranslatedMessage("no-winner"));
				return null;
			}
			if (maxCandidate.getValue().value < entry.getValue().value) {
				maxCandidate = entry;
				continue;
			}
		}

		winner = maxCandidate.getKey();

		try {
			nation.setCapital(TownyUniverse.getInstance().getDataSource()
					.getResident(Bukkit.getOfflinePlayer(winner).getName()).getTown());
			TownyUniverse.getInstance().getDataSource().saveNation(nation);
			String msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%player%",
					Bukkit.getOfflinePlayer(winner).getName());
			TownyElections.sendNationSubtitle(nation, msg);
			Player p = Bukkit.getPlayer(winner);
			if (p != null) {
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			}

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
