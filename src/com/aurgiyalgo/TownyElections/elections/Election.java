package com.aurgiyalgo.TownyElections.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.annotations.Expose;

import lombok.Getter;

public abstract class Election {
	
	@Getter
	@Expose
	protected Map<UUID, String> votes;
	@Getter
	@Expose
	protected long endTime;
	@Expose
	protected UUID territoryUuid;
	
	@Getter
	protected String winner;
	
	public Election(long endTime) {
		this.endTime = endTime;
		
		votes = new HashMap<UUID, String>();
	}
	
	public abstract void setup();
	
	public abstract void addVote(UUID player, String candidate);
	
	public abstract void removeVote(UUID player);
	
	public abstract String finishElection();
	
	public boolean hasVoted(UUID player) {
		return votes.containsKey(player);
	}
	
	public int getVotesCount() {
		return votes.size();
	}

}
