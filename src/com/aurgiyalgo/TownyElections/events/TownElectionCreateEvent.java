package com.aurgiyalgo.TownyElections.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.aurgiyalgo.TownyElections.elections.TownElection;

public class TownElectionCreateEvent extends Event implements Cancellable {
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	private boolean isCanceled = false;
	
	private TownElection election;

	public TownElectionCreateEvent(TownElection election) {
		this.election = election;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlersList() {
		return HANDLERS;
	}
	
	public TownElection getElection() {
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
