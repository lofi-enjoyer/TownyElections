package com.aurgiyalgo.TownyElections.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.google.gson.annotations.Expose;

import lombok.Getter;

public abstract class Election {
	
	@Getter
	@Expose
	protected final Map<UUID, String> votes;

	@Getter
	@Expose
	private final long endTime;

	@Expose
	protected UUID territoryUuid;
	
	@Getter
	protected String winner;

	protected TownyElections instance;
	
	public Election(long endTime) {
		this.instance = TownyElections.getInstance();

		this.endTime = endTime;
		
		votes = new HashMap<>();
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
