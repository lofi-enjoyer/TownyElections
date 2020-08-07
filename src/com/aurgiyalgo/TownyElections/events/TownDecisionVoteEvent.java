package com.aurgiyalgo.TownyElections.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.aurgiyalgo.TownyElections.elections.TownDecision;

public class TownDecisionVoteEvent extends Event implements Cancellable {
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	private boolean isCanceled = false;
	
	private TownDecision election;

	public TownDecisionVoteEvent(TownDecision election) {
		this.election = election;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlersList() {
		return HANDLERS;
	}
	
	public TownDecision getElection() {
		return election;
	}

	@Override
	public boolean isCancelled() {
		return isCanceled;
	}

	@Override
	public void setCancelled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

}
