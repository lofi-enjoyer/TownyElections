package me.lofienjoyer.TownyElections.parties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.lofienjoyer.TownyElections.TownyElections;
import com.google.gson.annotations.Expose;

public abstract class Party {
	
	public static enum PartyType {
		TOWN("TOWN", TownParty.class), NATION("NATION", NationParty.class);
		
		private static Map<String, PartyType> lookup = new HashMap<String, PartyType>();
		
		static {
			for (PartyType pt : values()) {
				lookup.put(pt.type, pt);
			}
		}
		
		private String type;
		private Class<? extends Party> classType;
		
		private PartyType(String type, Class<? extends Party> classType) {
			this.type = type;
			this.classType = classType;
		}
		
		public static PartyType getPartyType(String type) {
			return lookup.get(type);
		}
		
		public Class<? extends Party> getClassType() {
			return classType;
		}
	}

	@Expose
	protected String name;
	@Expose
	protected UUID leader;
	@Expose
	protected List<UUID> members;
	@Expose
	protected List<UUID> assistants;
	@Expose
	protected String partyType;
	@Expose
	protected UUID territory;
	
	protected List<UUID> invites;
	
	public Party(String name, UUID leader, PartyType partyType, UUID territory) {
		members = new ArrayList<UUID>();
		assistants = new ArrayList<UUID>();
		invites = new ArrayList<UUID>();
		
		this.name = name;
		this.leader = leader;
		this.partyType = partyType.type;
		this.territory = territory;
		
		members.add(leader);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLeader(UUID leader) {
		this.leader = leader;
	}
	
	public void addMember(UUID member) {
		if (members.contains(member)) return;
		members.add(member);
	}
	
	public void removeMember(UUID member) {
		if (members.size() <= 1) {
			TownyElections.getInstance().getPartyManager().removeParty(this);
			return;
		}
		members.remove(member);
		assistants.remove(member);
	}
	
	public void addAssistant(UUID member) {
		if (!members.contains(member)) return;
		if (assistants.contains(member)) return;
		assistants.add(member);
	}
	
	public void removeAssistant(UUID member) {
		if (!members.contains(member)) return;
		if (!assistants.contains(member)) return;
		assistants.remove(member);
	}

	public void setMembers(List<UUID> members) {
		this.members = members;
	}
	
	public boolean isAssistant(UUID player) {
		if (!members.contains(player)) return false;
		
		return assistants.contains(player);
	}
	
	public void addInvite(UUID player) {
		if (!invites.contains(player)) invites.add(player);
	}
	
	public void removeInvite(UUID player) {
		invites.remove(player);
	}
	
	public boolean isInvited(UUID player) {
		return invites.contains(player);
	}
	
	public abstract void setup();
	
	public PartyType getType() {
		return PartyType.getPartyType(partyType);
	}

	public String getName() {
		return name;
	}

	public UUID getLeader() {
		return leader;
	}

	public List<UUID> getMembers() {
		return members;
	}

	public List<UUID> getAssistants() {
		return assistants;
	}

}
