package me.lofienjoyer.TownyElections.elections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.lofienjoyer.TownyElections.TownyElections;
import com.google.gson.annotations.Expose;

public abstract class Election {

	@Expose
	protected final Map<UUID, String> votes;

	@Expose
	private final long endTime;

	@Expose
	protected UUID territoryUuid;

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

	public Map<UUID, String> getVotes() {
		return votes;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getWinner() {
		return winner;
	}

}
