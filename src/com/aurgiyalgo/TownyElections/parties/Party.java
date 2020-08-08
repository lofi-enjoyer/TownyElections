package com.aurgiyalgo.TownyElections.parties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
	protected String partyType;
	@Expose
	protected UUID territory;
	
	public Party(String name, UUID leader, PartyType partyType) {
		members = new ArrayList<UUID>();
		
		this.name = name;
		this.leader = leader;
		this.partyType = partyType.type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getLeader() {
		return leader;
	}

	public void setLeader(UUID leader) {
		this.leader = leader;
	}
	
	public void addMember(UUID member) {
		if (members.contains(member)) return;
		members.add(member);
	}
	
	public void removeMember(UUID member) {
		members.remove(member);
	}

	public List<UUID> getMembers() {
		return members;
	}

	public void setMembers(List<UUID> members) {
		this.members = members;
	}
	
	public PartyType getType() {
		return PartyType.getPartyType(partyType);
	}

}
