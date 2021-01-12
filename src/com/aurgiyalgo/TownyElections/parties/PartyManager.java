package com.aurgiyalgo.TownyElections.parties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.data.DataHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PartyManager {

	private List<Party> parties;
	private DataHandler dataHandler;
	private Gson gson;

	public PartyManager() {
		parties = new ArrayList<Party>();
		dataHandler = new DataHandler(TownyElections.getInstance().getDataFolder(), "parties.json");
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	}

	public void addParty(Party party) {
		if (!parties.contains(party))
			parties.add(party);
	}

	public void removeParty(Party party) {
		parties.remove(party);
	}

	public void removeParty(String partyName) {
		Iterator<Party> iterator = parties.iterator();
		while (iterator.hasNext()) {
			Party party = iterator.next();
			if (!party.getName().equals(partyName))
				continue;
			iterator.remove();
			return;
		}
	}

	public List<TownParty> getTownParties() {
		List<TownParty> townParties = new ArrayList<TownParty>();
		for (Party party : parties) {
			if (party instanceof TownParty)
				townParties.add((TownParty) party);
		}
		return null;
	}

	public List<NationParty> getNationParties() {
		List<NationParty> nationParties = new ArrayList<NationParty>();
		for (Party party : parties) {
			if (party instanceof NationParty)
				nationParties.add((NationParty) party);
		}
		return null;
	}


	public List<TownParty> getPartiesForTown(String town) {
		List<TownParty> townParties = new ArrayList<TownParty>();
		for (Party party : parties) {
			if (!(party instanceof TownParty))
				continue;
			if (!((TownParty) party).getTown().getName().equals(town))
				continue;
			townParties.add((TownParty) party);
		}
		return townParties;
	}
	
	public List<NationParty> getPartiesForNation(String nation) {
		List<NationParty> nationParties = new ArrayList<NationParty>();
		for (Party party : parties) {
			if (!(party instanceof NationParty)) continue;
			if (!((NationParty) party).getNation().getName().equals(nation)) continue;
			nationParties.add((NationParty) party);
		}
		return nationParties;
	}
	
	public TownParty getPlayerTownParty(UUID player) {
		for (Party party : parties) {
			if (!(party instanceof TownParty)) continue;
			if (party.members.contains(player)) return (TownParty) party;
		}
		return null;
	}
	
	public NationParty getPlayerNationParty(UUID player) {
		for (Party party : parties) {
			if (!(party instanceof NationParty)) continue;
			if (party.members.contains(player)) return (NationParty) party;
		}
		return null;
	}

	public void loadData() {
		List<JSONObject> jsonArray = dataHandler.getDataList("parties");
		if (jsonArray == null)
			return;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject currentObject = jsonArray.get(i);
			Party party = gson.fromJson(currentObject.toJSONString(),
					Party.PartyType.getPartyType(currentObject.get("partyType").toString()).getClassType());
			party.setup();
			parties.add(party);
		}
	}

	public void saveData() {
		List<JSONObject> jsonArray = new ArrayList<JSONObject>();
		for (Party party : parties) {
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(gson.toJson(party));
				jsonArray.add(jsonObject);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		dataHandler.addDataList("parties", jsonArray);
		
		dataHandler.saveData();
	}

}
