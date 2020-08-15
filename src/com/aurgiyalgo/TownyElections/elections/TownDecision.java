package com.aurgiyalgo.TownyElections.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.TownyElections.MutableInteger;
import com.google.gson.annotations.Expose;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;

public class TownDecision {
	
	public static final String FIRE = "FIRE";
	public static final String EXPLOSIONS = "EXPLOSIONS";
	public static final String PUBLIC = "PUBLIC";
	public static final String PVP = "PVP";
	public static final String OPEN = "OPEN";
	
	@Expose
	private Map<UUID, Boolean> votes;
	@Expose
	private UUID townUuid;
	private Town town;
	@Expose
	private long endTime;
	@Expose
	private String type;
	private boolean winner;
	
	public TownDecision(Town town, long endTime, String type) {
		this.town = town;
		this.endTime = endTime;
		this.type = type;
		votes = new HashMap<UUID, Boolean>();
		townUuid = town.getUuid();
	}
	
	public void setup() {
		try {
			town = TownyUniverse.getInstance().getDataSource().getTown(townUuid);
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
	}
	
	public boolean finishDecision() {
		if (votes.isEmpty()) {
			TownyElections.sendTownMessage(town, TownyElections.getTranslatedMessage("no-winner"));
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
			TownyElections.sendTownMessage(town, TownyElections.getTranslatedMessage("no-winner"));
			return false;
		}
		
		if (voteCount.get(true).value > voteCount.get(false).value) {
			winner = true;
		}
		
		String msg = "";
		
		switch (type) {
		case EXPLOSIONS:
			town.setBANG(winner);
			TownyUniverse.getInstance().getDataSource().saveTown(town);
			msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%player%", String.valueOf(winner));
			TownyElections.sendTownSubtitle(town, msg);
			break;
		case FIRE:
			town.setFire(winner);
			TownyUniverse.getInstance().getDataSource().saveTown(town);
			msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%player%", String.valueOf(winner));
			TownyElections.sendTownSubtitle(town, msg);
			break;
		case OPEN:
			town.setOpen(winner);
			TownyUniverse.getInstance().getDataSource().saveTown(town);
			msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%player%", String.valueOf(winner));
			TownyElections.sendTownSubtitle(town, msg);
			break;
		case PUBLIC:
			town.setPublic(winner);
			TownyUniverse.getInstance().getDataSource().saveTown(town);
			msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%option%", String.valueOf(winner));
			TownyElections.sendTownSubtitle(town, msg);
			break;
		case PVP:
			town.setPVP(winner);
			TownyUniverse.getInstance().getDataSource().saveTown(town);
			msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%option%", String.valueOf(winner));
			TownyElections.sendTownSubtitle(town, msg);
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

	public Town getTown() {
		return town;
	}
	
	public boolean getWinner() {
		return winner;
	}
	
	public String getType() {
		return type;
	}

}
