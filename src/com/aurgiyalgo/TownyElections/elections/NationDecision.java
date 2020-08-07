package com.aurgiyalgo.TownyElections.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.TownyElections.MutableInteger;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;

public class NationDecision {

	public static final String PUBLIC = "PUBLIC";
	public static final String NEUTRAL = "NEUTRAL";
	public static final String OPEN = "OPEN";
	

	
	private Map<UUID, Boolean> votes;
	private Nation nation;
	private long endTime;
	private String type;
	private boolean winner;
	
	public NationDecision(Nation nation, long endTime, String type) {
		this.nation = nation;
		this.endTime = endTime;
		this.type = type;
		votes = new HashMap<UUID, Boolean>();
	}
	
	public boolean finishDecision() {
		if (votes.isEmpty()) {
			TownyElections.sendNationMessage(nation, TownyElections.getTranslatedMessage("no-winner"));
			return false;
		}
		
		Map<Boolean, MutableInteger> voteCount = new HashMap<Boolean, MutableInteger>();
		voteCount.put(false, new MutableInteger(0));
		voteCount.put(true, new MutableInteger(0));
		
		for (Map.Entry<UUID, Boolean> entry : votes.entrySet()) {
			voteCount.get(entry.getValue()).value++;
		}
		
		boolean winner = false;
		
		if (voteCount.get(true).value == voteCount.get(false).value) {
			TownyElections.sendNationMessage(nation, TownyElections.getTranslatedMessage("no-winner"));
			return false;
		}
		
		if (voteCount.get(true).value > voteCount.get(false).value) {
			winner = true;
		}
		
		String msg = "";
		
		switch (type) {
		case OPEN:
			nation.setOpen(winner);
			TownyUniverse.getInstance().getDataSource().saveNation(nation);
			msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%player%", String.valueOf(winner));
			TownyElections.sendNationSubtitle(nation, msg);
			break;
		case PUBLIC:
			nation.setPublic(winner);
			TownyUniverse.getInstance().getDataSource().saveNation(nation);
			msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%option%", String.valueOf(winner));
			TownyElections.sendNationSubtitle(nation, msg);
			break;
		case NEUTRAL:
			try {
				nation.setNeutral(winner);
				TownyUniverse.getInstance().getDataSource().saveNation(nation);
				msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%option%", String.valueOf(winner));
				TownyElections.sendNationSubtitle(nation, msg);
			} catch (TownyException e) {
				e.printStackTrace();
			}
			break;
 		}
		
		return false;
	}
	
	public void addVote(UUID player, boolean vote) {
		if (votes.containsKey(player)) return;
		votes.put(player, vote);
	}
	
	public void removeVote(UUID player) {
		if (!votes.containsKey(player)) return;
		votes.remove(player);
	}
	
	public boolean hasVoted(UUID player) {
		return votes.containsKey(player);
	}
	
	public int getVotesCount() {
		return votes.size();
	}
	
	public Map<UUID, Boolean> getVotes() {
		return votes;
	}

	public long getEndTime() {
		return endTime;
	}

	public Nation getNation() {
		return nation;
	}
	
	public boolean getWinner() {
		return winner;
	}
	
	public String getType() {
		return type;
	}

}
