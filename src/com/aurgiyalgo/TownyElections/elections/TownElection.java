package com.aurgiyalgo.TownyElections.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.TownyElections.MutableInteger;
import com.aurgiyalgo.TownyElections.parties.TownParty;
import com.google.gson.annotations.Expose;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;

public class TownElection {
	
	@Expose
	private Map<UUID, String> votes;
	@Expose
	private long endTime;
	@Expose
	private UUID townUuid;
	private Town town;
	private String winner;
	
	public TownElection(long endTime, Town town) {
		this.endTime = endTime;
		this.town = town;
		
		votes = new HashMap<UUID, String>();
		townUuid = town.getUuid();
	}
	
	public void setup() {
		try {
			town = TownyUniverse.getInstance().getDataSource().getTown(townUuid);
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
	}
	
	public void addVote(UUID player, String candidate) {
		if (votes.containsKey(player)) return;
		for (TownParty party : TownyElections.getInstance().getPartyManager().getTownParties()) {
			if (!party.getName().equals(candidate)) continue;
			votes.put(player, candidate);
		}
	}
	
	public void removeVote(UUID player) {
		if (votes.containsKey(player)) {
			votes.remove(player);
		}
	}
	
	public String finishElection() {
		if (votes.isEmpty()) {
			TownyElections.sendTownMessage(town, TownyElections.getTranslatedMessage("no-winner"));
			return null;
		}
		
		Map<String, MutableInteger> voteCount = new HashMap<String, MutableInteger>();
		
		for (Map.Entry<UUID, String> entry : votes.entrySet())  {
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
				TownyElections.sendTownMessage(town, TownyElections.getTranslatedMessage("no-winner"));
				return null;
			}
			if (maxCandidate.getValue().value < entry.getValue().value) {
				maxCandidate = entry;
				continue;
			}
		}
		
		winner = maxCandidate.getKey();
		
		try {
			town.setMayor(TownyUniverse.getInstance().getDataSource().getResident(Bukkit.getOfflinePlayer(winner).getName()));
			TownyUniverse.getInstance().getDataSource().saveTown(town);
			String msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%player%", Bukkit.getOfflinePlayer(winner).getName());
			TownyElections.sendTownSubtitle(town, msg);
			Player p = Bukkit.getPlayer(winner);
			if (p != null) {
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			}
			
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		} catch (TownyException e) {
			e.printStackTrace();
		}
		return maxCandidate.getKey();
	}
	
	public boolean hasVoted(UUID player) {
		return votes.containsKey(player);
	}
	
	public int getVotesCount() {
		return votes.size();
	}
	
	public Map<UUID, String> getVotes() {
		return votes;
	}

	public long getEndTime() {
		return endTime;
	}

	public Town getTown() {
		return town;
	}
	
	public String getWinner() {
		return winner;
	}

}
