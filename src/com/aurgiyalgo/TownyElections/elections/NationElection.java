package com.aurgiyalgo.TownyElections.elections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.TownyElections.MutableInteger;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;

public class NationElection {
	
	private List<UUID> candidates;
	private Map<UUID, UUID> votes;
	private long endTime;
	private Nation nation;
	private UUID winner;
	
	public NationElection(Nation nation, long endTime) {
		this.nation = nation;
		this.endTime = endTime;
		
		candidates = new ArrayList<UUID>();
		votes = new HashMap<UUID, UUID>();
	}
	
	public UUID finishElection() {
		if (votes.isEmpty()) {
			TownyElections.sendNationMessage(nation, TownyElections.getTranslatedMessage("no-winner"));
			return null;
		}
		
		Map<UUID, MutableInteger> voteCount = new HashMap<UUID, MutableInteger>();
		
		for (Map.Entry<UUID, UUID> entry : votes.entrySet())  {
			if (!voteCount.containsKey(entry.getValue())) {
				voteCount.put(entry.getValue(), new MutableInteger(1));
				System.out.println(entry.getValue() + " - " + voteCount.get(entry.getValue()));
				continue;
			}
			voteCount.get(entry.getValue()).value++;
		}
		
		Map.Entry<UUID, MutableInteger> maxCandidate = null;
		
		for (Map.Entry<UUID, MutableInteger> entry : voteCount.entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue());
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
			nation.setCapital(TownyUniverse.getInstance().getDataSource().getResident(Bukkit.getOfflinePlayer(winner).getName()).getTown());
			TownyUniverse.getInstance().getDataSource().saveNation(nation);
			String msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%player%", Bukkit.getOfflinePlayer(winner).getName());
			TownyElections.sendNationSubtitle(nation, msg);
			Player p = Bukkit.getPlayer(winner);
			if (p != null) {
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			}
			
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		return maxCandidate.getKey();
	}
	
	public void addCandidate(UUID candidate) {
		if (candidates.contains(candidate)) return;
		candidates.add(candidate);
	}
	
	public void removeCandidate(UUID candidate) {
		if (!candidates.contains(candidate)) return;
		candidates.remove(candidate);
		for (Map.Entry<UUID, UUID> entry : votes.entrySet()) {
			if (entry.getValue().equals(candidate)) {
				votes.remove(entry.getKey());
			}
		}
	}
	
	public void addVote(UUID player, UUID candidate) {
		if (!candidates.contains(candidate)) return;
		votes.put(player, candidate);
	}
	
	public void removeVote(UUID player) {
		if (votes.containsKey(player)) votes.remove(player);
	}
	
	public boolean hasVoted(UUID player) {
		return votes.containsKey(player);
	}
	
	public int getVotesCount() {
		return votes.size();
	}
	
	public Map<UUID, UUID> getVotes() {
		return votes;
	}

	public long getEndTime() {
		return endTime;
	}

	public Nation getNation() {
		return nation;
	}
	
	public List<UUID> getCandidates() {
		return candidates;
	}
	
	public UUID getWinner() {
		return winner;
	}

}
